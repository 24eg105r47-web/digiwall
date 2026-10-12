import { createContext, useContext, useEffect, useState } from 'react'
import api from '../api/axiosInstance'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const stored = localStorage.getItem('wallet_user')
      return stored ? JSON.parse(stored) : null
    } catch {
      return null
    }
  })
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (user) {
      localStorage.setItem('wallet_user', JSON.stringify(user))
    } else {
      localStorage.removeItem('wallet_user')
    }
  }, [user])

  async function login(email, password) {
    setLoading(true)
    try {
      const cleanEmail = email ? email.trim().toLowerCase() : ''
      const { data } = await api.post('/auth/login', { email: cleanEmail, password })
      
      const loggedInUser = {
        userId: data.userId,
        name: data.name,
        email: data.email,
        role: data.role
      }

      localStorage.setItem('wallet_token', data.token)
      localStorage.setItem('wallet_user', JSON.stringify(loggedInUser))
      setUser(loggedInUser)
      return loggedInUser
    } finally {
      setLoading(false)
    }
  }

  async function register(name, email, password) {
    setLoading(true)
    try {
      const cleanName = name ? name.trim() : ''
      const cleanEmail = email ? email.trim().toLowerCase() : ''

      const { data } = await api.post('/users', {
        name: cleanName,
        email: cleanEmail,
        password,
      })

      const registeredUser = {
        userId: data.userId,
        name: data.name,
        email: data.email,
        role: data.role
      }

      localStorage.setItem('wallet_token', data.token)
      localStorage.setItem('wallet_user', JSON.stringify(registeredUser))
      setUser(registeredUser)
      return registeredUser
    } finally {
      setLoading(false)
    }
  }

  function logout() {
    localStorage.removeItem('wallet_token')
    localStorage.removeItem('wallet_user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, isAdmin: user?.role === 'ADMIN' }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider')
  return ctx
}
