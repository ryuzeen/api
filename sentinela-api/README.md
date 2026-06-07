# Global Solution 2026/1 — Partes 1, 2 e 3

**Aluno:** [seu nome] — RM [seu RM]
**Projeto:** SENTINELA — plataforma de monitoramento e resposta a desastres naturais (dados de satélite + sensores IoT).

Este pacote cobre 3 das 6 partes do projeto: **Banco de Dados, API REST e Plano de Testes**. As outras partes (Mobile, Segurança e IoT) estão sob responsabilidade dos demais membros do grupo.

## Pré-requisitos

- **JDK 21** (essencial)
- Conexão com a internet na primeira execução (o Maven Wrapper baixa o Maven sozinho)

Não é necessário instalar Maven nem Oracle.

---

## Parte 1 — Banco de Dados

**Arquivos:** `modelo_banco_sentinela.sql`, `diagrama_er_sentinela.png`.

**Como rodar (mais rápido, sem instalar nada):**

1. Acesse **https://livesql.oracle.com** e entre com qualquer conta Oracle gratuita.
2. Abra uma **SQL Worksheet**.
3. Cole todo o conteúdo de `modelo_banco_sentinela.sql` e pressione **F5**.

Resultado: 7 tabelas criadas (REGIAO, SENSOR, LEITURA, OCORRENCIA, ALERTA, EQUIPE, ATENDIMENTO), dados de exemplo inseridos e as 5 consultas exibindo resultados no painel inferior. O script é idempotente — pode rodar várias vezes.

**Alternativa (Oracle SQL Developer, ambiente FIAP):** File → Open → o `.sql` → **F5** (Run Script).

---

## Parte 2 — API REST

**Stack:** Java 21 + Spring Boot 3 + Spring Data JPA + Bean Validation + Swagger.

**Como rodar (mais rápido, sem Oracle):**

Abra um terminal **na raiz da pasta `sentinela-api/`** (a que contém o arquivo `pom.xml`).

No **Windows PowerShell**:
```powershell
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"
```

No **Linux / macOS / Git Bash**:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Quando aparecer `Started SentinelaApplication`, a API está em `http://localhost:8080`.

- **Swagger UI:** http://localhost:8080/swagger-ui.html — testa cada endpoint visualmente.
- **Console do H2:** http://localhost:8080/h2-console — JDBC URL `jdbc:h2:mem:sentinela_dev`, usuário `sa`, senha em branco.

**Sequência mínima de verificação (no Swagger):**

1. `POST /api/regioes` → `{"nome":"Teste","uf":"SP","latitude":-23.5,"longitude":-46.6,"bioma":"Mata Atlantica"}` → **201**
2. `POST /api/ocorrencias` → `{"regiaoId":1,"tipo":"QUEIMADA","severidade":"ALTA","fonteDeteccao":"SENSOR","dataDeteccao":"2026-06-04"}` → **201**
3. `GET /api/ocorrencias` → **200**, lista com a ocorrência criada
4. `POST /api/ocorrencias` sem o campo `tipo` → **400** (validação de entrada)
5. `GET /api/ocorrencias/9999` → **404** (handler global de erro)

**Endpoints implementados (8 — o enunciado exige 5):**

| Método | Rota | Descrição |
|--------|------|-----------|
| GET    | /api/regioes        | Lista regiões |
| GET    | /api/regioes/{id}   | Busca região por id |
| POST   | /api/regioes        | Cadastra região |
| GET    | /api/ocorrencias        | Lista ocorrências |
| GET    | /api/ocorrencias/{id}   | Busca ocorrência por id |
| POST   | /api/ocorrencias        | Registra ocorrência |
| PUT    | /api/ocorrencias/{id}   | Atualiza ocorrência |
| DELETE | /api/ocorrencias/{id}   | Remove ocorrência |

**Camadas (slide 19):** `controller/` → `service/` → `repository/` (Spring Data JPA, queries parametrizadas — imune a SQL Injection). Erros tratados em `GlobalExceptionHandler` (404 para recurso inexistente, 400 com detalhes para validação).

**Como rodar contra o Oracle da Parte 1:**

1. Execute primeiro o script `modelo_banco_sentinela.sql` no Oracle.
2. Ajuste `spring.datasource.url`, `username` e `password` em `sentinela-api/src/main/resources/application.properties`.
3. ```powershell
   .\mvnw spring-boot:run
   ```
   (sem o parâmetro de perfil)

---

## Parte 3 — Plano de Testes

**Tipo:** testes automatizados (JUnit 5 + Spring MockMvc), contra H2 em memória — independem do Oracle.

**Como rodar:**

No **Windows PowerShell**:
```powershell
.\mvnw test
```

No **Linux / macOS / Git Bash**:
```bash
./mvnw test
```

Resultado esperado:

```
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Evidência completa: relatório Surefire em `target/surefire-reports/` após a execução.

**Casos de teste:**

| ID   | Cenário                                            | Status |
|------|----------------------------------------------------|--------|
| CT01 | POST /api/regioes com dados válidos                | **201** |
| CT02 | POST /api/ocorrencias com dados válidos            | **201** |
| CT03 | GET /api/ocorrencias/{id} para id existente        | **200** |
| CT04 | POST /api/ocorrencias sem campo obrigatório `tipo` | **400** |
| CT05 | GET /api/ocorrencias/{id} para id inexistente      | **404** |

Detalhes (entrada exata e asserções) em `PLANO_DE_TESTES.md` e nas `@DisplayName` da classe `OcorrenciaApiTest`. CT04 valida `@NotNull` + `@Valid`; CT05 valida o handler global.

O enunciado exige **5 casos** documentados e **3 execuções**; esta entrega documenta e executa **os 5** automaticamente em uma chamada de `.\mvnw test`.

---

## Resolução de problemas

**`mvnw` não é reconhecido / `mvn` não é reconhecido.**
Você não está na raiz do projeto. Confira que o terminal está em `sentinela-api/` (a pasta onde fica o arquivo `pom.xml`), não num subdiretório de `src/`. No PowerShell, use `cd C:\caminho\para\sentinela-api`.

**`JAVA_HOME` não definido ou versão errada.**
Garanta que `java -version` mostra **21** ou superior. Se mostrar outra versão, instale o JDK 21 e ajuste a variável `JAVA_HOME` apontando para a pasta dele.

**Porta 8080 ocupada.**
Adicione `--server.port=8081` ao comando de execução, ou pare o processo que está usando a 8080.

---

## Estrutura do pacote

```
├── README.md                       este arquivo
├── modelo_banco_sentinela.sql      Parte 1 - script DDL + dados + consultas
├── diagrama_er_sentinela.png       Parte 1 - diagrama ER (vai no PDF)
├── PLANO_DE_TESTES.md              Parte 3 - documento descritivo (vai no PDF)
└── sentinela-api/                  Parte 2 + classe de testes da Parte 3
    ├── mvnw, mvnw.cmd, .mvn/       Maven Wrapper (dispensa Maven instalado)
    ├── pom.xml
    └── src/
```

Para a entrega final (slide 24): `diagrama_er_sentinela.png` e `PLANO_DE_TESTES.md` entram no **documento PDF**; os demais arquivos entram no **código-fonte (.zip)**.
