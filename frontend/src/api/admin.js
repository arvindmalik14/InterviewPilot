import { apiClient } from './client.js'

export const createExam = (payload) => apiClient.post('/admin/exams', payload).then((res) => res.data)

export const updateExam = (id, payload) => apiClient.put(`/admin/exams/${id}`, payload).then((res) => res.data)

export const deleteExam = (id) => apiClient.delete(`/admin/exams/${id}`).then((res) => res.data)

export const listAdminQuestions = (examId, page = 0, size = 20) =>
  apiClient.get('/admin/questions', { params: { examId, page, size } }).then((res) => res.data)

export const createQuestion = (payload) => apiClient.post('/admin/questions', payload).then((res) => res.data)

export const updateQuestion = (id, payload) =>
  apiClient.put(`/admin/questions/${id}`, payload).then((res) => res.data)

export const deleteQuestion = (id) => apiClient.delete(`/admin/questions/${id}`).then((res) => res.data)

// ---- AI Technical Questions and Answers ----

export const listAdminAiCategories = () => apiClient.get('/admin/ai/categories').then((res) => res.data)

export const createAiCategory = (payload) => apiClient.post('/admin/ai/categories', payload).then((res) => res.data)

export const updateAiCategory = (id, payload) =>
  apiClient.put(`/admin/ai/categories/${id}`, payload).then((res) => res.data)

export const deleteAiCategory = (id) => apiClient.delete(`/admin/ai/categories/${id}`).then((res) => res.data)

export const listAdminAiQuestions = (page = 0, size = 20) =>
  apiClient.get('/admin/ai/questions', { params: { page, size } }).then((res) => res.data)

export const createAiQuestion = (payload) => apiClient.post('/admin/ai/questions', payload).then((res) => res.data)

export const updateAiQuestion = (id, payload) =>
  apiClient.put(`/admin/ai/questions/${id}`, payload).then((res) => res.data)

export const deleteAiQuestion = (id) => apiClient.delete(`/admin/ai/questions/${id}`).then((res) => res.data)

export const activateAiQuestion = (id) => apiClient.post(`/admin/ai/questions/${id}/activate`).then((res) => res.data)

export const deactivateAiQuestion = (id) =>
  apiClient.post(`/admin/ai/questions/${id}/deactivate`).then((res) => res.data)

export const assignAiQuestionToPlan = (planId, questionId) =>
  apiClient.post(`/admin/ai/plans/${planId}/questions/${questionId}`).then((res) => res.data)

export const removeAiQuestionFromPlan = (planId, questionId) =>
  apiClient.delete(`/admin/ai/plans/${planId}/questions/${questionId}`).then((res) => res.data)

export const listAiQuestionsForPlan = (planId, size = 1000) =>
  apiClient.get(`/admin/ai/plans/${planId}/questions`, { params: { size } }).then((res) => res.data)
