package com.malik.InterviewPilot.razorpay.service;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RazorpayGatewayImpl implements RazorpayGateway {

    private final RazorpayClient razorpayClient;

    @Override
    @Retryable(
            retryFor = RazorpayException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2))
    public Order createOrder(JSONObject request) throws RazorpayException {
        return razorpayClient.orders.create(request);
    }

    @Override
    public Payment fetchPayment(String razorpayPaymentId) throws RazorpayException {
        return razorpayClient.payments.fetch(razorpayPaymentId);
    }
}
