package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class UserControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void retornaV1QuandoNaoHaHeader() {
        webTestClient.get()
                .uri("/api/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Beatriz Lima");
    }

    @Test
    void retornaV2ComHeader() {
        webTestClient.get()
                .uri("/api/users/1")
                .header("X-API-Version", "2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Beatriz")
                .jsonPath("$.lastName").isEqualTo("Lima")
                .jsonPath("$.avatarUrl").exists();
    }

    @Test
    void retorna404QuandoNaoExiste() {
        webTestClient.get()
                .uri("/api/users/999")
                .exchange()
                .expectStatus().isNotFound();
    }
}
