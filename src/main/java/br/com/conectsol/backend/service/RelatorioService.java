package br.com.conectsol.backend.service;

import br.com.conectsol.backend.dto.RelatorioEquipeDTO;
import br.com.conectsol.backend.dto.TendenciaMensalDTO;
import br.com.conectsol.backend.model.Alerta;
import br.com.conectsol.backend.model.Equipe;
import br.com.conectsol.backend.model.Lancamento;
import br.com.conectsol.backend.model.NivelAlerta;
import br.com.conectsol.backend.repository.AlertaRepository;
import br.com.conectsol.backend.repository.EquipeRepository;
import br.com.conectsol.backend.repository.LancamentoRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private static final String[] MESES_PT =
            {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

    private final AlertaRepository alertaRepository;
    private final LancamentoRepository lancamentoRepository;
    private final EquipeRepository equipeRepository;

    @Transactional(readOnly = true)
    public List<RelatorioEquipeDTO> gerarRelatorioEquipes(LocalDate de, LocalDate ate) {
        List<Alerta> alertas = alertaRepository.findByDataAlertaBetween(de, ate);
        List<Lancamento> lancamentos = lancamentoRepository.findByDataLancamentoBetween(de, ate);
        return montarRelatorioEquipes(alertas, lancamentos);
    }

    List<RelatorioEquipeDTO> montarRelatorioEquipes(List<Alerta> alertas, List<Lancamento> lancamentos) {
        Map<Long, List<Alerta>> alertasPorEquipe = alertas.stream()
                .collect(Collectors.groupingBy(a -> a.getEquipe().getId()));
        Map<Long, List<Lancamento>> lancamentosPorEquipe = lancamentos.stream()
                .collect(Collectors.groupingBy(l -> l.getEquipe().getId()));

        Set<Long> equipeIds = new LinkedHashSet<>();
        equipeIds.addAll(alertasPorEquipe.keySet());
        equipeIds.addAll(lancamentosPorEquipe.keySet());

        Map<Long, Equipe> equipesPorId = equipeRepository.findAllById(equipeIds).stream()
                .collect(Collectors.toMap(Equipe::getId, e -> e));

        List<RelatorioEquipeDTO> resultado = new ArrayList<>();
        for (Long equipeId : equipeIds) {
            Equipe equipe = equipesPorId.get(equipeId);
            List<Alerta> alertasEquipe = alertasPorEquipe.getOrDefault(equipeId, List.of());
            List<Lancamento> lancamentosEquipe = lancamentosPorEquipe.getOrDefault(equipeId, List.of());

            long alto = contarPorNivel(alertasEquipe, NivelAlerta.ALTO);
            long medio = contarPorNivel(alertasEquipe, NivelAlerta.MEDIO);
            long leve = contarPorNivel(alertasEquipe, NivelAlerta.LEVE);
            long total = alertasEquipe.size();
            long sistemas = lancamentosEquipe.stream()
                    .mapToLong(l -> l.getSistemas() == null ? 0 : l.getSistemas())
                    .sum();
            double indice = sistemas > 0 ? (double) total / sistemas : 0.0;

            resultado.add(RelatorioEquipeDTO.builder()
                    .montador(equipe != null ? equipe.getMontador() : null)
                    .eletricista(equipe != null ? equipe.getEletricista() : null)
                    .alto(alto)
                    .medio(medio)
                    .leve(leve)
                    .totalAlertas(total)
                    .sistemas(sistemas)
                    .indiceAlertasPorSistema(indice)
                    .build());
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<TendenciaMensalDTO> gerarTendencia(int meses) {
        YearMonth atual = YearMonth.now();
        YearMonth inicio = atual.minusMonths(meses - 1L);
        List<Alerta> alertas = alertaRepository.findByDataAlertaBetween(inicio.atDay(1), atual.atEndOfMonth());
        return montarTendencia(alertas, inicio, atual);
    }

    List<TendenciaMensalDTO> montarTendencia(List<Alerta> alertas, YearMonth inicio, YearMonth atual) {
        Map<YearMonth, List<Alerta>> alertasPorMes = alertas.stream()
                .collect(Collectors.groupingBy(a -> YearMonth.from(a.getDataAlerta())));

        List<TendenciaMensalDTO> resultado = new ArrayList<>();
        YearMonth cursor = inicio;
        while (!cursor.isAfter(atual)) {
            List<Alerta> alertasDoMes = alertasPorMes.getOrDefault(cursor, List.of());
            long alto = contarPorNivel(alertasDoMes, NivelAlerta.ALTO);
            long medio = contarPorNivel(alertasDoMes, NivelAlerta.MEDIO);
            long leve = contarPorNivel(alertasDoMes, NivelAlerta.LEVE);

            resultado.add(TendenciaMensalDTO.builder()
                    .mes(formatarMes(cursor))
                    .alto(alto)
                    .medio(medio)
                    .leve(leve)
                    .total(alto + medio + leve)
                    .build());

            cursor = cursor.plusMonths(1);
        }
        return resultado;
    }

    private long contarPorNivel(List<Alerta> alertas, NivelAlerta nivel) {
        return alertas.stream().filter(a -> a.getNivel() == nivel).count();
    }

    private String formatarMes(YearMonth yearMonth) {
        String mes = MESES_PT[yearMonth.getMonthValue() - 1];
        int anoDoSeculo = yearMonth.getYear() % 100;
        return mes + "/" + (anoDoSeculo < 10 ? "0" + anoDoSeculo : String.valueOf(anoDoSeculo));
    }
}
