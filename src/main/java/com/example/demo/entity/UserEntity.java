package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 * Espelho da tabela "users".
 *
 * A tabela é criada pelo Liquibase (db/changelog/changes/001-create-users-table.yaml).
 * Esta classe NÃO cria nada no banco: com ddl-auto=validate o Hibernate só confere
 * se os campos daqui batem com as colunas de lá.
 */
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(length = 40)
    private String phone;

    // Valor preenchido pelo banco (default now()), por isso insertable/updatable = false.
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected UserEntity() {
        // exigido pelo JPA
    }

    public UserEntity(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
