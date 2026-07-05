# 📘 FinPulse — Caderno de Notas (Parte 2: DTOs e Segurança)

---

## 4. A pasta `dto/` — Data Transfer Objects

**Por que não expor as entidades JPA diretamente na API?**

Essa é uma pergunta certeira de entrevista. Os motivos:

1. **Segurança**: se eu devolvesse a entidade `User` diretamente, o JSON incluiria `senhaHash` — um vazamento de dado sensível, mesmo que seja um hash.
2. **Acoplamento**: se o formato da API for *exatamente igual* ao modelo do banco, qualquer mudança na tabela quebra o contrato com o frontend. Com DTOs, posso mudar o banco internamente sem afetar quem consome a API.
3. **Validação**: anotações como `@NotBlank`, `@Email`, `@Positive` fazem sentido nos dados de **entrada** (requests), não na entidade que representa o estado salvo no banco.

Usamos **`record`** (recurso do Java moderno, desde o Java 14+) em vez de classes tradicionais para os DTOs. Um `record` é imutável e gera automaticamente construtor, getters, `equals`, `hashCode` e `toString` — perfeito para objetos que só "carregam dados" de um lado para o outro, sem comportamento.

### Padrão usado em todos os módulos (Account, Category, Transaction, Goal):

- **`XRequest`**: o que a API recebe (do frontend) ao criar/editar.
- **`XResponse`**: o que a API devolve (para o frontend), com um método estático `fromEntity(...)` que converte a entidade para o DTO.

```java
public static AccountResponse fromEntity(Account account) {
    return new AccountResponse(account.getId(), account.getNome(), ...);
}
```

Esse padrão (método de fábrica estático) centraliza a conversão em um único lugar — se um campo novo precisar aparecer na resposta, eu mudo em um arquivo, não em vários services espalhados.

### Destaque: `TransactionResponse`

Note que ele inclui `accountNome`, `categoryNome` e `categoryCor` — não só os IDs. Isso é uma decisão pensada para o **frontend**: ao listar transações na tela, o React não precisa fazer uma chamada extra para descobrir o nome da conta ou a cor da categoria — já vem tudo no mesmo JSON. Isso reduz o número de requisições HTTP (importante para performance).

### Destaque: `DashboardSummaryResponse` e `CategoriaSomaResponse`

Esses dois DTOs são o "contrato" entre o backend e os **gráficos** do frontend. `CategoriaSomaResponse` é literalmente uma fatia de gráfico de pizza: `{ categoria: "Alimentação", total: 450.00 }`. O frontend vai receber uma lista dessas e jogar direto numa lib de gráficos (ex: Recharts), sem precisar processar nada.

---

## 5. A pasta `security/` — Autenticação com JWT

Esse é provavelmente o assunto que mais aparece em entrevistas técnicas para vaga backend júnior/pleno: **"explica como funciona autenticação com JWT na sua API"**. Vamos detalhar.

### 5.1 O que é JWT, de fato

JWT = **JSON Web Token**. É uma string com 3 partes separadas por pontos:

```
HEADER.PAYLOAD.SIGNATURE
```

- **Header**: diz qual algoritmo de assinatura foi usado (no nosso caso, HS256).
- **Payload**: os dados do token — no nosso caso, o e-mail do usuário (`subject`) e a data de expiração.
- **Signature**: uma assinatura criptográfica gerada com uma chave secreta, que garante que **ninguém alterou o conteúdo** do token depois que ele foi emitido.

**Importante**: o conteúdo do payload **não é criptografado**, só assinado — qualquer um pode decodificar e ler o que tem dentro (existem sites como jwt.io que fazem isso). Por isso, nunca colocamos senha ou dado sensível dentro do JWT — só um identificador (no nosso caso, o e-mail).

### 5.2 `JwtService.java` — gerar e validar tokens

```java
public String gerarToken(String email) {
    return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(new Date(...))
            .setExpiration(new Date(...))
            .signWith(getChaveAssinatura(), SignatureAlgorithm.HS256)
            .compact();
}
```

Esse método gera o token no momento do login. `HS256` é um algoritmo simétrico — a mesma chave secreta que assina o token é usada para validar depois. (Existe também o RS256, assimétrico, com chave pública/privada — mais comum em sistemas distribuídos com múltiplos serviços validando tokens emitidos por um serviço de auth central. Para o nosso projeto, HS256 é suficiente e mais simples.)

```java
public boolean isTokenValido(String token, String emailEsperado) {
    String email = extrairEmail(token);
    return email.equals(emailEsperado) && !isTokenExpirado(token);
}
```

Validação em duas partes: (1) o e-mail dentro do token bate com o esperado, e (2) o token não expirou. Se a assinatura do token tivesse sido alterada por alguém mal-intencionado, o método `extrairTodosClaims` já teria lançado uma exceção antes de chegar aqui (a lib `jjwt` valida a assinatura automaticamente ao fazer o parse).

### 5.3 `User implements UserDetails`

Fizemos a entidade `User` implementar a interface `UserDetails` do Spring Security. Isso é uma escolha de design: poderíamos ter criado uma classe `CustomUserDetails` separada que "embrulha" o `User`, mas para um projeto desse tamanho, implementar direto reduz complexidade.

Os métodos como `getAuthorities()`, `isAccountNonExpired()`, etc., são exigências da interface — o Spring Security os chama internamente para decidir se deixa o usuário passar. Como não temos (ainda) sistema de permissões diferenciadas (ex: ADMIN vs USER comum) nem bloqueio de conta, todos retornam valores "neutros" (`true` para os `is...()`, lista vazia para `getAuthorities()`).

### 5.4 `FinpulseUserDetailsService.java`

Implementa `UserDetailsService`, com um único método: `loadUserByUsername`. O nome do método é genérico (vem da interface), mas no nosso caso, o "username" é o e-mail. Esse serviço é chamado pelo Spring Security sempre que ele precisa "saber quem é" um usuário — durante o login e durante a validação do token em cada requisição.

### 5.5 `JwtAuthFilter.java` — o "porteiro" de cada requisição

Esse é o componente mais importante de entender. Ele estende `OncePerRequestFilter`, o que garante que ele rode **exatamente uma vez por requisição HTTP**, antes do controller ser chamado.

Fluxo passo a passo:

1. Lê o cabeçalho `Authorization` da requisição.
2. Se não existir ou não começar com `"Bearer "`, deixa passar sem autenticar (a decisão de bloquear ou não fica para o `SecurityConfig`).
3. Se existir, extrai o token (removendo o prefixo `"Bearer "`) e o e-mail contido nele.
4. Carrega o usuário do banco (via `UserDetailsService`).
5. Verifica se o token é válido para aquele usuário.
6. Se for válido, registra no `SecurityContextHolder` que essa requisição está autenticada como aquele usuário.

**Por que isso importa**: depois desse filtro, em qualquer Controller, podemos fazer:

```java
@AuthenticationPrincipal User usuarioLogado
```

E o Spring injeta automaticamente o usuário autenticado, sem precisarmos decodificar token manualmente em cada endpoint. Isso vai ficar claro quando criarmos os Controllers.

### 5.6 `SecurityConfig.java` — a configuração geral

Os pontos centrais:

```java
.csrf(csrf -> csrf.disable())
```
CSRF (Cross-Site Request Forgery) é uma proteção relevante para aplicações que usam **cookies de sessão**. Como usamos JWT enviado manualmente no cabeçalho `Authorization` (não em cookie automático do navegador), essa proteção específica não se aplica aqui — por isso desabilitamos.

```java
.sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```
Essa é a linha que define a API como **stateless**. O servidor nunca guarda "quem está logado" em memória (não existe `HttpSession`). Cada requisição se autentica de forma independente, através do token. Isso é o que permite a API **escalar horizontalmente** sem complicação — qualquer instância do backend pode validar qualquer token, sem precisar "se comunicar" com outras instâncias para saber quem está logado (diferente de sessions tradicionais, que exigiriam algo como sticky sessions ou um cache compartilhado tipo Redis).

```java
.requestMatchers("/api/auth/**").permitAll()
.anyRequest().authenticated()
```
Define que **só** as rotas de autenticação são públicas; todo o resto exige token válido.

```java
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```
Registra nosso filtro customizado para rodar antes do filtro padrão do Spring Security — garantindo que, quando o Spring for decidir se autoriza a requisição, nosso filtro JWT já tenha tido a chance de autenticar o usuário.

### 5.7 CORS — por que precisamos configurar

CORS (Cross-Origin Resource Sharing) é uma proteção do **navegador**, não do servidor. Por padrão, JavaScript rodando em `https://meufrontend.com` não pode chamar uma API em `https://minhaapi.com` (origens diferentes) — a menos que o servidor explicitamente "autorize" isso através de cabeçalhos CORS.

```java
configuration.setAllowedOriginPatterns(List.of("*"));
```
Por enquanto, liberamos qualquer origem (`*`) para facilitar o desenvolvimento. **Antes de ir para produção**, isso deve ser restringido para a URL exata do frontend (ex: `https://finpulse.vercel.app`), senão qualquer site na internet poderia fazer chamadas autenticadas à sua API usando o token de um usuário que visitou aquele site.

---

## ✅ O que já temos até aqui (acumulado)

- [x] Estrutura de pastas do projeto Maven
- [x] `pom.xml` configurado
- [x] 5 entidades JPA: `User`, `Account`, `Category`, `Transaction`, `Goal`
- [x] 5 repositórios com queries customizadas para relatórios
- [x] DTOs de request/response para todos os módulos
- [x] Autenticação JWT completa (geração, validação, filtro, configuração)

## 🔜 Próximos passos

- [ ] Services (regras de negócio: AuthService, AccountService, TransactionService, GoalService, ReportService)
- [ ] Controllers (endpoints REST)
- [ ] `application.yml` (configuração do banco + variáveis JWT)
- [ ] Tratamento global de exceções
- [ ] Frontend React
- [ ] Deploy no Railway

---

*Continua na Parte 3...*
