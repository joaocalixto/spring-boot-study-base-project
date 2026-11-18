package com.example.demo.api.v1;

import com.example.demo.model.User;
import com.example.demo.model.UserV2;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demonstra o API Versioning nativo do Spring Boot 4.
 *
 * Mesma URL (/api/users/{id}), duas versões selecionadas pelo header X-API-Version
 * (ou query, path, media-type — configurado em application.yml).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}", version = "1")
    public ResponseEntity<User> getV1(@PathVariable Long id) {
        User user = service.findById(id);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @GetMapping(value = "/{id}", version = "2")
    public ResponseEntity<UserV2> getV2(@PathVariable Long id) {
        UserV2 user = service.findV2ById(id);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }
}
