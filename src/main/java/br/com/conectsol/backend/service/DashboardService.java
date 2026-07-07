package br.com.conectsol.backend.service;

import br.com.conectsol.backend.dto.DashboardStatsDTO;
import br.com.conectsol.backend.repository.AlertaRepository;
import br.com.conectsol.backend.repository.EquipeRepository;
import br.com.conectsol.backend.repository.LancamentoRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AlertaRepository alertaRepository;
    private final LancamentoRepository lancamentoRepository;
    private final EquipeRepository equipeRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDTO gerarStats() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.minusDays(6);
        LocalDate inicioMes = hoje.withDayOfMonth(1);

        long alertasHoje = alertaRepository.countByDataAlerta(hoje);
        long alertasSemana = alertaRepository.findByDataAlertaBetween(inicioSemana, hoje).size();
        long alertasMes = alertaRepository.findByDataAlertaBetween(inicioMes, hoje).size();
        long equipesAtivas = equipeRepository.countByAtivaTrue();
        long sistemasMes = lancamentoRepository.somarSistemasNoPeriodo(inicioMes, hoje);

        return DashboardStatsDTO.builder()
                .alertasHoje(alertasHoje)
                .alertasSemana(alertasSemana)
                .alertasMes(alertasMes)
                .equipesAtivas(equipesAtivas)
                .sistemasMes(sistemasMes)
                .build();
    }
}
