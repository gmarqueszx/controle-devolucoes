package br.com.conectsol.backend.controller;

import br.com.conectsol.backend.dto.LancamentoDTO;
import br.com.conectsol.backend.dto.LancamentoRequest;
import br.com.conectsol.backend.dto.MediaCaboUsoDTO;
import br.com.conectsol.backend.service.LancamentoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    private final LancamentoService lancamentoService;

    @GetMapping
    public List<LancamentoDTO> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
            @RequestParam(required = false) Long equipeId) {
        return lancamentoService.listar(de, ate, equipeId);
    }

    @GetMapping("/{id}")
    public LancamentoDTO buscarPorId(@PathVariable Long id) {
        return lancamentoService.buscarPorId(id);
    }

    @GetMapping("/sobras-pendentes")
    public List<LancamentoDTO> listarSobrasPendentes() {
        return lancamentoService.listarSobrasPendentes();
    }

    @GetMapping("/medias-uso")
    public List<MediaCaboUsoDTO> consultarMediasUso() {
        return lancamentoService.consultarMediasUso();
    }

    @PostMapping
    public ResponseEntity<LancamentoDTO> criar(@Valid @RequestBody LancamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoService.criar(request));
    }

    @PostMapping("/recalcular-alertas")
    public ResponseEntity<Void> recalcularAlertas() {
        lancamentoService.recalcularAlertas();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public LancamentoDTO atualizar(@PathVariable Long id, @Valid @RequestBody LancamentoRequest request) {
        return lancamentoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        lancamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
