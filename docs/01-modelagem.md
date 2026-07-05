# 📘 FinPulse — Caderno de Notas do Projeto

> Este caderno explica, parte por parte, cada arquivo do projeto FinPulse.
> A ideia é que você consiga **defender qualquer linha desse código** em uma entrevista técnica.

---

## 🗺️ Visão geral da arquitetura

```
finpulse/
├── backend/          → API Java + Spring Boot (o que estamos construindo agora)
│   └── finpulse-api/
│       ├── pom.xml   → "lista de compras" do Maven: quais bibliotecas o projeto usa
│       └── src/main/java/com/enzo/finpulse/
│           ├── model/        → as "tabelas" do banco, representadas como classes Java
│           ├── repository/   → quem fala com o banco de dados
│           ├── service/      → onde fica a lógica de negócio (ainda vamos criar)
│           ├── controller/   → quem recebe as requisições HTTP (ainda vamos criar)
│           ├── dto/          → objetos de transporte entre API e frontend (ainda vamos criar)
│           ├── security/     → autenticação JWT (ainda vamos criar)
│           └── config/       → configurações gerais (ainda vamos criar)
└── frontend/         → aplicação React (ainda vamos construir)
```

**Por que essa separação em camadas?**
Isso se chama **arquitetura em camadas (layered architecture)**. Cada camada tem uma responsabilidade clara:

- `model` → "o que existe" (dados)
- `repository` → "como buscar/salvar isso no banco"
- `service` → "as regras de negócio" (ex: "não pode gastar mais do que tem na conta")
- `controller` → "como o mundo externo (frontend, Postman) fala com a API"

Isso é importante em entrevista: se perguntarem "por que você separou assim?", a resposta é **separação de responsabilidades (Single Responsibility Principle)** — cada camada pode mudar sem afetar as outras. Se eu troco o banco de Postgres para MongoDB, só a camada `repository` muda; `controller` e `service` continuam iguais.

---

## 1. `pom.xml` — a configuração do projeto

O Maven é o gerenciador de dependências do Java (equivalente ao `package.json` do Node ou `requirements.txt` do Python). O `pom.xml` diz: "esse projeto precisa dessas bibliotecas para funcionar".

As dependências que escolhemos:

| Dependência | Para que serve |
|---|---|
| `spring-boot-starter-web` | Cria a API REST (endpoints HTTP) |
| `spring-boot-starter-data-jpa` | Conecta o Java com o banco de dados usando objetos (ORM) |
| `postgresql` | Driver para conectar no banco PostgreSQL |
| `spring-boot-starter-validation` | Permite validar dados de entrada (`@NotNull`, `@NotBlank`) |
| `spring-boot-starter-security` | Framework de autenticação/autorização |
| `jjwt-api` / `jjwt-impl` / `jjwt-jackson` | Biblioteca para gerar e validar tokens JWT (login) |
| `lombok` | Gera código repetitivo automaticamente (getters, setters) |
| `h2` | Banco de dados em memória, útil para testar sem precisar instalar Postgres |

**Pergunta de entrevista provável:** "O que é um ORM?"
**Resposta:** ORM (Object-Relational Mapping) é uma técnica que permite trabalhar com o banco de dados usando objetos Java em vez de escrever SQL puro. O JPA é a especificação; o Hibernate (que vem dentro do Spring Data JPA) é a implementação mais usada.

---

## 2. A pasta `model/` — as entidades

Cada classe aqui representa uma tabela do banco de dados. Vamos por arquivo:

### 2.1 `TransactionType.java` e `AccountType.java` (enums)

Um **enum** é um tipo que só pode assumir valores pré-definidos. Em vez de guardar o tipo de transação como uma `String` qualquer (onde alguém poderia digitar "receita", "Receita", "RECEITA" — três valores diferentes para o banco!), usamos um enum: `RECEITA` ou `DESPESA`, e ponto. Isso evita bugs de digitação e deixa o código auto-documentado.

### 2.2 `User.java`

```java
@Entity              // diz ao JPA: "essa classe é uma tabela do banco"
@Table(name = "users") // nome da tabela no banco (em inglês, plural, é convenção)
@Data                 // Lombok: gera getters/setters/toString/equals automaticamente
@Builder              // Lombok: permite criar objetos assim: User.builder().nome("Enzo").build()
```

Pontos importantes:
- `senhaHash`, não `senha`: **nunca guardamos senha em texto puro no banco**. Vamos usar BCrypt (que vem com o Spring Security) para transformar a senha em um hash irreversível. Se o banco for vazado, ninguém recupera a senha original.
- `@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)`: isso diz "um User tem várias Account". O `cascade = ALL` significa que se eu deletar o usuário, todas as contas dele são deletadas também (sem deixar "lixo" no banco). `orphanRemoval = true` significa: se eu tirar uma conta da lista `contas` do usuário, ela é deletada do banco (não fica "órfã").
- `@PrePersist`: método que o JPA chama automaticamente **antes de salvar** o registro pela primeira vez. Usamos para preencher `criadoEm` com a data atual sem precisar fazer isso manualmente em todo lugar do código.

### 2.3 `Account.java`

Representa uma conta financeira (Nubank, cartão, etc).

```java
@Column(nullable = false, precision = 15, scale = 2)
private BigDecimal saldo;
```

**Por que `BigDecimal` e não `double` ou `float`?**
Esta é uma pergunta clássica de entrevista para sistemas financeiros. `double` e `float` usam representação binária de ponto flutuante, que **não consegue representar exatamente** certos valores decimais (ex: 0.1 + 0.2 em double pode dar 0.30000000000000004). Em um sistema financeiro, esse erro de arredondamento é inaceitável. `BigDecimal` representa o número com precisão exata, ideal para dinheiro.

`@JsonBackReference`: quando a API devolve um JSON com `Account`, esse campo (`user`) não é incluído na resposta. Isso evita um problema chamado **referência circular infinita**: User tem lista de Account, Account tem User, que tem lista de Account... o Jackson (biblioteca que transforma objetos Java em JSON) entraria em loop infinito sem essa anotação.

### 2.4 `Category.java`

Categorias como "Alimentação", "Salário", "Transporte". Cada categoria tem um `tipo` (RECEITA ou DESPESA) — isso evita que uma despesa seja categorizada como "Salário", por exemplo.

Campos `cor` e `icone` existem para o **frontend**: quando formos montar os gráficos em React, cada categoria já vem com a cor certa para o gráfico de pizza, sem precisar mapear isso manualmente no frontend.

### 2.5 `Transaction.java`

A entidade mais usada do sistema. Cada lançamento financeiro (uma compra, um salário recebido) é um registro aqui.

Ponto de atenção: `valor` é **sempre positivo**. O sinal (se entra ou sai dinheiro) vem do campo `tipo`, não de um valor negativo. Isso é uma decisão de design: facilita a soma de relatórios (não precisamos lembrar de inverter sinal) e evita bugs onde alguém esquece de tornar um valor negativo.

`dataTransacao` (quando o gasto ocorreu) é diferente de `criadoEm` (quando o registro foi criado no sistema). Por exemplo: você pode registrar hoje uma compra que fez na semana passada — `dataTransacao` seria a data da compra, `criadoEm` seria hoje.

### 2.6 `Goal.java`

Representa uma meta financeira. O método `getProgresso()` é interessante:

```java
@Transient
public BigDecimal getProgresso() { ... }
```

`@Transient` diz ao JPA: "esse campo/método não existe no banco, é calculado em tempo real". O progresso (ex: 65% da meta atingida) não precisa ser salvo no banco — é sempre calculado a partir de `valorAtual / valorObjetivo`. Isso evita inconsistência (imagine se salvássemos o progresso e ele ficasse desatualizado depois de uma mudança no valorAtual).

---

## 3. A pasta `repository/` — acesso ao banco

Cada repositório é uma **interface** (não uma classe!) que estende `JpaRepository<TipoDaEntidade, TipoDoId>`. O Spring Data JPA gera a implementação automaticamente em tempo de execução — você nunca escreve a implementação, só declara o que precisa.

### Query methods (o "truque mágico" do Spring Data JPA)

```java
Optional<User> findByEmail(String email);
```

O Spring lê o **nome do método** e gera a query SQL automaticamente:
`findByEmail` → `SELECT * FROM users WHERE email = ?`

Funciona porque o Spring Data JPA segue uma convenção de nomenclatura. Alguns exemplos usados no projeto:

| Método | SQL equivalente |
|---|---|
| `findByUserId(Long userId)` | `WHERE user_id = ?` |
| `findByIdAndUserId(Long id, Long userId)` | `WHERE id = ? AND user_id = ?` |
| `findByUserIdAndDataTransacaoBetween(...)` | `WHERE user_id = ? AND data_transacao BETWEEN ? AND ?` |

**Por que sempre filtramos por `userId` nas buscas?**
Isso é uma decisão de **segurança**, não só de funcionalidade. Sem isso, o usuário A poderia, manipulando o ID na URL (ex: `/api/accounts/5`), acessar uma conta do usuário B. Ao exigir `findByIdAndUserId`, garantimos que mesmo que alguém tente acessar um ID que não é dele, a busca retorna vazio. Isso se chama **prevenção de IDOR** (Insecure Direct Object Reference) — uma vulnerabilidade clássica de APIs, e citar isso em entrevista mostra maturidade de segurança.

### `@Query` (quando o nome do método não é suficiente)

No `TransactionRepository`, temos queries mais complexas escritas em **JPQL** (uma linguagem parecida com SQL, mas que trabalha com entidades Java em vez de tabelas):

```java
@Query("""
        SELECT COALESCE(SUM(t.valor), 0) FROM Transaction t
        WHERE t.user.id = :userId
        AND t.tipo = :tipo
        AND t.dataTransacao BETWEEN :inicio AND :fim
        """)
BigDecimal somarPorTipoEPeriodo(...)
```

Essa query soma todas as transações de um tipo (RECEITA ou DESPESA) num período. É a base do dashboard: "quanto você gastou esse mês?", "quanto recebeu?". O `COALESCE(SUM(...), 0)` garante que, se não houver nenhuma transação, o resultado seja `0` em vez de `null` (evita um possível `NullPointerException` mais adiante no código).

A segunda query (`somarPorCategoriaEPeriodo`) agrupa por categoria — é exatamente o dado que vai alimentar o **gráfico de pizza** no frontend (quanto foi gasto em cada categoria).

---

## ✅ O que já temos até aqui

- [x] Estrutura de pastas do projeto Maven
- [x] `pom.xml` configurado
- [x] 5 entidades JPA: `User`, `Account`, `Category`, `Transaction`, `Goal`
- [x] 5 repositórios com queries customizadas para relatórios

## 🔜 Próximos passos (vou continuar gerando)

- [ ] DTOs (objetos de entrada/saída da API)
- [ ] Services (regras de negócio)
- [ ] Security + JWT (login e proteção das rotas)
- [ ] Controllers (endpoints REST)
- [ ] `application.yml` (configuração do banco)
- [ ] Frontend React
- [ ] Deploy no Railway

---

*Continua na Parte 2...*
