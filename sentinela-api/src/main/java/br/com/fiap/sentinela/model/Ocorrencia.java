package br.com.fiap.sentinela.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ocorrencia")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ocorrencia_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "regiao_id")
    private Regiao regiao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoOcorrencia tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Severidade severidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "fonte_deteccao", nullable = false, length = 10)
    private FonteDeteccao fonteDeteccao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusOcorrencia status = StatusOcorrencia.ABERTA;

    @Column(name = "data_deteccao", nullable = false)
    private LocalDate dataDeteccao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Regiao getRegiao() { return regiao; }
    public void setRegiao(Regiao regiao) { this.regiao = regiao; }
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
