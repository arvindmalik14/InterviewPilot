import { apiClient } from './client.js'

export const login = (email, password) =>
  apiClient.post('/auth/login', { email, password }).then((res) => res.data)

export const register = (name, email, mobileNumber, password) =>
  apiClient.post('/auth/register', { name, email, mobileNumber, password }).then((res) => res.data)

export const forgotPassword = (email) =>
  apiClient.post('/auth/forgot-password', { email }).then((res) => res.data)

export const changePassword = (email, newPassword, confirmPassword) =>
  apiClient.post('/auth/change-password', { email, newPassword, confirmPassword }).then((res) => res.data)

export const fetchCurrentUser = () => apiClient.get('/users/me').then((res) => res.data)
