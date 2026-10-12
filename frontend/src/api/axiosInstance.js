import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || (import.meta.env.DEV ? 'http://localhost:8082' : ''),
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('wallet_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const requestUrl = error.config?.url || ''
    const isAuthRequest = requestUrl.includes('/auth/login') || requestUrl.includes('/users')
    const currentPath = typeof window !== 'undefined' ? window.location.pathname : ''
    const isAuthPage = currentPath === '/login' || currentPath === '/register'

    if (error.response?.status === 401 && !isAuthRequest && !isAuthPage) {
      localStorage.removeItem('wallet_token')
      localStorage.removeItem('wallet_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api

