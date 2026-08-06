import { apiClient } from './client.js'

export const submitFeedback = (payload) => apiClient.post('/feedback', payload).then((res) => res.data)
