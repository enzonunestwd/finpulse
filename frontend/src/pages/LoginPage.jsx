import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { TrendingUp, Eye, EyeOff, AlertCircle } from 'lucide-react'

export default function LoginPage() {
  const [modo, setModo] = useState('login') // 'login' | 'register'
  const [showSenha, setShowSenha] = useState(false)
  const [loading, setLoading] = useState(false)
  const [erro, setErro] = useState('')
  const [form, setForm] = useState({ nome: '', email: '', senha: '' })

  const { login, register } = useAuth()
  const navigate = useNavigate()

  function handleChange(e) {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
    setErro('')
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setLoading(true)
    setErro('')

    try {
      if (modo === 'login') {
        await login(form.email, form.senha)
      } else {
        await register(form.nome, form.email, form.senha)
      }
      navigate('/dashboard')
    } catch (err) {
      setErro(
        err.response?.data?.mensagem ||
        (modo === 'login' ? 'E-mail ou senha inválidos.' : 'Erro ao criar conta. Tente novamente.')
      )
    } finally {
      setLoading(false)
    }
  }

  function trocarModo() {
    setModo(m => m === 'login' ? 'register' : 'login')
    setErro('')
    setForm({ nome: '', email: '', senha: '' })
  }

  return (
    <div className="min-h-screen bg-base flex">

      {/* ── Painel esquerdo (branding) ── */}
      <div className="hidden lg:flex flex-col justify-between w-1/2 bg-surface border-r border-border p-12">
        <div className="flex items-center gap-2">
          <TrendingUp size={24} className="text-accent" />
          <span className="text-2xl font-bold">
            Fin<span className="text-accent">Pulse</span>
          </span>
        </div>

        <div>
          <h1 className="text-4xl font-bold text-text-primary leading-tight mb-4">
            Controle seu dinheiro<br />
            <span className="text-accent">com precisão.</span>
          </h1>
          <p className="text-text-secondary text-lg leading-relaxed">
            Registre receitas e despesas, acompanhe suas metas e visualize seus gastos com gráficos em tempo real.
          </p>

          {/* Stats decorativas */}
          <div className="grid grid-cols-2 gap-4 mt-10">
            {[
              { label: 'Categorias', valor: 'Ilimitadas' },
              { label: 'Contas', valor: 'Múltiplas' },
              { label: 'Relatórios', valor: 'Mensais' },
              { label: 'Metas', valor: 'Personalizadas' },
            ].map(({ label, valor }) => (
              <div key={label} className="bg-base rounded-xl p-4 border border-border">
                <p className="text-accent font-mono font-semibold text-lg">{valor}</p>
                <p className="text-text-secondary text-sm mt-0.5">{label}</p>
              </div>
            ))}
          </div>
        </div>

        <p className="text-text-secondary text-sm">
          © 2025 FinPulse — Desenvolvido por Enzo Nunes
        </p>
      </div>

      {/* ── Painel direito (formulário) ── */}
      <div className="flex-1 flex items-center justify-center p-8">
        <div className="w-full max-w-md">

          {/* Logo mobile */}
          <div className="flex items-center gap-2 mb-8 lg:hidden">
            <TrendingUp size={22} className="text-accent" />
            <span className="text-xl font-bold">
              Fin<span className="text-accent">Pulse</span>
            </span>
          </div>

          <h2 className="text-2xl font-bold text-text-primary mb-1">
            {modo === 'login' ? 'Bem-vindo de volta' : 'Criar conta'}
          </h2>
          <p className="text-text-secondary text-sm mb-8">
            {modo === 'login'
              ? 'Entre com suas credenciais para continuar.'
              : 'Preencha os dados abaixo para começar.'}
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">

            {modo === 'register' && (
              <div>
                <label className="block text-sm font-medium text-text-secondary mb-1.5">
                  Nome completo
                </label>
                <input
                  name="nome"
                  type="text"
                  placeholder="Enzo Nunes"
                  value={form.nome}
                  onChange={handleChange}
                  required
                  className="input"
                />
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-text-secondary mb-1.5">
                E-mail
              </label>
              <input
                name="email"
                type="email"
                placeholder="enzo@email.com"
                value={form.email}
                onChange={handleChange}
                required
                className="input"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-text-secondary mb-1.5">
                Senha
              </label>
              <div className="relative">
                <input
                  name="senha"
                  type={showSenha ? 'text' : 'password'}
                  placeholder="••••••••"
                  value={form.senha}
                  onChange={handleChange}
                  required
                  minLength={6}
                  className="input pr-11"
                />
                <button
                  type="button"
                  onClick={() => setShowSenha(v => !v)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-text-secondary hover:text-text-primary transition-colors"
                >
                  {showSenha ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            {/* Mensagem de erro */}
            {erro && (
              <div className="flex items-center gap-2 bg-danger/10 border border-danger/30 rounded-lg px-4 py-3">
                <AlertCircle size={16} className="text-danger shrink-0" />
                <p className="text-danger text-sm">{erro}</p>
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="btn-primary w-full mt-2 disabled:opacity-60 disabled:cursor-not-allowed"
            >
              {loading
                ? 'Aguarde...'
                : modo === 'login' ? 'Entrar' : 'Criar conta'}
            </button>
          </form>

          <p className="text-center text-sm text-text-secondary mt-6">
            {modo === 'login' ? 'Não tem conta?' : 'Já tem conta?'}{' '}
            <button
              onClick={trocarModo}
              className="text-accent hover:text-accent-dim font-medium transition-colors"
            >
              {modo === 'login' ? 'Criar conta' : 'Entrar'}
            </button>
          </p>
        </div>
      </div>
    </div>
  )
}
