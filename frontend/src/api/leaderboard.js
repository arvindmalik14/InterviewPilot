import { apiClient } from './client.js'

export const getLeaderboard = () => apiClient.get('/leaderboard').then((res) => res.data)
