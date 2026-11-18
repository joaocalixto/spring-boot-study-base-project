package com.example.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Sobe um Postgres descartável (mesma imagem do docker-compose) para os testes.
 * O @ServiceConnection faz o Spring apontar o datasource para ele sozinho,
 * e o Liquibase roda as migrações nesse banco antes dos testes.
 *
 * Precisa do Docker ligado.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        return new PostgreSQLContainer(
                DockerImageName.parse("pgvector/pgvector:pg17").asCompatibleSubstituteFor("postgres"));
    }
}
