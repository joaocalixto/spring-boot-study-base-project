# Tarefa — Entidade `Account` (conta)

> Objetivo: criar **todas as camadas** de uma entidade nova, do banco até a API,
> seguindo o mesmo caminho que `users` já faz neste projeto.
> Antes de começar, leia `docs/liquibase-boas-praticas.md`.

---

## Contexto

Cada usuário pode ter uma ou mais contas. Uma conta tem número, tipo e saldo.

Use `users` como modelo. Os arquivos de referência são:

| Camada | Exemplo pronto em `users` |
|---|---|
| Migração (Liquibase) | `src/main/resources/db/changelog/changes/001-create-users-table.yaml` |
| Dados de exemplo | `src/main/resources/db/changelog/changes/002-insert-sample-users.yaml` |
| Entidade JPA | `entity/UserEntity.java` |
| Repository | `repository/UserRepository.java` |
| Service | `service/UserService.java` |
| Controller | `api/v1/UserController.java` |
| DTOs (o que a API recebe/devolve) | `model/User.java`, `model/UserV2.java` |
| Testes | `UserControllerTest.java`, `LiquibaseMigrationTest.java` |

---

## 1. Banco (Liquibase)

Crie `003-create-account-table.yaml` e inclua no `db.changelog-master.yaml` **depois** dos que já existem.

Tabela `account`:

| Coluna | Tipo | Regras |
|---|---|---|
| `id` | BIGINT | chave primária, auto incremento (`pk_account`) |
| `user_id` | BIGINT | obrigatório, chave estrangeira para `users(id)` (`fk_account_user`) |
| `number` | VARCHAR(20) | obrigatório, único (`uk_account_number`) |
| `type` | VARCHAR(20) | obrigatório: `CHECKING` (corrente) ou `SAVINGS` (poupança) |
| `balance` | NUMERIC(15,2) | obrigatório, padrão `0` |
| `created_at` | TIMESTAMP WITH TIME ZONE | obrigatório, padrão `now()` |

Crie também `004-insert-sample-accounts.yaml` com **2 contas de exemplo** (`context: dev`) e um bloco `rollback`.

**Não edite** os arquivos `001` e `002`.

---

## 2. Camadas no Spring Boot

1. **Entidade** `AccountEntity` mapeando a tabela `account`.
   - Relação com o usuário: `@ManyToOne` para `UserEntity`.
   - `balance` como `BigDecimal` (nunca `double` para dinheiro).
   - `type` como `enum AccountType { CHECKING, SAVINGS }` com `@Enumerated(EnumType.STRING)`.
2. **Repository** `AccountRepository`, com um método extra para listar as contas de um usuário.
3. **DTOs** (records em `model/`):
   - `AccountRequest` (o que chega no POST/PUT), com validação (`@NotBlank`, `@NotNull`, `@PositiveOrZero`...);
   - `AccountResponse` (o que a API devolve). **Não devolva a entidade direto.**
4. **Service** `AccountService` com as regras:
   - não deixar criar conta para usuário que não existe;
   - não deixar repetir o número da conta.
5. **Controller** `AccountController` em `/api/accounts`.

---

## 3. Endpoints (CRUD)

| Método | Rota | Resultado esperado |
|---|---|---|
| `POST` | `/api/accounts` | cria → `201 Created` + header `Location` |
| `GET` | `/api/accounts/{id}` | `200` ou `404` |
| `GET` | `/api/accounts?userId=1` | lista as contas do usuário |
| `PUT` | `/api/accounts/{id}` | atualiza → `200` ou `404` |
| `DELETE` | `/api/accounts/{id}` | `204 No Content` ou `404` |

Erros:

- dados inválidos → `400`, com mensagem dizendo qual campo está errado;
- usuário inexistente → `404` (ou `422`, mas explique a escolha);
- número de conta repetido → `409 Conflict`.

---

## 4. Critérios de aceite

- [ ] `docker compose down -v && docker compose up -d postgres` e depois `./mvnw spring-boot:run` sobem sem erro.
- [ ] No log aparece o Liquibase rodando os changesets `003` e `004`.
- [ ] `select id from databasechangelog;` mostra os 4 changesets.
- [ ] O Hibernate sobe com `ddl-auto: validate` (ou seja: entidade e tabela batem).
- [ ] Todos os endpoints acima respondem com os status certos (mostre com `curl` ou Postman).
- [ ] Testes novos em `AccountControllerTest` cobrindo: criar, buscar, listar por usuário, atualizar, apagar,
      e pelo menos um caso de erro (`400`, `404` ou `409`).
- [ ] `./mvnw test` passa inteiro.

---

## 5. Bônus

- Changeset `005` adicionando uma coluna nova (ex.: `nickname`), **sem** editar o `003`. Depois, faça o rollback dele.
- Paginação no `GET /api/accounts` (`Pageable`).
- Escreva em 3 frases: por que `ddl-auto: validate` e não `update`?

---

## Entrega

Uma branch `feature/account` com um Pull Request para `main`. Na descrição do PR:
como rodar, os `curl` usados para testar e qualquer decisão que você tomou.

Adicione também uma pasta **Accounts** na collection do Postman
(`postman/spring-boot-study-base-project.postman_collection.json`), com uma requisição por endpoint
e testes de status, no mesmo padrão das pastas que já existem.
