package com.malik.InterviewPilot.razorpay.service;

import com.malik.InterviewPilot.entity.User;
import com.malik.InterviewPilot.razorpay.dto.PaymentVerificationRequest;
import com.malik.InterviewPilot.razorpay.dto.PaymentVerificationResponse;
import com.malik.InterviewPilot.razorpay.entity.OrderStatus;
import com.malik.InterviewPilot.razorpay.entity.PaymentOrder;
import com.malik.InterviewPilot.razorpay.entity.PaymentTransaction;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionStatus;
import com.malik.InterviewPilot.razorpay.entity.TransactionStatus;
import com.malik.InterviewPilot.razorpay.entity.UserSubscription;
import com.malik.InterviewPilot.razorpay.exception.DuplicatePaymentException;
import com.malik.InterviewPilot.razorpay.exception.InvalidPaymentSignatureException;
import com.malik.InterviewPilot.razorpay.repository.OrderRepository;
import com.malik.InterviewPilot.razorpay.repository.PaymentTransactionRepository;
import com.razorpay.RazorpayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PaymentVerificationServiceTest {

    private static final String SECRET = "test_secret_123";

    private OrderRepository orderRepository;
    private PaymentTransactionRepository paymentTransactionRepository;
    private SubscriptionService subscriptionService;
    private RazorpayGateway razorpayGateway;
    private PaymentVerificationService verificationService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        paymentTransactionRepository = mock(PaymentTransactionRepository.class);
        subscriptionService = mock(SubscriptionService.class);
        razorpayGateway = mock(RazorpayGateway.class);
        verificationService = new PaymentVerificationService(
                orderRepository, paymentTransactionRepository, subscriptionService, razorpayGateway);
        ReflectionTestUtils.setField(verificationService, "razorpayKeySecret", SECRET);
    }

    /** Mirrors the SDK's own signing algorithm: HMAC-SHA256("orderId|paymentId", secret), hex-encoded. */
    private static String sign(String orderId, String paymentId) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal((orderId + "|" + paymentId).getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    @Test
    void verifyPayment_activatesSubscription_onValidSignature() throws Exception {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = SubscriptionPlan.builder().planId(2L).planName("Monthly").durationInMonths(1).questionLimit(500).build();
        PaymentOrder order = PaymentOrder.builder()
                .id(10L).user(user).plan(plan).razorpayOrderId("order_1")
                .status(OrderStatus.CREATED).amount(29900L).currency("INR").receipt("r1")
                .build();

        when(paymentTransactionRepository.findByRazorpayPaymentId("pay_1")).thenReturn(Optional.empty());
        when(orderRepository.findByRazorpayOrderIdForUpdate("order_1")).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(razorpayGateway.fetchPayment("pay_1")).thenThrow(new RazorpayException("enrichment unavailable"));

        UserSubscription activated = UserSubscription.builder()
                .user(user).plan(plan).startDate(LocalDate.now()).endDate(LocalDate.now().plusMonths(1))
                .subscriptionStatus(SubscriptionStatus.ACTIVE).remainingQuestionCount(500)
                .build();
        when(subscriptionService.activateSubscription(user, plan)).thenReturn(activated);

        PaymentVerificationRequest request =
                new PaymentVerificationRequest("order_1", "pay_1", sign("order_1", "pay_1"));

        PaymentVerificationResponse response = verificationService.verifyPayment(request);

        assertTrue(response.success());
        assertEquals("Monthly", response.subscription().planName());
        verify(subscriptionService).activateSubscription(user, plan);
        verify(orderRepository).save(argThat(o -> o.getStatus() == OrderStatus.PAID));
    }

    @Test
    void verifyPayment_throwsOnInvalidSignature_andMarksOrderFailed() {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = SubscriptionPlan.builder().planId(2L).build();
        PaymentOrder order = PaymentOrder.builder()
                .id(10L).user(user).plan(plan).razorpayOrderId("order_2").status(OrderStatus.CREATED)
                .build();

        when(paymentTransactionRepository.findByRazorpayPaymentId("pay_2")).thenReturn(Optional.empty());
        when(orderRepository.findByRazorpayOrderIdForUpdate("order_2")).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentVerificationRequest request =
                new PaymentVerificationRequest("order_2", "pay_2", "not-the-real-signature");

        assertThrows(InvalidPaymentSignatureException.class, () -> verificationService.verifyPayment(request));

        verify(orderRepository).save(argThat(o -> o.getStatus() == OrderStatus.FAILED));
        verify(subscriptionService, never()).activateSubscription(any(), any());
    }

    @Test
    void verifyPayment_shortCircuits_whenAlreadySuccessfullyVerified() {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = SubscriptionPlan.builder().planId(2L).planName("Monthly").build();
        PaymentOrder order = PaymentOrder.builder().id(10L).plan(plan).build();
        PaymentTransaction existing = PaymentTransaction.builder()
                .id(50L).user(user).order(order).razorpayPaymentId("pay_3").status(TransactionStatus.SUCCESS)
                .build();

        when(paymentTransactionRepository.findByRazorpayPaymentId("pay_3")).thenReturn(Optional.of(existing));

        UserSubscription activeSub = UserSubscription.builder()
                .user(user).plan(plan).subscriptionStatus(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now()).endDate(LocalDate.now().plusMonths(1)).remainingQuestionCount(500)
                .build();
        when(subscriptionService.findLatestActiveOrThrow(1L, 2L)).thenReturn(activeSub);

        PaymentVerificationRequest request = new PaymentVerificationRequest("order_3", "pay_3", "whatever");
        PaymentVerificationResponse response = verificationService.verifyPayment(request);

        assertTrue(response.success());
        assertEquals("Payment already verified", response.message());
        // Short-circuited before ever needing the order lock.
        verifyNoInteractions(orderRepository);
    }

    @Test
    void verifyPayment_throwsDuplicate_whenOrderAlreadyPaidByDifferentPayment() {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = SubscriptionPlan.builder().planId(2L).build();
        PaymentOrder order = PaymentOrder.builder()
                .id(10L).user(user).plan(plan).razorpayOrderId("order_4").status(OrderStatus.PAID)
                .build();

        when(paymentTransactionRepository.findByRazorpayPaymentId("pay_4")).thenReturn(Optional.empty());
        when(orderRepository.findByRazorpayOrderIdForUpdate("order_4")).thenReturn(Optional.of(order));

        PaymentVerificationRequest request = new PaymentVerificationRequest("order_4", "pay_4", "sig");

        assertThrows(DuplicatePaymentException.class, () -> verificationService.verifyPayment(request));
    }

    @Test
    void verifyPayment_fallsBackToWinner_whenConcurrentInsertLosesTheRace() throws Exception {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = SubscriptionPlan.builder().planId(2L).planName("Monthly").build();
        PaymentOrder order = PaymentOrder.builder()
                .id(10L).user(user).plan(plan).razorpayOrderId("order_5").status(OrderStatus.CREATED)
                .build();
        PaymentTransaction winner = PaymentTransaction.builder()
                .id(999L).user(user).order(order).razorpayPaymentId("pay_5").status(TransactionStatus.SUCCESS)
                .build();

        when(orderRepository.findByRazorpayOrderIdForUpdate("order_5")).thenReturn(Optional.of(order));
        when(razorpayGateway.fetchPayment("pay_5")).thenThrow(new RazorpayException("skip enrichment"));
        when(paymentTransactionRepository.save(any())).thenThrow(new DataIntegrityViolationException("dup key"));
        // Empty for the pre-lock check and the post-lock re-check; the concurrent winner's row
        // only becomes visible on the third lookup, inside savePaymentTransaction's race fallback.
        when(paymentTransactionRepository.findByRazorpayPaymentId("pay_5"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(winner));

        UserSubscription activeSub = UserSubscription.builder()
                .user(user).plan(plan).subscriptionStatus(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now()).endDate(LocalDate.now().plusMonths(1)).remainingQuestionCount(500)
                .build();
        when(subscriptionService.findLatestActiveOrThrow(1L, 2L)).thenReturn(activeSub);

        PaymentVerificationRequest request =
                new PaymentVerificationRequest("order_5", "pay_5", sign("order_5", "pay_5"));
        PaymentVerificationResponse response = verificationService.verifyPayment(request);

        assertTrue(response.success());
        assertEquals("Payment already verified", response.message());
        verify(subscriptionService, never()).activateSubscription(any(), any());
    }
}
