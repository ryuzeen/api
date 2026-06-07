package br.com.fiap.sentinela.controller;

import br.com.fiap.sentinela.dto.OcorrenciaRequest;
import br.com.fiap.sentinela.dto.OcorrenciaResponse;
import br.com.fiap.sentinela.model.Ocorrencia;
import br.com.fiap.sentinela.service.OcorrenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
@Tag(name = "Ocorrencias", description = "Eventos de desastre detectados nas regioes")
public class OcorrenciaController {

    private final OcorrenciaService service;

    public OcorrenciaController(OcorrenciaService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Lista todas as ocorrencias")
    public List<OcorrenciaResponse> listar() {
        return service.listar().stream().map(OcorrenciaResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma ocorrencia por id")
    public OcorrenciaResponse buscar(@PathVariable Long id) {
        return OcorrenciaResponse.from(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registra uma nova ocorrencia")
    public ResponseEntity<OcorrenciaResponse> criar(@Valid @RequestBody OcorrenciaRequest req) {
        Ocorrencia o = service.criar(req);
        return ResponseEntity
            .created(URI.create("/api/ocorrencias/" + o.getId()))
            .body(OcorrenciaResponse.from(o));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma ocorrencia existente")
    public OcorrenciaResponse atualizar(@PathVariable Long id, @Valid @RequestBody OcorrenciaRequest req) {
        return OcorrenciaResponse.from(service.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma ocorrencia")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
