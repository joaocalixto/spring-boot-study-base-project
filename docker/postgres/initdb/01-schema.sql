-- Schema inicial para a aula Spring Boot 4
-- Roda automaticamente na primeira subida do container

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- Para Spring AI / RAG (Exercicio 5.x)
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(80)  NOT NULL,
    last_name   VARCHAR(80)  NOT NULL,
    email       VARCHAR(160) NOT NULL UNIQUE,
    phone       VARCHAR(40),
    avatar_url  VARCHAR(255),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_email ON users(email);

CREATE TABLE repos (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(120) NOT NULL,
    description TEXT,
    stars       INT NOT NULL DEFAULT 0,
    language    VARCHAR(40),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_repos_user_id ON repos(user_id);

-- Tabela para chat com memoria (Spring AI)
CREATE TABLE chat_messages (
    id              BIGSERIAL PRIMARY KEY,
    conversation_id VARCHAR(64) NOT NULL,
    role            VARCHAR(20) NOT NULL,  -- user / assistant / system
    content         TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_chat_conversation ON chat_messages(conversation_id, created_at);
