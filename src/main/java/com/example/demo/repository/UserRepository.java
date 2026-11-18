package com.example.demo.repository;

import com.example.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso à tabela "users". O Spring Data gera a implementação.
 * Já vem com findById, findAll, save, deleteById etc.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
