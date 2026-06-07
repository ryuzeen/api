# Plano de Testes — API SENTINELA (Parte 3)

**Global Solution 2026/1 — Engenharia de Software**

Objetivo: validar o comportamento dos serviços REST da Parte 2, verificando respostas
corretas para entradas válidas, inválidas e inexistentes. Os testes são **automatizados**
(JUnit 5 + Spring MockMvc) e sobem a aplicação contra um banco **H2 em memória**, exercitando
a cadeia real Controller → Service → Repository → Banco.

Arquivo dos testes: `src/test/java/br/com/fiap/sentinela/OcorrenciaApiTest.java`

---

## Casos de teste

| ID | Cenário | Entrada | Saída esperada | Status HTTP |
|----|---------|---------|----------------|-------------|
| CT01 | Cadastrar uma região válida | POST `/api/regioes` com nome, uf, lat, long, bioma | Região criada, com `id` preenchido | **201 Created** |
| CT02 | Registrar uma ocorrência válida vinculada à região | POST `/api/ocorrencias` com `regiaoId`, tipo, severidade, fonte, data | Ocorrência criada, retornando `regiaoNome` resolvido | **201 Created** |
| CT03 | Consultar uma ocorrência existente por id | GET `/api/ocorrencias/{id}` com id válido | Ocorrência correspondente (severidade `ALTA`) | **200 OK** |
| CT04 | Rejeitar ocorrência sem campo obrigatório | POST `/api/ocorrencias` **sem** o campo `tipo` | Corpo de erro com `erros.tipo`; nada é persistido | **400 Bad Request** |
| CT05 | Consultar ocorrência inexistente | GET `/api/ocorrencias/999999` | Corpo de erro padronizado (`status: 404`) | **404 Not Found** |

CT04 valida a **validação de entrada** (`@NotNull` + `@Valid`).
CT05 valida o **tratamento de erro** centralizado (`GlobalExceptionHandler`).

---

## Execução e evidência

Comando:

```bash
mvn test
```

Resultado obtido (relatório Surefire):

```
-------------------------------------------------------------------------------
Test set: br.com.fiap.sentinela.OcorrenciaApiTest
-------------------------------------------------------------------------------
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 8.802 s

[INFO] BUILD SUCCESS
```

Os **5 casos** foram executados (o enunciado pede ao menos 3) e **todos passaram**.
O relatório completo fica em `target/surefire-reports/` após rodar `mvn test`.
