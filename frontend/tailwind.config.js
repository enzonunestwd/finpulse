/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        // Paleta FinPulse
        base: '#0A0E1A',        // fundo principal
        surface: '#111827',     // cards e painéis
        border: '#1E293B',      // bordas e separadores
        accent: '#00D4AA',      // verde-água (cor principal de ação)
        'accent-dim': '#00A88A',// hover do accent
        danger: '#FF6B6B',      // despesas / erros
        'text-primary': '#F8FAFC',
        'text-secondary': '#94A3B8',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        mono: ['JetBrains Mono', 'Fira Code', 'monospace'],
      },
    },
  },
  plugins: [],
}
