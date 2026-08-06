import { apiClient } from './client.js'

export const explainQuestion = (questionId) =>
  apiClient.post('/ai/explain', { questionId }).then((res) => res.data)

export const generateQuestions = (technology, experienceLevel, count) =>
  apiClient.post('/ai/generate-questions', { technology, experienceLevel, count }).then((res) => res.data)

export const analyzeResume = (resumeText) =>
  apiClient.post('/ai/resume-analysis', { resumeText }).then((res) => res.data)

export const reviewCode = (code, language) =>
  apiClient.post('/ai/code-review', { code, language }).then((res) => res.data)
