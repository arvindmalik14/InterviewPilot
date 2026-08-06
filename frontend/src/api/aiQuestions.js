import { apiClient } from './client.js'

export const listAiCategories = () => apiClient.get('/ai/categories').then((res) => res.data)

export const listAiQuestions = ({ categoryId, difficulty, search, page = 0, size = 10 } = {}) =>
  apiClient.get('/ai/questions', { params: { categoryId, difficulty, search, page, size } }).then((res) => res.data)

export const getAiQuestion = (id) => apiClient.get(`/ai/questions/${id}`).then((res) => res.data)

export const bookmarkAiQuestion = (id) => apiClient.post(`/ai/questions/${id}/bookmark`).then((res) => res.data)

export const removeAiQuestionBookmark = (id) => apiClient.delete(`/ai/questions/${id}/bookmark`).then((res) => res.data)

export const listAiBookmarks = (page = 0, size = 20) =>
  apiClient.get('/ai/bookmarks', { params: { page, size } }).then((res) => res.data)
