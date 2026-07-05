import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import api from '../services/api'
import {
  PieChart, Pie, Cell, Tooltip, ResponsiveContainer,
  BarChart, Bar, XAxis, YAxis, CartesianGrid
} from 'recharts'
import { TrendingUp, TrendingDown, Wallet, Target } from 'lucide-react'

const CORES = ['#00D4AA', '#3B82F6', '#8B5CF6', '#F59E0B', '#FF6B6B', '#06B6D4']

function formatMoney(valor) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency', currency: 'BRL'
  }).format(valor || 0)
}

function CardResumo({ titulo, valor, icone: Icon, cor, sub }) {
  return (
    <div className="card flex items-start justify-between">
      <div>
        <p className="text-text-secondary text-sm font-medium mb-1">{titulo}</p>
        <p className={`money text-2xl font-bold ${cor}`}>{formatMoney(valor)}</p>
        {sub && <p className="text-text-secondary text-xs mt-1">{sub}</p>}
      </div>
      <div className={`p-3 rounded-xl ${cor === 'text-accent' ? 'bg-accent/10' : cor === 'text-danger' ? 'bg-danger/10' : 'bg-border'}`}>
        <Icon size={20} className={cor} />
      </div>
    </div>
  )
}

export default function DashboardPage() {
  const { user } = useAuth()
  const [dados, setDados] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/reports/dashboard')
      .then(res => setDados(res.data))
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-2 border-accent border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  const despesasPorCategoria = dados?.despesasPorCategoria?.map(d => ({
    name: d.categoria,
    value: Number(d.total)
  })) || []

  const receitasPorCategoria = dados?.receitasPorCategoria?.map(d => ({
    name: d.categoria,
    value: Number(d.total)
  })) || []

  return (
    <div className="space-y-8">

      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-text-primary">
          Olá, {user?.nome?.split(' ')[0]} 👋
        </h1>
        <p className="text-text-secondary mt-1">
          Aqui está seu resumo financeiro do mês atual.
        </p>
      </div>

      {/* Cards de resumo */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
        <CardResumo
          titulo="Total de Receitas"
          valor={dados?.totalReceitas}
          icone={TrendingUp}
          cor="text-accent"
          sub="no mês atual"
        />
        <CardResumo
          titulo="Total de Despesas"
          valor={dados?.totalDespesas}
          icone={TrendingDown}
          cor="text-danger"
          sub="no mês atual"
        />
        <CardResumo
          titulo="Saldo do Período"
          valor={dados?.saldoPeriodo}
          icone={Wallet}
          cor={dados?.saldoPeriodo >= 0 ? 'text-accent' : 'text-danger'}
          sub="receitas − despesas"
        />
        <CardResumo
          titulo="Categorias com gasto"
          valor={despesasPorCategoria.length}
          icone={Target}
          cor="text-text-primary"
          sub="este mês"
        />
      </div>

      {/* Gráficos */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">

        {/* Gráfico de pizza — despesas por categoria */}
        <div className="card">
          <h2 className="text-base font-semibold text-text-primary mb-6">
            Despesas por categoria
          </h2>
          {despesasPorCategoria.length === 0 ? (
            <div className="flex items-center justify-center h-48 text-text-secondary text-sm">
              Nenhuma despesa registrada este mês.
            </div>
          ) : (
            <div className="flex gap-6">
              <ResponsiveContainer width="60%" height={200}>
                <PieChart>
                  <Pie
                    data={despesasPorCategoria}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    innerRadius={55}
                    outerRadius={80}
                    paddingAngle={3}
                  >
                    {despesasPorCategoria.map((_, i) => (
                      <Cell key={i} fill={CORES[i % CORES.length]} />
                    ))}
                  </Pie>
                  <Tooltip
                    formatter={(v) => formatMoney(v)}
                    contentStyle={{ background: '#111827', border: '1px solid #1E293B', borderRadius: 8 }}
                    labelStyle={{ color: '#F8FAFC' }}
                    itemStyle={{ color: '#94A3B8' }}
                  />
                </PieChart>
              </ResponsiveContainer>

              {/* Legenda */}
              <div className="flex flex-col justify-center gap-2 flex-1">
                {despesasPorCategoria.map((d, i) => (
                  <div key={d.name} className="flex items-center gap-2">
                    <div className="w-2.5 h-2.5 rounded-full shrink-0" style={{ background: CORES[i % CORES.length] }} />
                    <span className="text-text-secondary text-xs truncate">{d.name}</span>
                    <span className="text-text-primary text-xs money ml-auto">{formatMoney(d.value)}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Gráfico de barras — receitas por categoria */}
        <div className="card">
          <h2 className="text-base font-semibold text-text-primary mb-6">
            Receitas por categoria
          </h2>
          {receitasPorCategoria.length === 0 ? (
            <div className="flex items-center justify-center h-48 text-text-secondary text-sm">
              Nenhuma receita registrada este mês.
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={receitasPorCategoria} margin={{ top: 0, right: 0, left: -10, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1E293B" />
                <XAxis dataKey="name" tick={{ fill: '#94A3B8', fontSize: 11 }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fill: '#94A3B8', fontSize: 11 }} axisLine={false} tickLine={false} tickFormatter={v => `R$${v}`} />
                <Tooltip
                  formatter={(v) => formatMoney(v)}
                  contentStyle={{ background: '#111827', border: '1px solid #1E293B', borderRadius: 8 }}
                  labelStyle={{ color: '#F8FAFC' }}
                  itemStyle={{ color: '#94A3B8' }}
                />
                <Bar dataKey="value" fill="#00D4AA" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>
    </div>
  )
}
