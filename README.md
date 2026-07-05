# FinPulse 💰

> Aplicação fullstack de controle financeiro pessoal.
> Backend em Java/Spring Boot · Frontend em React · Banco PostgreSQL

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-green)
![React](https://img.shields.io/badge/React-18-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)

## 🚀 Subindo o projeto com Docker (recomendado)

### Pré-requisitos
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando

### Um único comando

```bash
docker compose up --build
```

Aguarde o build (primeira vez demora ~3-5 min). Depois acesse:

| Serviço   | URL                        |
|-----------|----------------------------|
| Frontend  | http://localhost:3000      |
| API       | http://localhost:8080      |
| Banco     | localhost:5432 (finpulse)  |

Para parar tudo:
```bash
docker compose down
```

Para parar E apagar os dados do banco:
```bash
docker compose down -v
```

---

## 🛠️ Rodando sem Docker (desenvolvimento)

### Backend
```bash
cd backend/finpulse-api

# Configure o banco no application.yml
# Crie o banco: CREATE DATABASE finpulse;

./mvnw spring-boot:run
# API em: http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# App em: http://localhost:5173
```

---

## 📁 Estrutura do projeto

```
finpulse/
├── docker-compose.yml          ← sobe tudo com um comando
├── .gitignore
├── backend/
│   └── finpulse-api/
│       ├── Dockerfile
│       ├── pom.xml
│       └── src/
└── frontend/
    ├── Dockerfile
    ├── nginx.conf
    ├── package.json
    └── src/
```

## ✨ Funcionalidades

- Autenticação JWT (cadastro e login)
- Múltiplas contas financeiras (corrente, cartão, poupança)
- Categorias personalizadas de receita e despesa
- Registro de transações com atualização automática de saldo
- Metas financeiras com barra de progresso
- Dashboard com gráficos de resumo mensal

## 🧑‍💻 Autor

**Enzo Nunes Andrade**
- LinkedIn: [linkedin.com/in/enzonunestwd](https://linkedin.com/in/enzonunestwd)
- GitHub: [github.com/enzonunestwd](https://github.com/enzonunestwd)
- E-mail: enzonunestwd@gmail.com
