import { apiClient } from './client.js'

export const login = (email, password) =>
  apiClient.post('/auth/login', { email, password }).then((res) => res.data)

export const register = (name, email, password) =>
  apiClient.post('/auth/register', { name, email, password }).then((res) => res.data)

export const fetchCurrentUser = () => apiClient.get('/users/me').then((res) => res.data)
