import { apiClient } from './client.js'

export const startTest = (examId, questionCount) =>
  apiClient.post('/tests/start', { examId, questionCount }).then((res) => res.data)

export const submitTest = (testId, durationSeconds, answers) =>
  apiClient.post(`/tests/${testId}/submit`, { durationSeconds, answers }).then((res) => res.data)

export const stopTest = (testId, durationSeconds) =>
  apiClient.post(`/tests/${testId}/stop`, { durationSeconds }).then((res) => res.data)

export const getTestHistory = () => apiClient.get('/tests/history').then((res) => res.data)
