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
