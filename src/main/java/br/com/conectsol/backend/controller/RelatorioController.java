package br.com.conectsol.backend.controller;

import br.com.conectsol.backend.dto.RelatorioEquipeDTO;
import br.com.conectsol.backend.dto.TendenciaMensalDTO;
import br.com.conectsol.backend.service.RelatorioService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorio")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/equipes")
    public List<RelatorioEquipeDTO> relatorioEquipes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) {
        return relatorioService.gerarRelatorioEquipes(de, ate);
    }

    @GetMapping("/tendencia")
    public List<TendenciaMensalDTO> tendencia(@RequestParam(defaultValue = "6") int meses) {
        return relatorioService.gerarTendencia(meses);
    }
}
