package br.com.fiap.sentinela.dto;

import br.com.fiap.sentinela.model.Ocorrencia;
import java.time.LocalDate;

/** Dados de saida (evita expor a entidade JPA diretamente). */
public class OcorrenciaResponse {

    private Long id;
    private Long regiaoId;
    private String regiaoNome;
    private String tipo;
    private String severidade;
    private String fonteDeteccao;
    private String status;
    private LocalDate dataDeteccao;

    public static OcorrenciaResponse from(Ocorrencia o) {
        OcorrenciaResponse r = new OcorrenciaResponse();
        r.id = o.getId();
        r.regiaoId = o.getRegiao().getId();
        r.regiaoNome = o.getRegiao().getNome();
        r.tipo = o.getTipo().name();
        r.severidade = o.getSeveridade().name();
        r.fonteDeteccao = o.getFonteDeteccao().name();
        r.status = o.getStatus().name();
        r.dataDeteccao = o.getDataDeteccao();
        return r;
    }

    public Long getId() { return id; }
    public Long getRegiaoId() { return regiaoId; }
    public String getRegiaoNome() { return regiaoNome; }
    public String getTipo() { return tipo; }
    public String getSeveridade() { return severidade; }
    public String getFonteDeteccao() { return fonteDeteccao; }
    public String getStatus() { return status; }
    public LocalDate getDataDeteccao() { return dataDeteccao; }
}
