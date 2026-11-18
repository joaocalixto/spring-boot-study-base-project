# Exercícios — Bootiful Spring Boot 4

Lista progressiva. Cada exercício tem critério de aceitação claro. Faça na ordem.

---

## Nível 1 — Aquecimento

### 1.1. Subir o projeto

- Clone o repositório e suba o banco: `docker compose up -d postgres`
- Execute `./mvnw spring-boot:run`
- Acesse `http://localhost:8080/actuator/health`

**Aceite:** retorna `{"status":"UP"}`.

### 1.2. Confirmar virtual threads

Acesse `http://localhost:8080/actuator/threaddump`.

**Aceite:** você consegue apontar pelo menos 5 threads marcadas como `virtual` no dump.

---

## Nível 2 — API Versioning

### 2.1. Adicionar uma V3

Crie um `UserV3` com os campos da V2 + `createdAt` (LocalDateTime).
Crie o endpoint `version = "3"` no `UserController`.

**Aceite:**

```bash
curl -H "X-API-Version: 3" http://localhost:8080/api/users/1
```

retorna JSON com `createdAt`.

### 2.2. Mudar estratégia de versionamento

Troque o `application.yml` para usar **path-segment** em vez de header:

```yaml
spring:
  mvc:
    apiversion:
      use:
        path-segment: 1
```

**Aceite:** `GET /api/v2/users/1` retorna o objeto V2 sem você ter mudado o controller.

### 2.3. Versão obrigatória

Configure `required: true` e remova a linha `default`. O que acontece se o cliente não passar o header? Capture o erro e devolva `400 Bad Request` com mensagem em português.

---

## Nível 3 — HTTP Client declarativo

### 3.1. Adicionar endpoint

No `GitHubClient`, adicione um método para buscar issues de um repo:

```
GET /repos/{owner}/{repo}/issues
```

**Aceite:** novo endpoint REST `/api/github/{owner}/{repo}/issues` funcionando.

### 3.2. Criar um segundo cliente

Crie um `PokeApiClient` consumindo `https://pokeapi.co/api/v2`. Crie a interface, o bean em `HttpClientsConfig` e um controller `/api/pokemon/{name}`.

**Aceite:** `curl http://localhost:8080/api/pokemon/pikachu` retorna stats.

### 3.3. Interceptor

Adicione um interceptor no `RestClient.Builder` que loga a duração de cada request. Use `RestClient.Builder.requestInterceptor(...)`.

**Aceite:** no log aparece `[GitHubClient] GET /users/torvalds took XXXms`.

---

## Nível 4 — JSpecify e null-safety

### 4.1. Anotação no pacote

Crie um `package-info.java` em `com.example.demo.service` marcado com `@NullMarked`.

```java
@NullMarked
package com.example.demo.service;

import org.jspecify.annotations.NullMarked;
```

### 4.2. Provocar um warning

No `UserService.findById`, mude o retorno para não-nulo (remova o `@Nullable`) e tente retornar `null`.

**Aceite:** sua IDE acusa warning. Bonus: rode `mvn compile` com Error Prone/NullAway plugin e veja a falha.

---

## Nível 5 — Spring AI

### 5.1. Configurar chave

Exporte `OPENAI_API_KEY` e teste:

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"O que e Spring Boot 4 em 1 frase?"}'
```

**Aceite:** retorna uma resposta razoável.

### 5.2. Adicionar memória

Use o `MessageChatMemoryAdvisor` para que o chat lembre de mensagens anteriores na mesma sessão. Adicione um path param `conversationId`.

**Aceite:**

```bash
curl -X POST /api/chat/abc123 -d '{"message":"Meu nome e Bea"}'
curl -X POST /api/chat/abc123 -d '{"message":"Qual e meu nome?"}'
# responde "Bea"
```

### 5.3. Tool calling

Crie um bean com método anotado `@Tool` que retorna o `User` por ID. Plugue no `ChatClient`. Pergunte: "Quem e o usuario 1?" — o LLM deve chamar a tool.

---

## Nível 6 — Observabilidade

### 6.1. Subir OTel collector localmente

Crie um `docker-compose.yml` com OpenTelemetry Collector + Jaeger.

```yaml
services:
  jaeger:
    image: jaegertracing/all-in-one:latest
    ports: ["16686:16686", "4317:4317"]
```

**Aceite:** depois de subir e bater nos endpoints, você ve os traces em `http://localhost:16686`.

### 6.2. Span customizado

Adicione um span manual no `UserService.findById` usando `Tracer` do OpenTelemetry. Coloque atributo `user.id`.

**Aceite:** no Jaeger você consegue filtrar traces por `user.id`.

---

## Nível 7 — Produção

### 7.1. AppCDS

Gere o arquivo de archive e rode com ele:

```bash
./mvnw package
java -XX:ArchiveClassesAtExit=app.jsa -jar target/bea-demo-0.0.1-SNAPSHOT.jar
# Ctrl+C
java -XX:SharedArchiveFile=app.jsa -jar target/bea-demo-0.0.1-SNAPSHOT.jar
```

**Aceite:** o startup com `app.jsa` é mensuravelmente mais rápido (anote os dois tempos).

### 7.2. Native image (desafio)

Compile native:

```bash
./mvnw -Pnative native:compile
./target/bea-demo
```

**Aceite:** binário roda. Compare startup com o jar normal. (Spoiler: 10x+ mais rápido.)

### 7.3. Carga com virtual threads

Use `wrk` ou `hey` pra disparar 1000 requests concorrentes contra `/api/github/torvalds`. Compare com e sem `spring.threads.virtual.enabled`.

**Aceite:** mostre os números em um README curto. Espera-se latência menor e throughput maior com virtuals.

---

## Entrega final

Crie um PR (ou pasta) com:

1. Código de todos os exercícios acima funcionando
2. README explicando como rodar cada um
3. Print/log dos testes 7.1 e 7.3 (medições reais)

Tempo estimado total: **8 a 12 horas** distribuídas em uma semana.
