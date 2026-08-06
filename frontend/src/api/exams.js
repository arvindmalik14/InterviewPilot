import { apiClient } from './client.js'

export const listExams = () => apiClient.get('/exams').then((res) => res.data)

export const getExam = (id) => apiClient.get(`/exams/${id}`).then((res) => res.data)

export const listQuestions = (examId, page = 0, size = 10) =>
  apiClient.get('/questions', { params: { examId, page, size } }).then((res) => res.data)
