package br.com.conectsol.backend.controller;

import br.com.conectsol.backend.dto.AlertaDTO;
import br.com.conectsol.backend.dto.AlertaRequest;
import br.com.conectsol.backend.dto.AlertaStatusRequest;
import br.com.conectsol.backend.model.NivelAlerta;
import br.com.conectsol.backend.service.AlertaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    public List<AlertaDTO> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
            @RequestParam(required = false) NivelAlerta nivel,
            @RequestParam(required = false) Long equipeId) {
        return alertaService.listar(de, ate, nivel, equipeId);
    }

    @PostMapping
    public ResponseEntity<AlertaDTO> criar(@Valid @RequestBody AlertaRequest request) {
        return alertaService.criar(request)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/{id}/status")
    public AlertaDTO atualizarStatus(@PathVariable Long id, @Valid @RequestBody AlertaStatusRequest request) {
        return alertaService.atualizarStatus(id, request);
    }
}
