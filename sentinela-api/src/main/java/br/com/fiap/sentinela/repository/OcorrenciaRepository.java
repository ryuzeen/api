package br.com.fiap.sentinela.repository;

import br.com.fiap.sentinela.model.Ocorrencia;
import br.com.fiap.sentinela.model.StatusOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    // Spring Data gera a query parametrizada (protegida contra SQL Injection)
    List<Ocorrencia> findByStatus(StatusOcorrencia status);
}
