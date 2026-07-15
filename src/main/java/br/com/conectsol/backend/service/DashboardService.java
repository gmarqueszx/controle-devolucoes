package br.com.conectsol.backend.service;

import br.com.conectsol.backend.dto.DashboardStatsDTO;
import br.com.conectsol.backend.model.Lancamento;
import br.com.conectsol.backend.model.NivelAlerta;
import br.com.conectsol.backend.repository.AlertaRepository;
import br.com.conectsol.backend.repository.LancamentoRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AlertaRepository alertaRepository;
    private final LancamentoRepository lancamentoRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDTO gerarStats(LocalDate de, LocalDate ate) {
        List<Lancamento> lancamentosPeriodo = lancamentoRepository.findByDataLancamentoBetween(de, ate);

        long sistemasPeriodo = lancamentosPeriodo.stream()
                .mapToLong(l -> l.getSistemas() == null ? 0 : l.getSistemas())
                .sum();

        List<Lancamento> comAproveitamentoInformado =
                lancamentosPeriodo.stream().filter(l -> l.getAproveitamento() != null).toList();
        Double percentualAproveitamento100 = comAproveitamentoInformado.isEmpty()
                ? null
                : 100.0
                        * comAproveitamentoInformado.stream().filter(l -> l.getAproveitamento() >= 1.0).count()
                        / comAproveitamentoInformado.size();

        long sobrasPendentes =
                lancamentoRepository.findByRetornouFalseOrderByDataLancamentoAsc().size();
        long alertasAltoPeriodo =
                alertaRepository.findByDataAlertaBetweenAndNivel(de, ate, NivelAlerta.ALTO).size();

        return DashboardStatsDTO.builder()
                .sistemasPeriodo(sistemasPeriodo)
                .percentualAproveitamento100(percentualAproveitamento100)
                .sobrasPendentes(sobrasPendentes)
                .alertasAltoPeriodo(alertasAltoPeriodo)
                .build();
    }
}
