import { useState, useEffect } from 'react'
import api from '../services/api'
import { Plus, Trash2, ArrowUpCircle, ArrowDownCircle } from 'lucide-react'

function formatMoney(valor) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor || 0)
}

function formatDate(date) {
  return new Date(date + 'T00:00:00').toLocaleDateString('pt-BR')
}

export default function TransactionsPage() {
  const [transactions, setTransactions] = useState([])
  const [accounts, setAccounts] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({
    descricao: '', valor: '', tipo: 'DESPESA',
    dataTransacao: new Date().toISOString().split('T')[0],
    accountId: '', categoryId: ''
  })

  useEffect(() => {
    Promise.all([
      api.get('/transactions'),
      api.get('/accounts'),
      api.get('/categories'),
    ]).then(([t, a, c]) => {
      setTransactions(t.data)
      setAccounts(a.data)
      setCategories(c.data)
    }).finally(() => setLoading(false))
  }, [])

  const categoriasFiltradas = categories.filter(c => c.tipo === form.tipo)

  async function handleSubmit(e) {
    e.preventDefault()
    try {
      const { data } = await api.post('/transactions', {
        ...form,
        valor: parseFloat(form.valor),
        accountId: parseInt(form.accountId),
        categoryId: parseInt(form.categoryId),
      })
      setTransactions(prev => [data, ...prev])
      setShowForm(false)
      setForm({ descricao: '', valor: '', tipo: 'DESPESA', dataTransacao: new Date().toISOString().split('T')[0], accountId: '', categoryId: '' })
    } catch (err) {
      alert(err.response?.data?.mensagem || 'Erro ao salvar transação.')
    }
  }

  async function handleDelete(id) {
    if (!confirm('Remover esta transação?')) return
    await api.delete(`/transactions/${id}`)
    setTransactions(prev => prev.filter(t => t.id !== id))
  }

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="w-8 h-8 border-2 border-accent border-t-transparent rounded-full animate-spin" />
    </div>
  )

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">Transações</h1>
          <p className="text-text-secondary text-sm mt-1">Registre receitas e despesas</p>
        </div>
        <button onClick={() => setShowForm(v => !v)} className="btn-primary flex items-center gap-2">
          <Plus size={18} />
          Nova transação
        </button>
      </div>

      {/* Formulário */}
      {showForm && (
        <div className="card">
          <h2 className="text-base font-semibold text-text-primary mb-4">Nova transação</h2>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">

            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Tipo</label>
              <select
                value={form.tipo}
                onChange={e => setForm(p => ({ ...p, tipo: e.target.value, categoryId: '' }))}
                className="input"
              >
                <option value="DESPESA">Despesa</option>
                <option value="RECEITA">Receita</option>
              </select>
            </div>

            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Descrição</label>
              <input
                value={form.descricao}
                onChange={e => setForm(p => ({ ...p, descricao: e.target.value }))}
                placeholder="Ex: Mercado"
                required className="input"
              />
            </div>

            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Valor (R$)</label>
              <input
                type="number" step="0.01" min="0.01"
                value={form.valor}
                onChange={e => setForm(p => ({ ...p, valor: e.target.value }))}
                placeholder="0,00"
                required className="input"
              />
            </div>

            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Data</label>
              <input
                type="date"
                value={form.dataTransacao}
                onChange={e => setForm(p => ({ ...p, dataTransacao: e.target.value }))}
                required className="input"
              />
            </div>

            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Conta</label>
              <select
                value={form.accountId}
                onChange={e => setForm(p => ({ ...p, accountId: e.target.value }))}
                required className="input"
              >
                <option value="">Selecione a conta</option>
                {accounts.map(a => (
                  <option key={a.id} value={a.id}>{a.nome}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Categoria</label>
              <select
                value={form.categoryId}
                onChange={e => setForm(p => ({ ...p, categoryId: e.target.value }))}
                required className="input"
              >
                <option value="">Selecione a categoria</option>
                {categoriasFiltradas.map(c => (
                  <option key={c.id} value={c.id}>{c.nome}</option>
                ))}
              </select>
            </div>

            <div className="md:col-span-2 flex gap-3 justify-end">
              <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">
                Cancelar
              </button>
              <button type="submit" className="btn-primary">
                Salvar transação
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Lista de transações */}
      <div className="card p-0 overflow-hidden">
        {transactions.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-16 text-text-secondary">
            <ArrowUpCircle size={40} className="mb-3 opacity-30" />
            <p className="text-sm">Nenhuma transação registrada ainda.</p>
          </div>
        ) : (
          <div className="divide-y divide-border">
            {transactions.map(t => (
              <div key={t.id} className="flex items-center gap-4 px-6 py-4 hover:bg-border/20 transition-colors">
                <div className={`p-2 rounded-lg ${t.tipo === 'RECEITA' ? 'bg-accent/10' : 'bg-danger/10'}`}>
                  {t.tipo === 'RECEITA'
                    ? <ArrowUpCircle size={18} className="text-accent" />
                    : <ArrowDownCircle size={18} className="text-danger" />
                  }
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-text-primary text-sm font-medium truncate">{t.descricao}</p>
                  <p className="text-text-secondary text-xs mt-0.5">
                    {t.categoryNome} · {t.accountNome} · {formatDate(t.dataTransacao)}
                  </p>
                </div>
                <p className={`money text-sm font-semibold shrink-0 ${t.tipo === 'RECEITA' ? 'text-accent' : 'text-danger'}`}>
                  {t.tipo === 'RECEITA' ? '+' : '-'}{formatMoney(t.valor)}
                </p>
                <button
                  onClick={() => handleDelete(t.id)}
                  className="p-1.5 text-text-secondary hover:text-danger transition-colors rounded-lg hover:bg-danger/10"
                >
                  <Trash2 size={15} />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
