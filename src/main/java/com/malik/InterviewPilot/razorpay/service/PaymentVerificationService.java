package com.malik.InterviewPilot.razorpay.service;

import com.malik.InterviewPilot.razorpay.dto.PaymentVerificationRequest;
import com.malik.InterviewPilot.razorpay.dto.PaymentVerificationResponse;
import com.malik.InterviewPilot.razorpay.dto.SubscriptionResponse;
import com.malik.InterviewPilot.razorpay.entity.OrderStatus;
import com.malik.InterviewPilot.razorpay.entity.PaymentOrder;
import com.malik.InterviewPilot.razorpay.entity.PaymentTransaction;
import com.malik.InterviewPilot.razorpay.entity.TransactionStatus;
import com.malik.InterviewPilot.razorpay.entity.UserSubscription;
import com.malik.InterviewPilot.razorpay.exception.DuplicatePaymentException;
import com.malik.InterviewPilot.razorpay.exception.InvalidPaymentSignatureException;
import com.malik.InterviewPilot.razorpay.exception.OrderNotFoundException;
import com.malik.InterviewPilot.razorpay.repository.OrderRepository;
import com.malik.InterviewPilot.razorpay.repository.PaymentTransactionRepository;
import com.razorpay.Payment;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Verifies a Razorpay payment callback, persists the outcome, and activates the
 * subscription on success.
 *
 * <p>Duplicate/concurrent protection is layered:
 * <ol>
 *   <li>An already-{@code SUCCESS} transaction for this payment id short-circuits
 *       immediately — no reprocessing, no re-activation.</li>
 *   <li>The order row is fetched with a pessimistic write lock, so two concurrent
 *       verify calls for the same order can't both pass the checks below at once.</li>
 *   <li>If the order is already {@code PAID} by a <em>different</em> payment id, this
 *       call is rejected as a duplicate.</li>
 *   <li>The {@code razorpay_payment_id} unique constraint is the final backstop: if two
 *       requests still race past all of the above, the loser's insert fails and it
 *       falls back to the winner's already-committed result instead of erroring.</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentVerificationService {

    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentRepository;
    private final SubscriptionService subscriptionService;
    private final RazorpayGateway razorpayGateway;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    public PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request) {
        Optional<PaymentTransaction> existingTxn =
                paymentRepository.findByRazorpayPaymentId(request.razorpayPaymentId());

        if (existingTxn.isPresent() && existingTxn.get().getStatus() == TransactionStatus.SUCCESS) {
            return alreadyVerifiedResponse(existingTxn.get());
        }

        PaymentOrder order = orderRepository.findByRazorpayOrderIdForUpdate(request.razorpayOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + request.razorpayOrderId()));

        // Re-check now that we hold the order's row lock: a concurrent verify call for
        // this exact payment id (e.g. webhook + client redirect firing for one payment)
        // may have committed while this request was waiting to acquire it.
        existingTxn = paymentRepository.findByRazorpayPaymentId(request.razorpayPaymentId());
        if (existingTxn.isPresent() && existingTxn.get().getStatus() == TransactionStatus.SUCCESS) {
            return alreadyVerifiedResponse(existingTxn.get());
        }

        if (order.getStatus() == OrderStatus.PAID) {
            // A different payment id already settled this order before we acquired the lock.
            throw new DuplicatePaymentException("Order " + request.razorpayOrderId() + " has already been paid");
        }

        boolean signatureValid = verifySignature(request);

        PaymentTransaction transaction = existingTxn.orElseGet(() -> PaymentTransaction.builder()
                .user(order.getUser())
                .order(order)
                .razorpayPaymentId(request.razorpayPaymentId())
                .build());
        transaction.setRazorpaySignature(request.razorpaySignature());

        if (!signatureValid) {
            transaction.setStatus(TransactionStatus.FAILED);
            savePaymentTransaction(transaction);
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            throw new InvalidPaymentSignatureException("Payment signature verification failed");
        }

        transaction.setPaymentMethod(fetchPaymentMethodSafely(request.razorpayPaymentId()));
        transaction.setStatus(TransactionStatus.SUCCESS);
        PaymentTransaction saved = savePaymentTransaction(transaction);

        if (saved != transaction) {
            // savePaymentTransaction fell back to a fetched row instead of persisting ours —
            // a concurrent request already completed this exact payment id first.
            return alreadyVerifiedResponse(saved);
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        UserSubscription subscription = subscriptionService.activateSubscription(order.getUser(), order.getPlan());
        return new PaymentVerificationResponse(
                true, "Payment verified and subscription activated", SubscriptionResponse.from(subscription));
    }

    private boolean verifySignature(PaymentVerificationRequest request) {
        JSONObject payload = new JSONObject();
        payload.put("razorpay_order_id", request.razorpayOrderId());
        payload.put("razorpay_payment_id", request.razorpayPaymentId());
        payload.put("razorpay_signature", request.razorpaySignature());

        try {
            return Utils.verifyPaymentSignature(payload, razorpayKeySecret);
        } catch (RazorpayException ex) {
            log.warn("Signature verification threw for order {}: {}", request.razorpayOrderId(), ex.getMessage());
            return false;
        }
    }

    private PaymentTransaction savePaymentTransaction(PaymentTransaction transaction) {
        try {
            return paymentRepository.save(transaction);
        } catch (DataIntegrityViolationException ex) {
            return paymentRepository.findByRazorpayPaymentId(transaction.getRazorpayPaymentId())
                    .orElseThrow(() -> ex);
        }
    }

    private String fetchPaymentMethodSafely(String razorpayPaymentId) {
        try {
            Payment payment = razorpayGateway.fetchPayment(razorpayPaymentId);
            String method = payment.get("method");
            return method != null ? method : "unknown";
        } catch (Exception ex) {
            log.warn("Could not fetch payment method for {}: {}", razorpayPaymentId, ex.getMessage());
            return "unknown";
        }
    }

    private PaymentVerificationResponse alreadyVerifiedResponse(PaymentTransaction txn) {
        UserSubscription subscription = subscriptionService.findLatestActiveOrThrow(
                txn.getUser().getId(), txn.getOrder().getPlan().getPlanId());
        return new PaymentVerificationResponse(true, "Payment already verified", SubscriptionResponse.from(subscription));
    }
}
