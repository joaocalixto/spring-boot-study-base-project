# Postgres com dados pre-carregados para a aula Spring Boot 4.
# Usamos a imagem pgvector/pgvector porque os exercicios de Spring AI usam VectorStore.
FROM pgvector/pgvector:pg17

# Variaveis padrao (podem ser sobrescritas no docker-compose / -e)
ENV POSTGRES_DB=demo \
    POSTGRES_USER=demo \
    POSTGRES_PASSWORD=demo123 \
    TZ=America/Sao_Paulo \
    LANG=pt_BR.utf8

# Tudo dentro de /docker-entrypoint-initdb.d/ roda 1x na PRIMEIRA inicializacao
# (ordem alfabetica: 01-schema.sql, 02-seed.sql, 03-extra.sql)
COPY initdb/ /docker-entrypoint-initdb.d/

# Healthcheck para o Spring Boot esperar antes de conectar
HEALTHCHECK --interval=5s --timeout=3s --retries=10 \
  CMD pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB" || exit 1

EXPOSE 5432
