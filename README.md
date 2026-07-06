<div align="center">

<img src="https://img.shields.io/badge/FinPulse-Controle%20Financeiro%20Pessoal-00D4AA?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHBhdGggZmlsbD0id2hpdGUiIGQ9Ik0yMyA2bC00IDQtNC04LTQgMTItNC04LTQgNEgxdjJoMmw0LTQgNCA4IDQtMTIgNCA4IDQtNGgydi0yeiIvPjwvc3ZnPg==" alt="FinPulse"/>

# FinPulse 💰

### Aplicação fullstack de controle financeiro pessoal

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://adoptium.net)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=flat-square&logo=springsecurity)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql)](https://www.postgresql.org)
[![React](https://img.shields.io/badge/React-18-61DAFB?style=flat-square&logo=react)](https://react.dev)
[![Vite](https://img.shields.io/badge/Vite-5-646CFF?style=flat-square&logo=vite)](https://vitejs.dev)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-3-06B6D4?style=flat-square&logo=tailwindcss)](https://tailwindcss.com)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)](https://www.docker.com)

</div>

---

## 📋 Sobre o Projeto

O **FinPulse** é uma aplicação web fullstack de gerenciamento financeiro pessoal, desenvolvida com foco em boas práticas de arquitetura de software e segurança de APIs REST.

O back-end foi construído com **Java 17** e **Spring Boot 3**, seguindo uma arquitetura em camadas (Controller → Service → Repository), com autenticação **stateless via JWT**, isolamento de dados por usuário, e regras de negócio transacionais garantindo consistência total dos dados financeiros.

O front-end foi desenvolvido em **React 18** com **Vite**, utilizando **Tailwind CSS** para estilização com tema escuro estilo fintech, e **Recharts** para visualização de dados em gráficos interativos.

---

## 🖥️ Visão Geral da Interface

### Tela de Login / Cadastro
> Layout split: branding à esquerda, formulário à direita. Autenticação com validação em tempo real e feedback de erro.

<!-- Screenshot: adicione aqui uma imagem da tela de login -->
<!-- ![Login](docs/screenshots/login.png) --><img width="1920" height="862" alt="image" src="https://github.com/user-attachments/assets/d3b5a9ca-4c8b-41a3-b734-db7cd502d0f1" />


---

### Dashboard Principal
> Cards de resumo financeiro + gráfico de pizza (despesas por categoria) + gráfico de barras (receitas por categoria). Dados calculados em tempo real a partir das transações do mês atual.

<!-- Screenshot: adicione aqui uma imagem do dashboard -->
<!-- ![Dashboard](docs/screenshots/dashboard.png) -->

---

### Tela de Transações
> Listagem de todos os lançamentos financeiros com data, valor, conta e categoria. Formulário inline para criação de novas transações com seleção de conta e categoria.

<!-- Screenshot: adicione aqui uma imagem das transações -->
<!-- ![Transações](docs/screenshots/transactions.png) -->

---

### Tela de Metas Financeiras
> Cards de metas com barra de progresso animada, percentual de conclusão e funcionalidade de aporte incremental.

<!-- Screenshot: adicione aqui uma imagem das metas -->
<!-- ![Metas](docs/screenshots/goals.png) -->

---

### Tela de Contas
> Grid de contas financeiras com ícones por tipo (corrente, cartão, investimento, poupança), saldo individual e saldo total consolidado.

<!-- Screenshot: adicione aqui uma imagem das contas -->
<!-- ![Contas](docs/screenshots/accounts.png) -->

---

## ✨ Funcionalidades

- 🔐 **Autenticação JWT stateless** — cadastro, login e proteção de todas as rotas da API
- 🏦 **Múltiplas contas financeiras** — corrente, cartão de crédito, investimento, poupança e dinheiro
- 🏷️ **Categorias personalizadas** — receitas e despesas com cor customizável para os gráficos
- 💸 **Registro de transações** — lançamentos com atualização automática e atômica do saldo da conta
- 🎯 **Metas financeiras** — definição de objetivos com prazo, valor atual e histórico de aportes
- 📊 **Dashboard com gráficos** — resumo mensal, gráfico de pizza por categoria de despesa e gráfico de barras por receita
- 🔒 **Isolamento de dados** — cada usuário acessa exclusivamente seus próprios dados (prevenção de IDOR)

---

## 🏗️ Arquitetura do Projeto

```
finpulse/
├── docker-compose.yml              ← sobe tudo com um único comando
├── README.md
├── backend/
│   └── finpulse-api/
│       ├── Dockerfile
│       ├── pom.xml
│       └── src/main/java/com/enzo/finpulse/
│           ├── controller/        ← endpoints HTTP REST (19 endpoints)
│           ├── service/           ← regras de negócio (@Transactional)
│           ├── repository/        ← acesso ao banco (Spring Data JPA)
│           ├── model/             ← entidades JPA (5 tabelas)
│           ├── dto/               ← objetos de entrada/saída da API
│           ├── security/          ← JWT: geração, filtro, autenticação
│           ├── config/            ← Spring Security, CORS
│           └── exception/         ← tratamento global de erros
└── frontend/
    ├── Dockerfile
    ├── nginx.conf
    └── src/
        ├── pages/                 ← Login, Dashboard, Transações, Metas, Contas
        ├── components/            ← Layout, Sidebar
        ├── context/               ← AuthContext (estado global)
        └── services/              ← axios com interceptors JWT
```

---

## 🛠️ Tecnologias Utilizadas

### Back-end
| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.3 | Framework web e IoC |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 3.x | ORM (mapeamento objeto-relacional) |
| PostgreSQL | 16 | Banco de dados relacional |
| JWT (jjwt) | 0.11.5 | Tokens de autenticação stateless |
| Lombok | latest | Redução de boilerplate |
| Maven | 3.9 | Gerenciamento de dependências |

### Front-end
| Tecnologia | Versão | Uso |
|---|---|---|
| React | 18 | Biblioteca de interface |
| Vite | 5 | Bundler e servidor de desenvolvimento |
| Tailwind CSS | 3 | Estilização utility-first |
| Recharts | 2 | Gráficos interativos |
| Axios | 1.7 | Cliente HTTP com interceptors JWT |
| React Router | 6 | Roteamento com rotas protegidas |
| Lucide React | 0.383 | Biblioteca de ícones |

### DevOps
| Tecnologia | Uso |
|---|---|
| Docker | Containerização da aplicação |
| Docker Compose | Orquestração de múltiplos containers |
| Nginx | Servidor web para o frontend em produção |

---

## 🔑 Decisões Técnicas

### Por que BigDecimal para valores monetários?
`double` e `float` usam representação binária de ponto flutuante que não consegue representar exatamente certos decimais (ex: `0.1 + 0.2 = 0.30000000000000004`). Em sistemas financeiros, esse erro de arredondamento é inaceitável. `BigDecimal` garante precisão exata.

### Por que @Transactional no registro de transações?
Ao registrar um lançamento financeiro, dois registros precisam ser salvos atomicamente: a transação em si e a atualização do saldo da conta. `@Transactional` garante que, se qualquer operação falhar, **ambas são desfeitas** — nunca ficamos com saldo inconsistente no banco.

### Por que DTOs em vez de expor as entidades diretamente?
Três razões: **segurança** (a entidade `User` contém `senhaHash`), **desacoplamento** (mudanças no banco não afetam o contrato com o frontend), e **flexibilidade** (`TransactionResponse` combina dados de `Account` e `Category` em uma única resposta).

### Por que JWT stateless e não sessão?
A API não armazena nenhum estado de sessão no servidor. Cada requisição autentica-se de forma independente via token. Isso permite **escala horizontal** sem sessões compartilhadas ou sticky sessions.

### Prevenção de IDOR
Todos os repositórios usam `findByIdAndUserId` — um recurso só é retornado se pertencer ao usuário autenticado. Mesmo manipulando IDs na URL, um usuário nunca acessa dados de outro.

---

## 🗂️ Endpoints da API

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Cadastro de novo usuário | ❌ |
| POST | `/api/auth/login` | Login → retorna JWT | ❌ |
| GET | `/api/accounts` | Lista contas do usuário | ✅ |
| POST | `/api/accounts` | Cria nova conta | ✅ |
| PUT | `/api/accounts/{id}` | Atualiza conta | ✅ |
| DELETE | `/api/accounts/{id}` | Remove conta | ✅ |
| GET | `/api/categories` | Lista categorias | ✅ |
| POST | `/api/categories` | Cria categoria | ✅ |
| PUT | `/api/categories/{id}` | Atualiza categoria | ✅ |
| DELETE | `/api/categories/{id}` | Remove categoria | ✅ |
| GET | `/api/transactions` | Lista transações | ✅ |
| POST | `/api/transactions` | Registra transação | ✅ |
| DELETE | `/api/transactions/{id}` | Remove transação | ✅ |
| GET | `/api/goals` | Lista metas | ✅ |
| POST | `/api/goals` | Cria meta | ✅ |
| PUT | `/api/goals/{id}` | Atualiza meta | ✅ |
| PATCH | `/api/goals/{id}/aporte` | Registra aporte | ✅ |
| DELETE | `/api/goals/{id}` | Remove meta | ✅ |
| GET | `/api/reports/dashboard` | Resumo financeiro do período | ✅ |

---

## 🐳 Rodando com Docker (Recomendado)

Esta é a forma mais simples de rodar o projeto. Com Docker Compose, toda a aplicação — backend, frontend e banco de dados PostgreSQL — é iniciada com um único comando.

### Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando

### 1. Clone o repositório

```bash
git clone https://github.com/enzonunestwd/finpulse.git
cd finpulse
```

### 2. Suba todos os containers

```bash
docker compose up --build
```

> ⏳ A primeira execução demora entre 3 e 5 minutos (build do backend Maven + download das imagens base). As execuções seguintes são muito mais rápidas pois o cache é reutilizado.

### 3. Acesse a aplicação

| Serviço | URL |
|---|---|
| 🖥️ **Frontend** | http://localhost:3000 |
| ⚙️ **API** | http://localhost:8080 |
| 🗄️ **Banco** | localhost:5432 (banco: `finpulse`, usuário: `postgres`, senha: `finpulse123`) |

### 4. Cadastre-se e comece a usar

1. Acesse http://localhost:3000
2. Clique em **"Criar conta"** e preencha seus dados
3. Vá em **Contas** → crie pelo menos uma conta financeira
4. Vá em **Categorias** (via API ou Postman) → crie categorias de receita e despesa
5. Registre transações e veja o **Dashboard** atualizar em tempo real

### Comandos úteis

```bash
# Parar todos os containers
docker compose down

# Parar e apagar os dados do banco
docker compose down -v

# Ver logs do backend em tempo real
docker compose logs -f backend

# Ver logs do frontend
docker compose logs -f frontend
```

---

## 🧪 Testando a API com Postman

### 1. Criar conta

```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "nome": "Seu Nome",
  "email": "seuemail@email.com",
  "senha": "suasenha123"
}
```

### 2. Login (guarde o token retornado)

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "seuemail@email.com",
  "senha": "suasenha123"
}
```

### 3. Criar categoria (use o token no header)

```http
POST http://localhost:8080/api/categories
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json

{
  "nome": "Salário",
  "tipo": "RECEITA",
  "cor": "#00D4AA"
}
```

---

## 🔧 Rodando sem Docker (Desenvolvimento)

### Backend

```bash
cd backend/finpulse-api

# Pré-requisitos: Java 17+, Maven 3.8+, PostgreSQL rodando
# Crie o banco: CREATE DATABASE finpulse;
# Configure as credenciais em src/main/resources/application.yml

./mvnw spring-boot:run
# API disponível em: http://localhost:8080
```

### Frontend

```bash
cd frontend

npm install
npm run dev
# App disponível em: http://localhost:5173
# O proxy redireciona /api → localhost:8080 automaticamente
```

---

## 📁 Estrutura do Banco de Dados

```
users          → usuários da aplicação (email único, senha com hash BCrypt)
   ↓
accounts       → contas financeiras do usuário (corrente, cartão, etc.)
categories     → categorias de receita/despesa do usuário
goals          → metas financeiras do usuário
   ↓
transactions   → lançamentos financeiros (vinculados a account + category + user)
```

---

## 👨‍💻 Autor

**Enzo Nunes Andrade**

## 📬 Contato

**Enzo Nunes Andrade**

* **GitHub:** [@enzonunestwd](https://github.com/enzonunestwd)
* **LinkedIn:** [@enzonunesdf](https://www.linkedin.com/in/enzonunesdf)
* **Email:** [enzonunestwd@gmail.com](mailto:enzonunestwd@gmail.com)

---

<div align="center">

Desenvolvido com ☕ Java e muito aprendizado

</div>
