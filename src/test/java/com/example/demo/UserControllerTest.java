package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void retornaV1QuandoNaoHaHeader() throws Exception {
        mvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Beatriz Lima"));
    }

    @Test
    void retornaV2ComHeader() throws Exception {
        mvc.perform(get("/api/users/1").header("X-API-Version", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Beatriz"))
                .andExpect(jsonPath("$.lastName").value("Lima"))
                .andExpect(jsonPath("$.avatarUrl").exists());
    }

    @Test
    void retorna404QuandoNaoExiste() throws Exception {
        mvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }
}
