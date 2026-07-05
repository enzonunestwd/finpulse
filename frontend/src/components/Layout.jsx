import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  LayoutDashboard,
  ArrowLeftRight,
  Target,
  Wallet,
  LogOut,
  TrendingUp
} from 'lucide-react'

const navItems = [
  { to: '/dashboard',    icon: LayoutDashboard, label: 'Dashboard'    },
  { to: '/transactions', icon: ArrowLeftRight,  label: 'Transações'   },
  { to: '/goals',        icon: Target,          label: 'Metas'        },
  { to: '/accounts',     icon: Wallet,          label: 'Contas'       },
]

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="flex h-screen bg-base overflow-hidden">

      {/* ── Sidebar ── */}
      <aside className="w-64 bg-surface border-r border-border flex flex-col shrink-0">

        {/* Logo */}
        <div className="px-6 py-6 border-b border-border">
          <div className="flex items-center gap-2">
            <TrendingUp size={22} className="text-accent" />
            <span className="text-xl font-bold text-text-primary tracking-tight">
              Fin<span className="text-accent">Pulse</span>
            </span>
          </div>
          <p className="text-xs text-text-secondary mt-1">Controle financeiro pessoal</p>
        </div>

        {/* Navegação */}
        <nav className="flex-1 px-3 py-4 space-y-1">
          {navItems.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors duration-150 ${
                  isActive
                    ? 'bg-accent/10 text-accent'
                    : 'text-text-secondary hover:text-text-primary hover:bg-border/50'
                }`
              }
            >
              <Icon size={18} />
              {label}
            </NavLink>
          ))}
        </nav>

        {/* Usuário + logout */}
        <div className="px-3 py-4 border-t border-border">
          <div className="px-3 py-2 mb-1">
            <p className="text-sm font-medium text-text-primary truncate">{user?.nome}</p>
            <p className="text-xs text-text-secondary truncate">{user?.email}</p>
          </div>
          <button
            onClick={handleLogout}
            className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-text-secondary hover:text-danger hover:bg-danger/10 transition-colors duration-150 w-full"
          >
            <LogOut size={18} />
            Sair
          </button>
        </div>
      </aside>

      {/* ── Conteúdo principal ── */}
      <main className="flex-1 overflow-y-auto">
        <div className="p-8">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
