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
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RazorpayServiceTest {

    private SubscriptionPlanRepository planRepository;
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private RazorpayGateway razorpayGateway;
    private SubscriptionService subscriptionService;
    private RazorpayService razorpayService;

    @BeforeEach
    void setUp() {
        planRepository = mock(SubscriptionPlanRepository.class);
        userRepository = mock(UserRepository.class);
        orderRepository = mock(OrderRepository.class);
        razorpayGateway = mock(RazorpayGateway.class);
        subscriptionService = mock(SubscriptionService.class);
        razorpayService = new RazorpayService(planRepository, userRepository, orderRepository, razorpayGateway, subscriptionService);
        ReflectionTestUtils.setField(razorpayService, "razorpayKeyId", "rzp_test_key");
        ReflectionTestUtils.setField(razorpayService, "pendingOrderReuseMinutes", 15L);
    }

    private SubscriptionPlan activePremiumPlan() {
        return SubscriptionPlan.builder()
                .planId(2L)
                .planName("Premium")
                .price(new BigDecimal("299.00"))
                .durationInMonths(12)
                .questionLimit(2000)
                .status(PlanStatus.ACTIVE)
                .build();
    }

    private SubscriptionPlan freePlan() {
        return SubscriptionPlan.builder()
                .planId(1L)
                .planName("Free")
                .price(new BigDecimal("0.00"))
                .durationInMonths(12)
                .questionLimit(50)
                .status(PlanStatus.ACTIVE)
                .build();
    }

    @Test
    void createOrder_throwsWhenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> razorpayService.createOrder(new CreateOrderRequest(1L, 2L)));
    }

    @Test
    void createOrder_throwsWhenPlanMissingOrInactive() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(planRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(SubscriptionPlanNotFoundException.class,
                () -> razorpayService.createOrder(new CreateOrderRequest(1L, 2L)));
    }

    @Test
    void createOrder_activatesImmediately_whenPlanIsFree() {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = freePlan();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        CreateOrderResponse response = razorpayService.createOrder(new CreateOrderRequest(1L, 1L));

        assertFalse(response.paymentRequired());
        assertEquals("Free", response.planName());
        verify(subscriptionService).activateSubscription(user, plan);
        verifyNoInteractions(razorpayGateway, orderRepository);
    }

    @Test
    void createOrder_reusesFreshPendingOrder_insteadOfCallingRazorpayAgain() {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = activePremiumPlan();
        PaymentOrder existingOrder = PaymentOrder.builder()
                .id(10L).user(user).plan(plan)
                .razorpayOrderId("order_existing").amount(29900L).currency("INR")
                .status(OrderStatus.CREATED).receipt("rcpt_1")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(planRepository.findById(2L)).thenReturn(Optional.of(plan));
        when(orderRepository.findFirstByUserIdAndPlanPlanIdAndStatusAndCreatedAtAfterOrderByCreatedAtDesc(
                eq(1L), eq(2L), eq(OrderStatus.CREATED), any())).thenReturn(Optional.of(existingOrder));

        CreateOrderResponse response = razorpayService.createOrder(new CreateOrderRequest(1L, 2L));

        assertTrue(response.paymentRequired());
        assertEquals("order_existing", response.razorpayOrderId());
        verifyNoInteractions(razorpayGateway);
    }

    @Test
    void createOrder_callsGatewayWithAmountInPaise_andPersistsOrder() throws Exception {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = activePremiumPlan();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(planRepository.findById(2L)).thenReturn(Optional.of(plan));
        when(orderRepository.findFirstByUserIdAndPlanPlanIdAndStatusAndCreatedAtAfterOrderByCreatedAtDesc(
                any(), any(), any(), any())).thenReturn(Optional.empty());

        Order razorpayOrder = mock(Order.class);
        when(razorpayOrder.get("id")).thenReturn("order_abc123");
        when(razorpayGateway.createOrder(any(JSONObject.class))).thenReturn(razorpayOrder);
        when(orderRepository.save(any(PaymentOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderResponse response = razorpayService.createOrder(new CreateOrderRequest(1L, 2L));

        var captor = forClass(JSONObject.class);
        verify(razorpayGateway).createOrder(captor.capture());
        assertEquals(29900L, captor.getValue().getLong("amount")); // 299.00 rupees -> 29900 paise
        assertTrue(response.paymentRequired());
        assertEquals("order_abc123", response.razorpayOrderId());
        assertEquals("rzp_test_key", response.razorpayKeyId());
        assertEquals("Premium", response.planName());
    }

    @Test
    void createOrder_wrapsRazorpayException() throws Exception {
        User user = User.builder().id(1L).build();
        SubscriptionPlan plan = activePremiumPlan();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(planRepository.findById(2L)).thenReturn(Optional.of(plan));
        when(orderRepository.findFirstByUserIdAndPlanPlanIdAndStatusAndCreatedAtAfterOrderByCreatedAtDesc(
                any(), any(), any(), any())).thenReturn(Optional.empty());
        when(razorpayGateway.createOrder(any())).thenThrow(new RazorpayException("network down"));

        assertThrows(RazorpayIntegrationException.class,
                () -> razorpayService.createOrder(new CreateOrderRequest(1L, 2L)));
    }
}
