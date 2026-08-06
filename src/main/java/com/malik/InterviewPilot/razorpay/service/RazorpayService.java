package com.malik.InterviewPilot.razorpay.service;

import com.malik.InterviewPilot.entity.User;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import com.malik.InterviewPilot.razorpay.dto.CreateOrderRequest;
import com.malik.InterviewPilot.razorpay.dto.CreateOrderResponse;
import com.malik.InterviewPilot.razorpay.entity.OrderStatus;
import com.malik.InterviewPilot.razorpay.entity.PaymentOrder;
import com.malik.InterviewPilot.razorpay.entity.PlanStatus;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import com.malik.InterviewPilot.razorpay.exception.RazorpayIntegrationException;
import com.malik.InterviewPilot.razorpay.exception.SubscriptionPlanNotFoundException;
import com.malik.InterviewPilot.razorpay.repository.OrderRepository;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionPlanRepository;
import com.malik.InterviewPilot.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Order creation orchestration: resolves the plan, reuses a still-fresh pending order
 * for the same user+plan instead of minting a duplicate one (idempotency for
 * double-click/retry), calls Razorpay, and persists the result.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RazorpayService {

    private static final String CURRENCY = "INR";

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final RazorpayGateway razorpayGateway;
    private final SubscriptionService subscriptionService;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.pending-order-reuse-minutes:15}")
    private long pendingOrderReuseMinutes;

    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.userId()));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.planId())
                .filter(p -> p.getStatus() == PlanStatus.ACTIVE)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException("Active plan not found: " + request.planId()));

        if (plan.getPrice().signum() == 0) {
            // Free tier: no payment involved, activate straight away.
            subscriptionService.activateSubscription(user, plan);
            return new CreateOrderResponse(false, null, null, 0L, CURRENCY, plan.getPlanName(), null);
        }

        Optional<PaymentOrder> reusable = findReusablePendingOrder(user.getId(), plan.getPlanId());
        if (reusable.isPresent()) {
            return toResponse(reusable.get(), plan);
        }

        long amountInPaise = toPaise(plan.getPrice());
        String receipt = "rcpt_u" + user.getId() + "_p" + plan.getPlanId() + "_" + System.currentTimeMillis();

        Order razorpayOrder = createRazorpayOrder(amountInPaise, receipt);
        String razorpayOrderId = razorpayOrder.get("id");

        PaymentOrder order = PaymentOrder.builder()
                .user(user)
                .plan(plan)
                .razorpayOrderId(razorpayOrderId)
                .amount(amountInPaise)
                .currency(CURRENCY)
                .status(OrderStatus.CREATED)
                .receipt(receipt)
                .build();
        order = orderRepository.save(order);

        return toResponse(order, plan);
    }

    private Optional<PaymentOrder> findReusablePendingOrder(Long userId, Long planId) {
        Instant reuseWindowStart = Instant.now().minus(pendingOrderReuseMinutes, ChronoUnit.MINUTES);
        return orderRepository.findFirstByUserIdAndPlanPlanIdAndStatusAndCreatedAtAfterOrderByCreatedAtDesc(
                userId, planId, OrderStatus.CREATED, reuseWindowStart);
    }

    private Order createRazorpayOrder(long amountInPaise, String receipt) {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", CURRENCY);
        orderRequest.put("receipt", receipt);
        orderRequest.put("payment_capture", 1);

        try {
            return razorpayGateway.createOrder(orderRequest);
        } catch (RazorpayException ex) {
            throw new RazorpayIntegrationException("Failed to create Razorpay order after retries", ex);
        }
    }

    private static long toPaise(BigDecimal rupees) {
        return rupees.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private CreateOrderResponse toResponse(PaymentOrder order, SubscriptionPlan plan) {
        return new CreateOrderResponse(
                true, order.getRazorpayOrderId(), razorpayKeyId, order.getAmount(), order.getCurrency(),
                plan.getPlanName(), order.getReceipt());
    }
}
