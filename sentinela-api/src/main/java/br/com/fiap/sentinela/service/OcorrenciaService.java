package br.com.fiap.sentinela.service;

import br.com.fiap.sentinela.dto.OcorrenciaRequest;
import br.com.fiap.sentinela.exception.RecursoNaoEncontradoException;
import br.com.fiap.sentinela.model.*;
import br.com.fiap.sentinela.repository.OcorrenciaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final RegiaoService regiaoService;

    public OcorrenciaService(OcorrenciaRepository repository, RegiaoService regiaoService) {
        this.repository = repository;
        this.regiaoService = regiaoService;
    }

    public List<Ocorrencia> listar() { return repository.findAll(); }

    public Ocorrencia buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrencia " + id + " nao encontrada"));
    }

    public Ocorrencia criar(OcorrenciaRequest req) {
        Ocorrencia o = new Ocorrencia();
        aplicar(o, req);
        return repository.save(o);
    }

    public Ocorrencia atualizar(Long id, OcorrenciaRequest req) {
        Ocorrencia o = buscarPorId(id);
        aplicar(o, req);
        return repository.save(o);
    }

    public void excluir(Long id) {
        Ocorrencia o = buscarPorId(id);
        repository.delete(o);
    }

    private void aplicar(Ocorrencia o, OcorrenciaRequest req) {
        Regiao regiao = regiaoService.buscarPorId(req.getRegiaoId());
        o.setRegiao(regiao);
        o.setTipo(req.getTipo());
        o.setSeveridade(req.getSeveridade());
        o.setFonteDeteccao(req.getFonteDeteccao());
        if (req.getStatus() != null) o.setStatus(req.getStatus());
        o.setDataDeteccao(req.getDataDeteccao());
    }
}
