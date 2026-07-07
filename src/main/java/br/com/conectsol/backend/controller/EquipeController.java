package br.com.conectsol.backend.controller;

import br.com.conectsol.backend.dto.EquipeDTO;
import br.com.conectsol.backend.dto.EquipeRequest;
import br.com.conectsol.backend.service.EquipeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/equipes")
@RequiredArgsConstructor
public class EquipeController {

    private final EquipeService equipeService;

    @GetMapping
    public List<EquipeDTO> listar() {
        return equipeService.listar();
    }

    @GetMapping("/{id}")
    public EquipeDTO buscarPorId(@PathVariable Long id) {
        return equipeService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<EquipeDTO> criar(@Valid @RequestBody EquipeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipeService.criar(request));
    }

    @PutMapping("/{id}")
    public EquipeDTO atualizar(@PathVariable Long id, @Valid @RequestBody EquipeRequest request) {
        return equipeService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        equipeService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
