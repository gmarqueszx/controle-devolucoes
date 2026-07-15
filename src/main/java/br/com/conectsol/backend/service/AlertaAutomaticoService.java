package br.com.conectsol.backend.service;

import br.com.conectsol.backend.model.Alerta;
import br.com.conectsol.backend.model.Lancamento;
import br.com.conectsol.backend.model.NivelAlerta;
import br.com.conectsol.backend.repository.AlertaRepository;
import br.com.conectsol.backend.repository.LancamentoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlertaAutomaticoService {

    private static final double LIMITE_ACIMA_MEDIA = 1.05;
    static final String ORIGEM_CABO_ACIMA_MEDIA = "CABO_ACIMA_MEDIA";
    static final String ORIGEM_APROVEITAMENTO_BAIXO = "APROVEITAMENTO_BAIXO";

    private final AlertaRepository alertaRepository;
    private final LancamentoRepository lancamentoRepository;

    @Transactional
    public void avaliar(Lancamento lancamento) {
        avaliarDesvioDeCabo(lancamento);
        avaliarAproveitamentoBaixo(lancamento);
    }

    private void avaliarDesvioDeCabo(Lancamento lancamento) {
        removerAlertaAutomaticoExistente(lancamento, ORIGEM_CABO_ACIMA_MEDIA);

        boolean retornou = Boolean.TRUE.equals(lancamento.getRetornou());
        Integer strings = lancamento.getStrings();
        if (!retornou
                || lancamento.getCaboSolarVermUsado() == null
                || lancamento.getCaboSolarPretoUsado() == null
                || lancamento.getCaboHeprUsado() == null
                || lancamento.getTipoSistema() == null
                || strings == null
                || strings <= 0) {
            return;
        }

        // Vermelho e preto são cabo solar: comparados por string, já que instalações maiores usam mais strings e
        // mais cabo no total. HEPR não varia por string, então é comparado pelo total do lançamento. Solo usa
        // base de cálculo diferente do telhado (60m/string vs 30m/string), por isso a média é separada também
        // por solo/telhado, além de projeto/ampliação.
        double vermPorString = lancamento.getCaboSolarVermUsado() / strings;
        double pretoPorString = lancamento.getCaboSolarPretoUsado() / strings;
        double heprTotal = lancamento.getCaboHeprUsado();
        boolean solo = Boolean.TRUE.equals(lancamento.getSolo());

        List<Object[]> resultado =
                lancamentoRepository.calcularMediasUso(lancamento.getTipoSistema(), lancamento.getId(), solo);
        Object[] medias = resultado.isEmpty() ? null : resultado.get(0);
        Double mediaVermPorString = medias != null ? (Double) medias[0] : null;
        Double mediaPretoPorString = medias != null ? (Double) medias[1] : null;
        Double mediaHeprTotal = medias != null ? (Double) medias[2] : null;

        StringBuilder descricao = new StringBuilder();
        adicionarSeAcimaDaMedia(descricao, "vermelho", vermPorString, mediaVermPorString, "m/string");
        adicionarSeAcimaDaMedia(descricao, "preto", pretoPorString, mediaPretoPorString, "m/string");
        adicionarSeAcimaDaMedia(descricao, "HEPR", heprTotal, mediaHeprTotal, "m");

        if (descricao.length() == 0) {
            return;
        }

        criarAlertaAutomatico(
                lancamento, ORIGEM_CABO_ACIMA_MEDIA, NivelAlerta.MEDIO, "Consumo de cabo acima da média: " + descricao);
    }

    private void avaliarAproveitamentoBaixo(Lancamento lancamento) {
        removerAlertaAutomaticoExistente(lancamento, ORIGEM_APROVEITAMENTO_BAIXO);

        Double aproveitamento = lancamento.getAproveitamento();
        if (aproveitamento == null || aproveitamento >= 1.0) {
            return;
        }

        String descricao = String.format("Aproveitamento abaixo de 100%%: %.1f%%", aproveitamento * 100);
        criarAlertaAutomatico(lancamento, ORIGEM_APROVEITAMENTO_BAIXO, NivelAlerta.LEVE, descricao);
    }

    private void adicionarSeAcimaDaMedia(StringBuilder descricao, String cor, Double usado, Double media, String unidade) {
        if (media == null || media == 0 || usado == null) {
            return;
        }
        if (usado > media * LIMITE_ACIMA_MEDIA) {
            double percentualAcima = ((usado / media) - 1) * 100;
            if (descricao.length() > 0) {
                descricao.append("; ");
            }
            descricao.append(String.format(
                    "%s %.1f%s (%.1f%% acima da média de %.1f%s)", cor, usado, unidade, percentualAcima, media, unidade));
        }
    }

    private void removerAlertaAutomaticoExistente(Lancamento lancamento, String origem) {
        List<Alerta> existentes = alertaRepository.findByLancamentoIdAndOrigem(lancamento.getId(), origem);
        if (!existentes.isEmpty()) {
            alertaRepository.deleteAll(existentes);
        }
    }

    private void criarAlertaAutomatico(Lancamento lancamento, String origem, NivelAlerta nivel, String descricao) {
        Alerta alerta = Alerta.builder()
                .equipe(lancamento.getEquipe())
                .lancamento(lancamento)
                .dataAlerta(lancamento.getDataLancamento())
                .descricao(descricao)
                .nivel(nivel)
                .origem(origem)
                .build();
        alertaRepository.save(alerta);
    }
}
