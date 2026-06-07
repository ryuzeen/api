package br.com.fiap.sentinela.service;

import br.com.fiap.sentinela.exception.RecursoNaoEncontradoException;
import br.com.fiap.sentinela.model.Regiao;
import br.com.fiap.sentinela.repository.RegiaoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RegiaoService {

    private final RegiaoRepository repository;

    public RegiaoService(RegiaoRepository repository) {
        this.repository = repository;
    }

    public List<Regiao> listar() { return repository.findAll(); }

    public Regiao buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Regiao " + id + " nao encontrada"));
    }

    public Regiao criar(Regiao regiao) { return repository.save(regiao); }
}
