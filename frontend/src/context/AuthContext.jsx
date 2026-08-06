import { createContext, useContext, useEffect, useState } from 'react'
import * as authApi from '../api/auth.js'
import { getMySubscriptions } from '../api/razorpay.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [activePlan, setActivePlan] = useState(null)

  const refreshActivePlan = () =>
    getMySubscriptions()
      .then((subs) => setActivePlan(subs.find((s) => s.subscriptionStatus === 'ACTIVE') || null))
      .catch(() => setActivePlan(null))

  useEffect(() => {
    const token = localStorage.getItem('ip_token')
    if (!token) {
      setLoading(false)
      return
    }
    authApi
      .fetchCurrentUser()
      .then((fetchedUser) => {
        setUser(fetchedUser)
        return refreshActivePlan()
      })
      .catch(() => {
        localStorage.removeItem('ip_token')
      })
      .finally(() => setLoading(false))
  }, [])

  const login = async (email, password) => {
    const data = await authApi.login(email, password)
    localStorage.setItem('ip_token', data.token)
    setUser(data.user)
    await refreshActivePlan()
    return data.user
  }

  const register = async (name, email, password) => {
    const data = await authApi.register(name, email, password)
    localStorage.setItem('ip_token', data.token)
    setUser(data.user)
    await refreshActivePlan()
    return data.user
  }

  const logout = () => {
    localStorage.removeItem('ip_token')
    setUser(null)
    setActivePlan(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, activePlan, refreshActivePlan, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
