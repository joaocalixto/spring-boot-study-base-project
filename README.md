# bea-demo — Spring Boot 4 demo

Projeto exemplo da aula. Demonstra:

- Virtual threads (Project Loom)
- JSpecify null-safety
- API Versioning nativo (header)
- HTTP Service Client declarativo
- Spring AI (OpenAI)
- OpenTelemetry

## Rodar

```bash
export OPENAI_API_KEY=sk-...
./mvnw spring-boot:run
```

## Endpoints

```bash
# User v1 (default)
curl http://localhost:8080/api/users/1

# User v2 (via header)
curl -H "X-API-Version: 2" http://localhost:8080/api/users/1

# HTTP client declarativo (GitHub)
curl http://localhost:8080/api/github/torvalds

# Chat AI
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"O que e Spring Boot 4?"}'

# Health / metrics
curl http://localhost:8080/actuator/health
```

## Observabilidade (opcional)

```bash
docker compose up -d
# Jaeger UI: http://localhost:16686
```

## Testar

```bash
./mvnw test
```
