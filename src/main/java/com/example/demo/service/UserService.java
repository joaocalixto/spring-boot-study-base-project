package com.example.demo.service;

import com.example.demo.entity.UserEntity;
import com.example.demo.model.User;
import com.example.demo.model.UserV2;
import com.example.demo.repository.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio de usuário.
 * Busca no banco (via repository) e converte a entidade para o formato de cada versão da API.
 */
@Service
@NullMarked
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public @Nullable User findById(Long id) {
        return repository.findById(id).map(this::toV1).orElse(null);
    }

    public @Nullable UserV2 findV2ById(Long id) {
        return repository.findById(id).map(this::toV2).orElse(null);
    }

    private User toV1(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getFirstName() + " " + entity.getLastName(),
                entity.getEmail(),
                entity.getPhone()
        );
    }

    private UserV2 toV2(UserEntity entity) {
        return new UserV2(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhone(),
                "https://i.pravatar.cc/150?u=" + entity.getId()
        );
    }
}
