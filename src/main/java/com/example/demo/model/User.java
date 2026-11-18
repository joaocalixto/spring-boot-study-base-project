package com.example.demo.model;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Demonstrando JSpecify:
 * - @NullMarked: por padrão tudo aqui é NÃO-nulo.
 * - @Nullable explícito quando o campo pode ser nulo.
 */
@NullMarked
public record User(
        Long id,
        String name,
        String email,
        @Nullable String phone
) {
}
