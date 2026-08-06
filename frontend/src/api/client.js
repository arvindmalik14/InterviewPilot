import axios from 'axios'

export const apiClient = axios.create({
  baseURL: '/api',
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('ip_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const message =
      error.response?.data?.message ||
      error.response?.data?.fieldErrors?.[Object.keys(error.response?.data?.fieldErrors || {})[0]] ||
      error.message ||
      'Something went wrong'
    return Promise.reject(new Error(message))
  },
)
