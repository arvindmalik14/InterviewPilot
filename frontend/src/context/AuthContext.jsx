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

  const applySession = async (data) => {
    localStorage.setItem('ip_token', data.token)
    setUser(data.user)
    await refreshActivePlan()
    return data
  }

  const login = async (email, password) => {
    const data = await authApi.login(email, password)
    if (data.requiresPasswordReset) {
      // Store the token so the change-password request can authenticate, but don't
      // treat the session as logged in until the temp password has been replaced.
      localStorage.setItem('ip_token', data.token)
      return data
    }
    return applySession(data)
  }

  const register = (name, email, mobileNumber, password) =>
    authApi.register(name, email, mobileNumber, password)

  const completePasswordChange = (data) => applySession(data)

  const logout = () => {
    localStorage.removeItem('ip_token')
    setUser(null)
    setActivePlan(null)
  }

  return (
    <AuthContext.Provider
      value={{ user, loading, activePlan, refreshActivePlan, login, register, completePasswordChange, logout }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
