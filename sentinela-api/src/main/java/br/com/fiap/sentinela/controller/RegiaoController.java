package br.com.fiap.sentinela.controller;

import br.com.fiap.sentinela.model.Regiao;
import br.com.fiap.sentinela.service.RegiaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/regioes")
@Tag(name = "Regioes", description = "Regioes monitoradas via satelite e sensores")
public class RegiaoController {

    private final RegiaoService service;

    public RegiaoController(RegiaoService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Lista todas as regioes")
    public List<Regiao> listar() { return service.listar(); }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma regiao por id")
    public Regiao buscar(@PathVariable Long id) { return service.buscarPorId(id); }

    @PostMapping
    @Operation(summary = "Cadastra uma nova regiao")
    public ResponseEntity<Regiao> criar(@RequestBody Regiao regiao) {
        Regiao salva = service.criar(regiao);
        return ResponseEntity.created(URI.create("/api/regioes/" + salva.getId())).body(salva);
    }
}
