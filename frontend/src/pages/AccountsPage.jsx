import { useState, useEffect } from 'react'
import api from '../services/api'
import { Plus, Trash2, Wallet, CreditCard, TrendingUp, Banknote, PiggyBank } from 'lucide-react'

function formatMoney(valor) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor || 0)
}

const tipoConfig = {
  CORRENTE:        { label: 'Conta Corrente',    icon: Wallet,      cor: 'text-accent',   bg: 'bg-accent/10' },
  CARTAO_CREDITO:  { label: 'Cartão de Crédito', icon: CreditCard,  cor: 'text-blue-400', bg: 'bg-blue-400/10' },
  INVESTIMENTO:    { label: 'Investimento',       icon: TrendingUp,  cor: 'text-purple-400', bg: 'bg-purple-400/10' },
  DINHEIRO:        { label: 'Dinheiro',           icon: Banknote,    cor: 'text-yellow-400', bg: 'bg-yellow-400/10' },
  POUPANCA:        { label: 'Poupança',           icon: PiggyBank,   cor: 'text-pink-400', bg: 'bg-pink-400/10' },
}

export default function AccountsPage() {
  const [accounts, setAccounts] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ nome: '', tipo: 'CORRENTE', saldoInicial: '' })

  useEffect(() => {
    api.get('/accounts')
      .then(res => setAccounts(res.data))
      .finally(() => setLoading(false))
  }, [])

  async function handleSubmit(e) {
    e.preventDefault()
    const { data } = await api.post('/accounts', {
      ...form,
      saldoInicial: form.saldoInicial ? parseFloat(form.saldoInicial) : 0,
    })
    setAccounts(prev => [...prev, data])
    setShowForm(false)
    setForm({ nome: '', tipo: 'CORRENTE', saldoInicial: '' })
  }

  async function handleDelete(id) {
    if (!confirm('Remover esta conta? Todas as transações vinculadas serão removidas.')) return
    await api.delete(`/accounts/${id}`)
    setAccounts(prev => prev.filter(a => a.id !== id))
  }

  const saldoTotal = accounts.reduce((acc, a) => acc + Number(a.saldo), 0)

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="w-8 h-8 border-2 border-accent border-t-transparent rounded-full animate-spin" />
    </div>
  )

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Contas</h1>
          <p className="text-text-secondary text-sm mt-1">
            Saldo total:{' '}
            <span className={`money font-semibold ${saldoTotal >= 0 ? 'text-accent' : 'text-danger'}`}>
              {formatMoney(saldoTotal)}
            </span>
          </p>
        </div>
        <button onClick={() => setShowForm(v => !v)} className="btn-primary flex items-center gap-2">
          <Plus size={18} />
          Nova conta
        </button>
      </div>

      {/* Formulário */}
      {showForm && (
        <div className="card">
          <h2 className="text-base font-semibold text-text-primary mb-4">Nova conta</h2>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Nome da conta</label>
              <input
                value={form.nome}
                onChange={e => setForm(p => ({ ...p, nome: e.target.value }))}
                placeholder="Ex: Nubank"
                required className="input"
              />
            </div>
            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Tipo</label>
              <select
                value={form.tipo}
                onChange={e => setForm(p => ({ ...p, tipo: e.target.value }))}
                className="input"
              >
                {Object.entries(tipoConfig).map(([key, { label }]) => (
                  <option key={key} value={key}>{label}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Saldo inicial (R$)</label>
              <input
                type="number" step="0.01"
                value={form.saldoInicial}
                onChange={e => setForm(p => ({ ...p, saldoInicial: e.target.value }))}
                placeholder="0,00" className="input"
              />
            </div>
            <div className="md:col-span-3 flex gap-3 justify-end">
              <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">
                Cancelar
              </button>
              <button type="submit" className="btn-primary">
                Criar conta
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Grid de contas */}
      {accounts.length === 0 ? (
        <div className="card flex flex-col items-center justify-center py-16 text-text-secondary">
          <Wallet size={40} className="mb-3 opacity-30" />
          <p className="text-sm">Nenhuma conta cadastrada ainda.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {accounts.map(account => {
            const config = tipoConfig[account.tipo] || tipoConfig.CORRENTE
            const Icon = config.icon
            return (
              <div key={account.id} className="card flex items-start justify-between">
                <div className="flex items-start gap-4">
                  <div className={`p-3 rounded-xl ${config.bg}`}>
                    <Icon size={20} className={config.cor} />
                  </div>
                  <div>
                    <p className="text-text-primary font-semibold">{account.nome}</p>
                    <p className="text-text-secondary text-xs mt-0.5">{config.label}</p>
                    <p className={`money text-lg font-bold mt-2 ${Number(account.saldo) >= 0 ? 'text-text-primary' : 'text-danger'}`}>
                      {formatMoney(account.saldo)}
                    </p>
                  </div>
                </div>
                <button
                  onClick={() => handleDelete(account.id)}
                  className="p-1.5 text-text-secondary hover:text-danger transition-colors rounded-lg hover:bg-danger/10"
                >
                  <Trash2 size={15} />
                </button>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
