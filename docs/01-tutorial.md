# Tutorial: Bootiful Spring Boot 4

> Tutorial guiado para Beatriz, baseado na palestra do Josh Long na Spring I/O 2026.
> Pré-requisito: você já conhece Spring Boot 3.x, REST, JPA e injeção de dependência.

---

## 0. Contexto: por que Spring Boot 4 importa

Spring Boot 4.0 saiu em **20/nov/2025** junto com Spring Framework 7.0. Não é só "mais um upgrade" — ele redefine a baseline:

- **Java 25 como mínimo recomendado** (LTS, com virtual threads maduros, pattern matching avançado, structured concurrency)
- **Codebase modularizada** em jars menores e focados
- **JSpecify** como padrão único de null-safety (substitui `@Nullable`/`@NonNull` do Spring)
- **API versioning nativo**, sem mais hacks com path prefix manual
- **HTTP Service Clients** estabilizados — Feign virou opcional
- **Spring AI 1.0** já integrado ao ecossistema oficial
- **OpenTelemetry starter** oficial

A mensagem do Josh: Spring Boot deixou de ser "framework web" e virou **plataforma de aplicação** — web, batch, dados, IA, integração e observabilidade no mesmo lugar.

---

## 1. Setup do projeto

Use o [start.spring.io](https://start.spring.io) com:

- **Spring Boot:** 4.0.x
- **Java:** 25
- **Build:** Maven
- **Dependencies:** Web, Validation, Actuator, OpenTelemetry, AI (OpenAI)

Trecho relevante do `pom.xml`:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.5</version>
</parent>

<properties>
    <java.version>25</java.version>
</properties>
```

---

## 2. Virtual Threads (Project Loom)

No Spring Boot 3.2 já existia, mas em 4.0 está **ligado por padrão para Tomcat/Jetty** quando você ativa:

```yaml
spring:
  threads:
    virtual:
      enabled: true
```

O que muda na prática: o pool de threads do Tomcat passa de ~200 plataformais para **N virtuais** (centenas de milhares possíveis). Bloqueante deixa de ser problema.

**Quando NÃO usar:** código que faz `synchronized` pesado (vai "pinar" a virtual thread) ou usa `ThreadLocal` enorme. Java 25 suavizou isso, mas vale revisar.

**Exercício mental:** seu serviço hoje faz 3 chamadas HTTP serializadas por request. Com 200 threads plataformais e 100ms cada chamada, qual é a capacidade máxima? Com virtual threads?

---

## 3. JSpecify: null-safety unificada

Antes (Spring 6):

```java
import org.springframework.lang.Nullable;

public @Nullable User findById(Long id) { ... }
```

Agora (Spring 7):

```java
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked  // tudo no pacote é não-nulo por padrão
package com.example.demo.service;
```

```java
public @Nullable User findById(Long id) { ... }
public User findByIdRequired(Long id) { ... }  // não-nulo por default
```

**Por que isso importa:** ferramentas (IDE, Checker Framework, NullAway) finalmente compartilham um vocabulário. Você ganha avisos em tempo de compilação e via análise estática.

---

## 4. API Versioning nativo

O grande pedido da comunidade. Em vez de:

```java
@GetMapping("/api/v1/users/{id}")  // velho jeito: path manual
```

Agora:

```java
@RestController
@RequestMapping("/api/users")
class UserController {

    @GetMapping(value = "/{id}", version = "1")
    public UserV1 getV1(@PathVariable Long id) { ... }

    @GetMapping(value = "/{id}", version = "2")
    public UserV2 getV2(@PathVariable Long id) { ... }
}
```

E configura a estratégia em `application.yml`:

```yaml
spring:
  mvc:
    apiversion:
      use:
        header: X-API-Version
      # alternativas: path-segment, query-param, media-type
      required: false
      default-version: "1"
```

**Estratégias suportadas:**

- `header` — `X-API-Version: 2`
- `query-param` — `?version=2`
- `path-segment` — `/api/v2/users` (Spring resolve sem você duplicar path)
- `media-type` — `Accept: application/json;version=2`

**Vantagem real:** rotear por versão sem `if` no controller, e o Actuator/OpenAPI sabe que aquilo é versão 2.

---

## 5. HTTP Service Clients declarativos

Esse é o "adeus Feign". Você declara uma **interface** e o Spring gera a implementação:

```java
public interface GitHubClient {

    @GetExchange("/users/{username}")
    GitHubUser getUser(@PathVariable String username);

    @GetExchange("/users/{username}/repos")
    List<Repo> listRepos(@PathVariable String username);
}
```

E registra:

```java
@Configuration
class HttpClientsConfig {

    @Bean
    GitHubClient gitHubClient(RestClient.Builder builder) {
        RestClient client = builder
            .baseUrl("https://api.github.com")
            .defaultHeader("Accept", "application/vnd.github+json")
            .build();
        return HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(client))
            .build()
            .createClient(GitHubClient.class);
    }
}
```

Spring Boot 4 também trouxe `@ImportHttpServices` para registrar várias interfaces em grupo com config em `application.yml`. Útil quando você consome 5+ APIs.

**Por que isso é grande:** zero implementação manual de cliente, suporte a reactive (`Mono<T>`/`Flux<T>`), interceptors, retries — tudo via configuração.

---

## 6. Modularização do Spring Boot

O monolito `spring-boot-autoconfigure` foi quebrado. Agora você tem jars como:

- `spring-boot-tomcat`
- `spring-boot-jackson`
- `spring-boot-validation`
- `spring-boot-restclient`

**O que muda pra você:** menos coisas no classpath que você não usa, builds nativos (GraalVM) menores, e o `spring-boot-starter-web` virou um agregador puro de starters menores.

Veja o que entrou na sua app:

```bash
./mvnw dependency:tree | grep spring-boot
```

---

## 7. Observabilidade: OpenTelemetry starter

Antes era um quebra-cabeça de Micrometer + Brave + agentes. Agora:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-opentelemetry</artifactId>
</dependency>
```

E configura o exporter:

```yaml
otel:
  exporter:
    otlp:
      endpoint: http://localhost:4317
  service:
    name: bea-demo
```

Sem agente Java, sem hack. Métricas, traces e logs em OTLP nativo.

---

## 8. Spring AI

Spring AI 1.0 saiu junto com Boot 4. A ideia: tratar LLM como qualquer outro recurso Spring (igual JDBC).

```java
@RestController
class ChatController {

    private final ChatClient chat;

    ChatController(ChatClient.Builder builder) {
        this.chat = builder.build();
    }

    @PostMapping("/api/chat")
    String chat(@RequestBody String pergunta) {
        return chat.prompt()
            .user(pergunta)
            .call()
            .content();
    }
}
```

`application.yml`:

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini
```

Funcionalidades-chave do Spring AI 1.0:

- **ChatClient** unificado (OpenAI, Anthropic, Ollama, Bedrock)
- **Tool calling** declarativo via `@Tool`
- **RAG** com `VectorStore` (Postgres pgvector, Redis, Pinecone)
- **Advisors** (interceptors para o prompt — memória, RAG, guardrails)
- **MCP** (Model Context Protocol) — clientes e servidores

---

## 9. Produção: GraalVM Native, AppCDS, CRaC

O Josh sempre fecha com isso. Três caminhos:

### GraalVM Native Image

```bash
./mvnw -Pnative native:compile
```

Resultado: binário standalone, startup em ~50ms, RAM 10x menor. Ideal pra serverless e Kubernetes scale-to-zero.

### AppCDS (Application Class Data Sharing)

```bash
java -XX:ArchiveClassesAtExit=app.jsa -jar app.jar
java -XX:SharedArchiveFile=app.jsa -jar app.jar
```

Spring Boot 4 gera o `app.jsa` no build se você quiser. Startup ~30% mais rápido sem mexer no código.

### CRaC (Coordinated Restore at Checkpoint)

Você "fotografa" a JVM já quentinha e restaura em milissegundos. Boot 4 tem hooks declarativos pra preparar/restaurar.

---

## 10. Roadmap pessoal pra Beatriz

Ordem recomendada de adoção em um projeto real:

1. Subir baseline Java 25 + virtual threads (ganho grátis)
2. Trocar `@Nullable` antigo por JSpecify (refactor automático na IDE)
3. Migrar clientes HTTP para HTTP Interface (apaga muito código)
4. Adicionar OpenTelemetry starter (substitui Micrometer + Brave)
5. Adotar API versioning quando precisar quebrar contrato
6. Experimentar Spring AI num feature isolado
7. Native image só depois que app estiver estável

---

## Referências

- Palestra: [Bootiful Spring Boot 4 — Josh Long @ Spring I/O 2026](https://www.youtube.com/watch?v=6zfuCPQzrwE)
- Sessão oficial: [2026.springio.net/sessions/bootiful-spring-boot-4](https://2026.springio.net/sessions/bootiful-spring-boot-4/)
- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [HTTP Service Client Enhancements](https://spring.io/blog/2025/09/23/http-service-client-enhancements/)
- [API Versioning in Spring (blog oficial)](https://spring.io/blog/2025/09/16/api-versioning-in-spring/)
- [Baeldung — Spring Boot 4 & Framework 7](https://www.baeldung.com/spring-boot-4-spring-framework-7)
