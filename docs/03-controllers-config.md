# 📘 FinPulse — Caderno de Notas (Parte 3: Controllers e Configuração)

---

## 6. A pasta `controller/` — os endpoints REST

Controllers são a "porta de entrada" da API. Eles recebem requisições HTTP, delegam o trabalho pro Service, e devolvem uma resposta HTTP. **Nenhuma regra de negócio mora aqui** — controllers só traduzem HTTP em chamadas de método.

### Padrão usado em todos os controllers:

```java
@RestController           // diz que essa classe responde requisições HTTP com JSON
@RequestMapping("/api/x") // prefixo de todas as rotas dessa classe
@RequiredArgsConstructor  // Lombok: injeta os Services pelo construtor
public class XController {

    @GetMapping           // HTTP GET
    @PostMapping          // HTTP POST
    @PutMapping("/{id}")  // HTTP PUT com parâmetro de path
    @DeleteMapping("/{id}") // HTTP DELETE
}
```

### `@AuthenticationPrincipal User usuario`

Essa anotação é o que "pega" o usuário autenticado em cada endpoint. Funciona porque o `JwtAuthFilter` (que criamos antes) já extraiu o usuário do token JWT e registrou no `SecurityContextHolder` antes do controller ser chamado. Então em vez de ter que decodificar token manualmente em cada endpoint, o Spring injeta o `User` pronto.

```java
@GetMapping
public ResponseEntity<List<AccountResponse>> listar(@AuthenticationPrincipal User usuario) {
    return ResponseEntity.ok(accountService.listarPorUsuario(usuario));
}
```

Isso garante automaticamente que cada usuário só vê os próprios dados — o `usuario` injetado aqui é sempre quem está autenticado nessa requisição.

### Códigos HTTP usados e por quê:

| Código | Quando usar |
|---|---|
| `200 OK` | Busca ou atualização bem-sucedida |
| `201 Created` | Criação de novo recurso (POST) |
| `204 No Content` | Deleção bem-sucedida (sem corpo de resposta) |
| `400 Bad Request` | Dados de entrada inválidos (validação falhou) |
| `401 Unauthorized` | Token ausente ou inválido |
| `404 Not Found` | Recurso não encontrado |

### Destaque: `ReportController`

```java
@GetMapping("/dashboard")
public ResponseEntity<DashboardSummaryResponse> dashboard(
        @RequestParam(required = false) LocalDate inicio,
        @RequestParam(required = false) LocalDate fim,
        @AuthenticationPrincipal User usuario) {

    if (inicio == null) inicio = LocalDate.now().withDayOfMonth(1);
    if (fim == null) fim = LocalDate.now();
    ...
}
```

Os parâmetros de data são opcionais (`required = false`). Se o frontend não informar, usa o mês atual por padrão — isso é um "default inteligente" que evita que o frontend precise sempre mandar as datas, e ao mesmo tempo permite filtrar períodos diferentes quando precisar. Essa é uma boa prática de design de API REST.

### Destaque: `GoalController` — o endpoint de aporte

```java
@PatchMapping("/{id}/aporte")
public ResponseEntity<GoalResponse> adicionarAporte(
        @PathVariable Long id,
        @RequestParam BigDecimal valor,
        @AuthenticationPrincipal User usuario) {
```

Usamos `PATCH` (não `PUT`) porque estamos fazendo uma **atualização parcial** — só incrementando o `valorAtual`, não substituindo o objeto inteiro. Essa distinção é importante:
- `PUT`: substitui o recurso inteiro (precisa mandar todos os campos)
- `PATCH`: atualização parcial (manda só o que muda)

---

## 7. `application.yml` — configuração da aplicação

### Por que `.yml` e não `.properties`?

Ambos funcionam. YAML é mais legível para configurações aninhadas — em vez de:
```properties
spring.datasource.url=jdbc:postgresql://...
spring.datasource.username=postgres
spring.jpa.hibernate.ddl-auto=update
```

Você tem:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://...
    username: postgres
  jpa:
    hibernate:
      ddl-auto: update
```

### Variáveis de ambiente com fallback

```yaml
url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/finpulse}
```

Esse padrão `${VARIAVEL:valor_padrao}` é essencial para funcionar em dois ambientes:
- **Local**: usa o valor padrão (`localhost:5432/finpulse`)
- **Railway (produção)**: usa a variável de ambiente `DATABASE_URL` que o Railway injeta automaticamente

Isso evita ter dois arquivos `application.yml` diferentes pra cada ambiente.

### `ddl-auto: update`

O Hibernate lê suas entidades JPA e cria/atualiza as tabelas no banco automaticamente. Em desenvolvimento, isso é conveniente. Em produção real com dados importantes, o ideal é usar `validate` (só verifica se o schema bate, sem alterar nada) e gerenciar as mudanças de banco com uma ferramenta de migration como **Flyway** ou **Liquibase**. Para o FinPulse no estágio atual (portfólio), `update` é suficiente.

### JWT Secret

```yaml
jwt:
  secret: ${JWT_SECRET:finpulse-secret-key-deve-ter-pelo-menos-32-caracteres-para-hs256}
```

**NUNCA** faça commit de uma secret real no Git. O valor padrão aqui é só para desenvolvimento local. No Railway, você vai configurar `JWT_SECRET` como variável de ambiente com um valor gerado aleatoriamente (ex: `openssl rand -base64 32` no terminal).

---

## 8. Mapa completo dos endpoints da API

| Método | Rota | Descrição | Auth? |
|---|---|---|---|
| POST | `/api/auth/register` | Cadastro de novo usuário | ❌ |
| POST | `/api/auth/login` | Login, retorna JWT | ❌ |
| GET | `/api/accounts` | Lista contas do usuário | ✅ |
| POST | `/api/accounts` | Cria nova conta | ✅ |
| PUT | `/api/accounts/{id}` | Atualiza conta | ✅ |
| DELETE | `/api/accounts/{id}` | Deleta conta | ✅ |
| GET | `/api/categories` | Lista categorias | ✅ |
| POST | `/api/categories` | Cria categoria | ✅ |
| PUT | `/api/categories/{id}` | Atualiza categoria | ✅ |
| DELETE | `/api/categories/{id}` | Deleta categoria | ✅ |
| GET | `/api/transactions` | Lista transações | ✅ |
| POST | `/api/transactions` | Registra transação | ✅ |
| DELETE | `/api/transactions/{id}` | Deleta transação | ✅ |
| GET | `/api/goals` | Lista metas | ✅ |
| POST | `/api/goals` | Cria meta | ✅ |
| PUT | `/api/goals/{id}` | Atualiza meta | ✅ |
| PATCH | `/api/goals/{id}/aporte` | Registra aporte na meta | ✅ |
| DELETE | `/api/goals/{id}` | Deleta meta | ✅ |
| GET | `/api/reports/dashboard` | Resumo financeiro do período | ✅ |

---

## ✅ Backend completo — o que foi construído

- [x] 5 entidades JPA com relacionamentos
- [x] 5 repositórios com queries customizadas (JPQL)
- [x] DTOs tipados de request/response para todos os módulos
- [x] Autenticação JWT stateless completa
- [x] 6 services com regras de negócio
- [x] 6 controllers com 19 endpoints REST
- [x] Tratamento global de exceções (GlobalExceptionHandler)
- [x] Configuração de ambiente (application.yml com suporte a variáveis de ambiente)

## 🔜 Próximos passos

- [ ] Frontend React (próxima etapa)
- [ ] Deploy no Railway
- [ ] README.md do projeto (importante pro portfólio)

---

*Continua na Parte 4 (Frontend)...*
