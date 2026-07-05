import { createContext, useContext, useState, useCallback } from 'react'
import api from '../services/api'

// AuthContext: disponibiliza o usuário logado e funções de login/logout
// para qualquer componente da aplicação, sem precisar passar por props.
const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    // Inicializa com o usuário salvo no localStorage (persiste entre reloads)
    const saved = localStorage.getItem('finpulse_user')
    return saved ? JSON.parse(saved) : null
  })

  const login = useCallback(async (email, senha) => {
    const { data } = await api.post('/auth/login', { email, senha })
    localStorage.setItem('finpulse_token', data.token)
    localStorage.setItem('finpulse_user', JSON.stringify(data))
    setUser(data)
    return data
  }, [])

  const register = useCallback(async (nome, email, senha) => {
    const { data } = await api.post('/auth/register', { nome, email, senha })
    localStorage.setItem('finpulse_token', data.token)
    localStorage.setItem('finpulse_user', JSON.stringify(data))
    setUser(data)
    return data
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('finpulse_token')
    localStorage.removeItem('finpulse_user')
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

// Hook customizado — em vez de importar useContext + AuthContext em todo lugar,
// só importamos useAuth()
export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth deve ser usado dentro de AuthProvider')
  return ctx
}
