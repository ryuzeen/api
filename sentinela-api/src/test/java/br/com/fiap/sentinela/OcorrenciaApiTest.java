package br.com.fiap.sentinela;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Suite de testes da API (Parte 3 - Plano de Testes).
 * Sobe a aplicacao inteira contra um banco H2 em memoria (profile "test"),
 * portanto exercita de verdade a cadeia Controller -> Service -> Repository -> Banco.
 *
 * Os metodos estao ordenados para montar o cenario (cria regiao -> cria ocorrencia)
 * antes dos testes que dependem desses dados.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OcorrenciaApiTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper json;

    private static Long regiaoId;
    private static Long ocorrenciaId;

    @Test @Order(1)
    @DisplayName("CT01 - POST /api/regioes deve criar regiao e retornar 201")
    void deveCriarRegiao() throws Exception {
        var corpo = Map.of(
            "nome", "Chapada dos Veadeiros",
            "uf", "GO",
            "latitude", -14.1,
            "longitude", -47.6,
            "bioma", "Cerrado"
        );
        var resposta = mockMvc.perform(post("/api/regioes")
                .contentType("application/json")
                .content(json.writeValueAsString(corpo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Chapada dos Veadeiros"))
                .andReturn();
        regiaoId = json.readTree(resposta.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test @Order(2)
    @DisplayName("CT02 - POST /api/ocorrencias deve registrar ocorrencia e retornar 201")
    void deveCriarOcorrencia() throws Exception {
        var corpo = Map.of(
            "regiaoId", regiaoId,
            "tipo", "QUEIMADA",
            "severidade", "ALTA",
            "fonteDeteccao", "SENSOR",
            "status", "EM_ATENDIMENTO",
            "dataDeteccao", "2026-05-20"
        );
        var resposta = mockMvc.perform(post("/api/ocorrencias")
                .contentType("application/json")
                .content(json.writeValueAsString(corpo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("QUEIMADA"))
                .andExpect(jsonPath("$.regiaoNome").value("Chapada dos Veadeiros"))
                .andReturn();
        ocorrenciaId = json.readTree(resposta.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test @Order(3)
    @DisplayName("CT03 - GET /api/ocorrencias/{id} deve retornar a ocorrencia criada com 200")
    void deveBuscarOcorrenciaPorId() throws Exception {
        mockMvc.perform(get("/api/ocorrencias/{id}", ocorrenciaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ocorrenciaId))
                .andExpect(jsonPath("$.severidade").value("ALTA"));
    }

    @Test @Order(4)
    @DisplayName("CT04 - POST /api/ocorrencias sem campo obrigatorio deve retornar 400")
    void deveRejeitarOcorrenciaInvalida() throws Exception {
        // 'tipo' ausente -> a validacao de entrada (@NotNull) deve barrar
        var corpoInvalido = Map.of(
            "regiaoId", regiaoId,
            "severidade", "ALTA",
            "fonteDeteccao", "SENSOR",
            "dataDeteccao", "2026-05-20"
        );
        mockMvc.perform(post("/api/ocorrencias")
                .contentType("application/json")
                .content(json.writeValueAsString(corpoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.tipo").exists());
    }

    @Test @Order(5)
    @DisplayName("CT05 - GET /api/ocorrencias/{id} inexistente deve retornar 404")
    void deveRetornar404ParaOcorrenciaInexistente() throws Exception {
        mockMvc.perform(get("/api/ocorrencias/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
