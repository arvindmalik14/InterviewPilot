package com.malik.InterviewPilot.razorpay.service;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayException;
import org.json.JSONObject;

/**
 * Thin seam over the Razorpay SDK's concrete client classes so RazorpayService can be
 * unit-tested with a mock instead of hitting the real Razorpay API (or needing a
 * hand-rolled HTTP stub for a third-party SDK's internals).
 */
public interface RazorpayGateway {

    Order createOrder(JSONObject request) throws RazorpayException;

    Payment fetchPayment(String razorpayPaymentId) throws RazorpayException;
}
