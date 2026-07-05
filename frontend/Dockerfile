# ── STAGE 1: BUILD ────────────────────────────────────────────────────
FROM node:20-alpine AS build

WORKDIR /app

# Copia package.json e instala dependências
COPY package*.json ./
RUN npm install

# Copia o código e faz o build de produção
COPY . .
RUN npm run build
# Isso gera a pasta /app/dist com HTML/CSS/JS otimizados

# ── STAGE 2: SERVE ────────────────────────────────────────────────────
# Usa o Nginx (servidor web leve) para servir os arquivos estáticos
FROM nginx:alpine

# Copia os arquivos buildados para o diretório do Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Configuração do Nginx para suportar React Router (rotas client-side)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
