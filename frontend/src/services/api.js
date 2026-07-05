import axios from 'axios'

// Instância do axios com baseURL e interceptors configurados.
// Todos os arquivos de serviço importam ESSE axios, não o padrão.
const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// Interceptor de REQUEST: adiciona o token JWT em toda requisição automaticamente.
// Sem isso, teríamos que passar o token manualmente em cada chamada.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('finpulse_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Interceptor de RESPONSE: se receber 401 (token expirado/inválido),
// limpa o storage e redireciona pro login automaticamente.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('finpulse_token')
      localStorage.removeItem('finpulse_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
