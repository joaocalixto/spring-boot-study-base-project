# Spring Boot DevTools — como usar

> O DevTools já está no `pom.xml`. Este guia explica o que ele faz e como deixar
> o reinício automático funcionando no IntelliJ e no terminal.

---

## 1. O que o DevTools faz

Sem DevTools: você muda o código, para a aplicação, sobe de novo e espera tudo carregar.

Com DevTools: você muda o código, **compila**, e a aplicação **reinicia sozinha** em poucos segundos.

Ele é rápido porque separa as classes em duas partes:

- **bibliotecas** (Spring, Hibernate, driver do Postgres...) ficam carregadas e não são recarregadas;
- **o seu código** (`target/classes`) é jogado fora e carregado de novo a cada reinício.

Outras coisas que ele faz:

- desliga caches que atrapalham em desenvolvimento;
- mostra no log o que mudou na configuração automática do Spring depois de cada reinício.

**Ele só vale em desenvolvimento.** Quando a aplicação roda empacotada (`java -jar target/demo-0.0.1-SNAPSHOT.jar`),
o DevTools se desliga sozinho. Como a dependência é `optional`, ele também não vai para o jar final.

---

## 2. A regra de ouro: só reinicia quando COMPILA

O DevTools observa a pasta `target/classes`. Ele não olha o `.java`: olha o `.class`.

- Salvou o arquivo e nada aconteceu? **Faltou compilar.**
- Compilou e o log mostrou o Spring subindo de novo? Funcionou.

No log, o reinício aparece assim: a thread passa a se chamar **`restartedMain`** e o banner do Spring
aparece de novo.

---

## 3. Usando no IntelliJ

### Jeito manual (mais previsível)

1. Suba a aplicação pelo IntelliJ (botão ▶ em `DemoApplication`).
2. Mude o código.
3. **Build → Build Project** (`⌘ F9`).
4. A aplicação reinicia sozinha.

### Jeito automático (compila quando você salva ou troca de janela)

1. **Settings → Build, Execution, Deployment → Compiler**
   → marque **Build project automatically**.
2. **Settings → Advanced Settings**
   → marque **Allow auto-make to start even if developed application is currently running**.
3. Na configuração de execução (**Run → Edit Configurations… → DemoApplication**):
   - **On 'Update' action:** `Update classes and resources`
   - **On frame deactivation:** `Update classes and resources`
     (aparece em *Modify options*, se não estiver visível)

Com isso, ao sair da janela do IntelliJ (por exemplo, indo para o Postman), ele compila e a aplicação reinicia.

> O projeto precisa estar usando o **JDK 25** (File → Project Structure → Project → SDK).

---

## 4. Usando no terminal

Terminal 1 — sobe a aplicação:

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

Terminal 2 — depois de mudar o código:

```bash
./mvnw compile
```

O terminal 1 mostra a aplicação reiniciando.

---

## 5. O que reinicia e o que não reinicia

| Você mudou... | O que acontece |
|---|---|
| Uma classe Java (controller, service...) | Reinicia depois de compilar |
| `application.yml` | Reinicia depois de compilar (ele é copiado para `target/classes`) |
| Um changeset novo do Liquibase | Reinicia e o Liquibase roda o changeset novo |
| O `pom.xml` (dependência nova) | **Não basta.** Pare a aplicação, recarregue o Maven e suba de novo |
| Arquivos em `static/` ou `templates/` | Não reinicia (não precisa) |

---

## 6. Configurações úteis (`application.yml`)

```yaml
spring:
  devtools:
    restart:
      enabled: true                     # false desliga o reinício automático
      poll-interval: 1s                 # de quanto em quanto tempo olha as mudanças
      quiet-period: 400ms               # espera esse tempo sem mudanças antes de reiniciar
      exclude: "static/**,public/**"    # pastas que NÃO disparam reinício
      log-condition-evaluation-delta: true  # mostra no log o que mudou na configuração automática
```

Desligar só por uma execução, sem mexer em arquivo:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=false"
```

---

## 7. Problemas comuns

| Sintoma | Causa provável | O que fazer |
|---|---|---|
| Mudei o código e nada aconteceu | Não compilou | `⌘ F9` no IntelliJ ou `./mvnw compile` |
| Reinicia o tempo todo | Algo está gravando arquivos em `target/classes` sem parar | Veja o que muda com `ls -lt target/classes`; use `spring.devtools.restart.exclude` |
| `ClassCastException` estranha só em desenvolvimento (ex.: `X cannot be cast to X`) | A mesma classe carregada pelas duas partes do DevTools | Desligue o reinício (`restart.enabled: false`) para confirmar |
| Rodando com `java -jar` e o DevTools não funciona | É de propósito: ele se desliga em jar empacotado | Use o IntelliJ ou `./mvnw spring-boot:run` |

---

## 8. E o LiveReload?

O DevTools também tem o LiveReload, que recarrega a página do navegador sozinho.
Ele só faz sentido para páginas HTML, e este projeto é uma API REST. Além disso, a partir do
Spring Boot 4.1 ele foi marcado como obsoleto, sem substituto. Não usamos.

---

Documentação oficial: https://docs.spring.io/spring-boot/reference/using/devtools.html
