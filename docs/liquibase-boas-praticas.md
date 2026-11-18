# Liquibase — como funciona, boas práticas e comandos úteis

> Guia curto da aula. Leia junto com os arquivos em `src/main/resources/db/changelog/`.

---

## 1. O problema que o Liquibase resolve

Sem uma ferramenta de migração, cada pessoa (e cada ambiente) cria e altera tabelas "na mão".
Logo ninguém sabe qual é a versão certa do banco.

O Liquibase trata o **banco como código**:

- cada mudança no banco é um arquivo versionado no Git, junto com o código Java;
- a aplicação, ao subir, aplica sozinha as mudanças que ainda faltam;
- todo ambiente (sua máquina, testes, produção) chega no mesmo estado, na mesma ordem.

---

## 2. Como funciona (o essencial)

```
db.changelog-master.yaml        <- índice: lista os arquivos em ordem
  └── changes/001-create-users-table.yaml   <- 1 changeset
  └── changes/002-insert-sample-users.yaml  <- 1 changeset
```

- **Changelog**: o arquivo índice (`db.changelog-master.yaml`). Ele só inclui os outros.
- **Changeset**: uma unidade de mudança. É identificado por `id` + `author` + caminho do arquivo.
- **Tabela `DATABASECHANGELOG`**: o Liquibase cria essa tabela no banco e anota cada changeset que já rodou,
  com um **checksum** (uma "impressão digital" do conteúdo).
- **Tabela `DATABASECHANGELOGLOCK`**: uma trava para duas instâncias da aplicação não migrarem ao mesmo tempo.

Quando a aplicação sobe, o Spring Boot chama o Liquibase, que:

1. lê o changelog;
2. compara com a `DATABASECHANGELOG`;
3. roda só os changesets que ainda não rodaram, na ordem;
4. se um changeset que **já rodou** foi alterado, o checksum muda e ele **para com erro**. Isso é proposital.

Neste projeto:

- `spring.liquibase.change-log` aponta para o arquivo índice;
- `spring.jpa.hibernate.ddl-auto: validate` → o Hibernate **não cria tabelas**, só confere se as entidades
  batem com o banco. Quem manda no schema é o Liquibase.

---

## 3. Boas práticas

1. **Nunca edite um changeset que já rodou** (em qualquer ambiente que outra pessoa use).
   Precisa mudar algo? Crie um changeset novo (`ALTER`, `addColumn`, etc.).
2. **Um changeset = uma mudança pequena.** Se falhar no meio, fica fácil entender e corrigir.
3. **Um arquivo por mudança, com número na frente** (`003-create-account-table.yaml`)
   e inclua no master **explicitamente**, na ordem. Evite `includeAll`: a ordem fica implícita.
4. **`id` descritivo e `author` real.** Ex.: `id: 003-create-account-table`, `author: bea`.
5. **Dê nome às constraints** (`pk_account`, `uk_account_number`, `fk_account_user`).
   Nome gerado pelo banco é diferente em cada ambiente e atrapalha migrações futuras.
6. **Pense no rollback.** `createTable` e `addColumn` desfazem sozinhos.
   `insert`, `update`, `sql` **não**: escreva um bloco `rollback`.
7. **Separe estrutura de dados.** Dados de exemplo ficam em changesets com `context: dev`,
   para não irem para produção.
8. **Hibernate só valida** (`ddl-auto: validate`). Nunca use `update`/`create` junto com Liquibase.
9. **Mudanças perigosas em duas etapas.** Renomear ou apagar coluna usada pelo código:
   primeiro adicione a nova e faça o código usar as duas; só depois, em outro deploy, remova a antiga.
10. **Teste a migração.** Os testes deste projeto sobem um Postgres real (Testcontainers)
    e rodam todos os changesets do zero. Se a migração quebra, o teste quebra.

---

## 4. Comandos úteis

### Banco no Docker

```bash
docker compose up -d postgres          # sobe só o Postgres (porta 5433)
docker compose ps                      # vê se está "healthy"
docker compose logs -f postgres        # logs do banco
docker compose down                    # para (os dados ficam no volume)
docker compose down -v                 # para E APAGA os dados (banco volta a ficar vazio)
```

Entrar no banco pelo terminal:

```bash
docker exec -it bea-postgres psql -U bea -d beademo
```

Dentro do `psql`:

```sql
\dt                                                     -- lista as tabelas
\d users                                                -- mostra as colunas de users
select id, author, filename, orderexecuted, exectype
  from databasechangelog order by orderexecuted;        -- o que o Liquibase já rodou
select * from databasechangeloglock;                    -- trava (locked deve ser false)
\q                                                      -- sair
```

Interface gráfica: `docker compose up -d pgadmin` e abra http://localhost:5050.

### Aplicação

```bash
./mvnw spring-boot:run                 # sobe a app; o Liquibase roda no início (veja no log)
./mvnw test                            # testes com Postgres em container (Docker ligado)
curl localhost:8080/actuator/liquibase # lista os changesets aplicados, pela própria app
```

Rodar sem os dados de exemplo (como seria em produção):

```bash
LIQUIBASE_CONTEXTS=prod ./mvnw spring-boot:run
```

### Problemas comuns

| Sintoma | Causa provável | O que fazer |
|---|---|---|
| `Validation Failed: ... checksum changed` | Alguém editou um changeset que já rodou | Volte o arquivo ao original e crie um changeset novo. Na sua máquina, `docker compose down -v` também resolve (apaga tudo). |
| `Schema-validation: missing table [...]` | Entidade nova sem changeset, ou changeset fora do master | Crie o changeset e inclua no `db.changelog-master.yaml` |
| App travada em `Waiting for changelog lock` | Uma execução anterior caiu no meio | `update databasechangeloglock set locked = false;` |
| `Connection refused` na porta 5433 | Postgres do Docker não está rodando | `docker compose up -d postgres` |

---

## 5. Para ir além

- Documentação: https://docs.liquibase.com
- Changesets em SQL puro ("formatted SQL") são uma alternativa ao YAML, se o time preferir.
- O plugin Maven do Liquibase permite `status`, `rollback`, `diff` pela linha de comando,
  sem subir a aplicação.
