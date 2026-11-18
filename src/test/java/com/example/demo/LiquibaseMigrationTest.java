package com.example.demo;

import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Confere que o Liquibase rodou: a tabela existe, os dados de exemplo entraram
 * e os changesets ficaram registrados na tabela de controle DATABASECHANGELOG.
 */
@SpringBootTest
@Import(TestcontainersConfig.class)
class LiquibaseMigrationTest {

    @Autowired
    UserRepository users;

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void criaTabelaUsersComDadosDeExemplo() {
        assertThat(users.count()).isEqualTo(3);
    }

    @Test
    void registraOsChangesetsNaTabelaDeControle() {
        var ids = jdbc.queryForList("select id from databasechangelog order by orderexecuted", String.class);
        assertThat(ids).containsExactly("001-create-users-table", "002-insert-sample-users");
    }
}
