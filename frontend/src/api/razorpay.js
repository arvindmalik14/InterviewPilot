import { apiClient } from './client.js'

export const listPlans = () => apiClient.get('/plans').then((res) => res.data)

export const getMySubscriptions = () => apiClient.get('/subscriptions/me').then((res) => res.data)

export const createOrder = (userId, planId) =>
  apiClient.post('/payments/order', { userId, planId }).then((res) => res.data)

export const verifyPayment = (razorpayOrderId, razorpayPaymentId, razorpaySignature) =>
  apiClient
    .post('/payments/verify', { razorpayOrderId, razorpayPaymentId, razorpaySignature })
    .then((res) => res.data)
