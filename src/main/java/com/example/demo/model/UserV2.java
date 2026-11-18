package com.example.demo.model;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * V2 do contrato — adiciona campos novos e separa nome.
 * Mantida ao lado da V1 graças ao API Versioning nativo.
 */
@NullMarked
public record UserV2(
        Long id,
        String firstName,
        String lastName,
        String email,
        @Nullable String phone,
        @Nullable String avatarUrl
) {
}
