import { useState, useEffect } from 'react'
import api from '../services/api'
import { Plus, Trash2, Target, PlusCircle } from 'lucide-react'

function formatMoney(valor) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor || 0)
}

export default function GoalsPage() {
  const [goals, setGoals] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [aporteId, setAporteId] = useState(null)
  const [valorAporte, setValorAporte] = useState('')
  const [form, setForm] = useState({ titulo: '', valorObjetivo: '', valorAtual: '', dataLimite: '' })

  useEffect(() => {
    api.get('/goals')
      .then(res => setGoals(res.data))
      .finally(() => setLoading(false))
  }, [])

  async function handleSubmit(e) {
    e.preventDefault()
    const { data } = await api.post('/goals', {
      ...form,
      valorObjetivo: parseFloat(form.valorObjetivo),
      valorAtual: form.valorAtual ? parseFloat(form.valorAtual) : 0,
      dataLimite: form.dataLimite || null,
    })
    setGoals(prev => [...prev, data])
    setShowForm(false)
    setForm({ titulo: '', valorObjetivo: '', valorAtual: '', dataLimite: '' })
  }

  async function handleAporte(id) {
    if (!valorAporte || isNaN(valorAporte)) return
    const { data } = await api.patch(`/goals/${id}/aporte?valor=${valorAporte}`)
    setGoals(prev => prev.map(g => g.id === id ? data : g))
    setAporteId(null)
    setValorAporte('')
  }

  async function handleDelete(id) {
    if (!confirm('Remover esta meta?')) return
    await api.delete(`/goals/${id}`)
    setGoals(prev => prev.filter(g => g.id !== id))
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
          <h1 className="text-2xl font-bold text-text-primary">Metas Financeiras</h1>
          <p className="text-text-secondary text-sm mt-1">Acompanhe seu progresso rumo aos seus objetivos</p>
        </div>
        <button onClick={() => setShowForm(v => !v)} className="btn-primary flex items-center gap-2">
          <Plus size={18} />
          Nova meta
        </button>
      </div>

      {/* Formulário */}
      {showForm && (
        <div className="card">
          <h2 className="text-base font-semibold text-text-primary mb-4">Nova meta</h2>
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="md:col-span-2">
              <label className="block text-sm text-text-secondary mb-1.5">Título da meta</label>
              <input
                value={form.titulo}
                onChange={e => setForm(p => ({ ...p, titulo: e.target.value }))}
                placeholder="Ex: Reserva de emergência"
                required className="input"
              />
            </div>
            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Valor objetivo (R$)</label>
              <input
                type="number" step="0.01" min="0.01"
                value={form.valorObjetivo}
                onChange={e => setForm(p => ({ ...p, valorObjetivo: e.target.value }))}
                placeholder="0,00" required className="input"
              />
            </div>
            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Valor atual (R$)</label>
              <input
                type="number" step="0.01" min="0"
                value={form.valorAtual}
                onChange={e => setForm(p => ({ ...p, valorAtual: e.target.value }))}
                placeholder="0,00" className="input"
              />
            </div>
            <div>
              <label className="block text-sm text-text-secondary mb-1.5">Prazo (opcional)</label>
              <input
                type="date"
                value={form.dataLimite}
                onChange={e => setForm(p => ({ ...p, dataLimite: e.target.value }))}
                className="input"
              />
            </div>
            <div className="flex items-end gap-3">
              <button type="button" onClick={() => setShowForm(false)} className="btn-secondary flex-1">
                Cancelar
              </button>
              <button type="submit" className="btn-primary flex-1">
                Criar meta
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Cards de metas */}
      {goals.length === 0 ? (
        <div className="card flex flex-col items-center justify-center py-16 text-text-secondary">
          <Target size={40} className="mb-3 opacity-30" />
          <p className="text-sm">Nenhuma meta criada ainda.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {goals.map(goal => {
            const progresso = Math.min(Number(goal.progresso), 100)
            return (
              <div key={goal.id} className="card space-y-4">
                <div className="flex items-start justify-between">
                  <div>
                    <p className="text-text-primary font-semibold">{goal.titulo}</p>
                    {goal.dataLimite && (
                      <p className="text-text-secondary text-xs mt-0.5">
                        Prazo: {new Date(goal.dataLimite + 'T00:00:00').toLocaleDateString('pt-BR')}
                      </p>
                    )}
                  </div>
                  <button
                    onClick={() => handleDelete(goal.id)}
                    className="p-1.5 text-text-secondary hover:text-danger transition-colors rounded-lg hover:bg-danger/10"
                  >
                    <Trash2 size={15} />
                  </button>
                </div>

                {/* Barra de progresso */}
                <div>
                  <div className="flex justify-between text-xs text-text-secondary mb-1.5">
                    <span className="money">{formatMoney(goal.valorAtual)}</span>
                    <span className="money">{formatMoney(goal.valorObjetivo)}</span>
                  </div>
                  <div className="h-2 bg-border rounded-full overflow-hidden">
                    <div
                      className="h-full bg-accent rounded-full transition-all duration-500"
                      style={{ width: `${progresso}%` }}
                    />
                  </div>
                  <p className="text-right text-xs text-accent font-semibold mt-1">
                    {progresso.toFixed(1)}%
                  </p>
                </div>

                {/* Aporte */}
                {aporteId === goal.id ? (
                  <div className="flex gap-2">
                    <input
                      type="number" step="0.01" min="0.01"
                      value={valorAporte}
                      onChange={e => setValorAporte(e.target.value)}
                      placeholder="Valor do aporte"
                      className="input text-sm py-2"
                      autoFocus
                    />
                    <button onClick={() => handleAporte(goal.id)} className="btn-primary text-sm py-2 px-3">
                      OK
                    </button>
                    <button onClick={() => { setAporteId(null); setValorAporte('') }} className="btn-secondary text-sm py-2 px-3">
                      ✕
                    </button>
                  </div>
                ) : (
                  <button
                    onClick={() => setAporteId(goal.id)}
                    className="flex items-center gap-2 text-accent text-sm font-medium hover:text-accent-dim transition-colors"
                  >
                    <PlusCircle size={16} />
                    Registrar aporte
                  </button>
                )}
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
