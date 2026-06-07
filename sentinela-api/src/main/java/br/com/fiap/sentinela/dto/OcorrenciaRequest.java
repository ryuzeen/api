package br.com.fiap.sentinela.dto;

import br.com.fiap.sentinela.model.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Dados de entrada para criar/atualizar uma ocorrencia.
 *  As anotacoes @NotNull fazem a validacao de entrada (HTTP 400 se faltar campo). */
public class OcorrenciaRequest {

    @NotNull(message = "regiaoId e obrigatorio")
    private Long regiaoId;

    @NotNull(message = "tipo e obrigatorio")
    private TipoOcorrencia tipo;

    @NotNull(message = "severidade e obrigatoria")
    private Severidade severidade;

    @NotNull(message = "fonteDeteccao e obrigatoria")
    private FonteDeteccao fonteDeteccao;

    private StatusOcorrencia status;

    @NotNull(message = "dataDeteccao e obrigatoria")
    private LocalDate dataDeteccao;

    public Long getRegiaoId() { return regiaoId; }
    public void setRegiaoId(Long regiaoId) { this.regiaoId = regiaoId; }
    public TipoOcorrencia getTipo() { return tipo; }
    public void setTipo(TipoOcorrencia tipo) { this.tipo = tipo; }
    public Severidade getSeveridade() { return severidade; }
    public void setSeveridade(Severidade severidade) { this.severidade = severidade; }
    public FonteDeteccao getFonteDeteccao() { return fonteDeteccao; }
    public void setFonteDeteccao(FonteDeteccao fonteDeteccao) { this.fonteDeteccao = fonteDeteccao; }
    public StatusOcorrencia getStatus() { return status; }
    public void setStatus(StatusOcorrencia status) { this.status = status; }
    public LocalDate getDataDeteccao() { return dataDeteccao; }
    public void setDataDeteccao(LocalDate dataDeteccao) { this.dataDeteccao = dataDeteccao; }
}
