package br.com.conectsol.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.com.conectsol.backend.model.Alerta;
import br.com.conectsol.backend.model.Equipe;
import br.com.conectsol.backend.model.EquipeMembro;
import br.com.conectsol.backend.model.FuncaoEquipe;
import br.com.conectsol.backend.model.Lancamento;
import br.com.conectsol.backend.model.NivelAlerta;
import br.com.conectsol.backend.model.StatusAlerta;
import br.com.conectsol.backend.repository.AlertaRepository;
import br.com.conectsol.backend.repository.EquipeRepository;
import br.com.conectsol.backend.repository.LancamentoRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private LancamentoRepository lancamentoRepository;

    @Mock
    private EquipeRepository equipeRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    @Test
    void deveCalcularResumoPorEquipeComAlertasELancamentosMockados() {
        Equipe jose = equipeComMembros(1L, "JOSE", "CARLOS");

        List<Alerta> alertas = List.of(
                Alerta.builder().equipe(jose).nivel(NivelAlerta.ALTO).dataAlerta(LocalDate.now()).build(),
                Alerta.builder().equipe(jose).nivel(NivelAlerta.ALTO).dataAlerta(LocalDate.now()).build(),
                Alerta.builder().equipe(jose).nivel(NivelAlerta.MEDIO).dataAlerta(LocalDate.now()).build(),
                Alerta.builder().equipe(jose).nivel(NivelAlerta.LEVE).dataAlerta(LocalDate.now()).build());

        List<Lancamento> lancamentos = List.of(
                Lancamento.builder().equipe(jose).sistemas(6).dataLancamento(LocalDate.now()).build(),
                Lancamento.builder().equipe(jose).sistemas(6).dataLancamento(LocalDate.now()).build());

        when(equipeRepository.findAllById(any())).thenReturn(List.of(jose));

        var resultado = relatorioService.montarRelatorioEquipes(alertas, lancamentos);

        assertThat(resultado).hasSize(1);
        var relatorio = resultado.get(0);
        assertThat(relatorio.getMontador()).isEqualTo("JOSE");
        assertThat(relatorio.getEletricista()).isEqualTo("CARLOS");
        assertThat(relatorio.getAlto()).isEqualTo(2);
        assertThat(relatorio.getMedio()).isEqualTo(1);
        assertThat(relatorio.getLeve()).isEqualTo(1);
        assertThat(relatorio.getTotalAlertas()).isEqualTo(4);
        assertThat(relatorio.getPontos()).isEqualTo(2 * 5 + 1 * 3 + 1 * 1);
        assertThat(relatorio.getSistemas()).isEqualTo(12);
        assertThat(relatorio.getIndiceAlertasPorSistema()).isEqualTo(4.0 / 12.0);
    }

    @Test
    void naoDeveContarAlertasJustificadosOuResolvidosNosPontos() {
        Equipe jose = equipeComMembros(1L, "JOSE", "CARLOS");

        List<Alerta> alertas = List.of(
                Alerta.builder()
                        .equipe(jose)
                        .nivel(NivelAlerta.ALTO)
                        .status(StatusAlerta.ABERTO)
                        .dataAlerta(LocalDate.now())
                        .build(),
                Alerta.builder()
                        .equipe(jose)
                        .nivel(NivelAlerta.ALTO)
                        .status(StatusAlerta.JUSTIFICADO)
                        .dataAlerta(LocalDate.now())
                        .build(),
                Alerta.builder()
                        .equipe(jose)
                        .nivel(NivelAlerta.MEDIO)
                        .status(StatusAlerta.RESOLVIDO)
                        .dataAlerta(LocalDate.now())
                        .build());

        when(equipeRepository.findAllById(any())).thenReturn(List.of(jose));

        var resultado = relatorioService.montarRelatorioEquipes(alertas, List.of());

        assertThat(resultado).hasSize(1);
        var relatorio = resultado.get(0);
        assertThat(relatorio.getAlto()).isEqualTo(1);
        assertThat(relatorio.getMedio()).isZero();
        assertThat(relatorio.getTotalAlertas()).isEqualTo(1);
        assertThat(relatorio.getPontos()).isEqualTo(5);
    }

    @Test
    void deveRetornarIndiceZeroQuandoNaoHouverSistemasLancados() {
        Equipe maria = equipeComMembros(2L, "MARIA", "PEDRO");
        List<Alerta> alertas = List.of(
                Alerta.builder().equipe(maria).nivel(NivelAlerta.ALTO).dataAlerta(LocalDate.now()).build());

        when(equipeRepository.findAllById(any())).thenReturn(List.of(maria));

        var resultado = relatorioService.montarRelatorioEquipes(alertas, List.of());

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getSistemas()).isZero();
        assertThat(resultado.get(0).getIndiceAlertasPorSistema()).isZero();
    }

    @Test
    void deveMontarTendenciaMensalPreenchendoMesesSemAlertasComZero() {
        YearMonth atual = YearMonth.of(2026, 7);
        YearMonth inicio = atual.minusMonths(2);

        Equipe equipe = equipeComMembros(1L, "JOSE", null);
        List<Alerta> alertas = List.of(
                Alerta.builder().equipe(equipe).nivel(NivelAlerta.ALTO).dataAlerta(LocalDate.of(2026, 7, 5)).build(),
                Alerta.builder().equipe(equipe).nivel(NivelAlerta.LEVE).dataAlerta(LocalDate.of(2026, 5, 10)).build());

        var tendencia = relatorioService.montarTendencia(alertas, inicio, atual);

        assertThat(tendencia).hasSize(3);
        assertThat(tendencia.get(0).getMes()).isEqualTo("Mai/26");
        assertThat(tendencia.get(0).getLeve()).isEqualTo(1);
        assertThat(tendencia.get(1).getMes()).isEqualTo("Jun/26");
        assertThat(tendencia.get(1).getTotal()).isZero();
        assertThat(tendencia.get(2).getMes()).isEqualTo("Jul/26");
        assertThat(tendencia.get(2).getAlto()).isEqualTo(1);
    }

    private Equipe equipeComMembros(Long id, String montador, String eletricista) {
        Equipe equipe = Equipe.builder().id(id).build();
        equipe.getMembros()
                .add(EquipeMembro.builder().equipe(equipe).nome(montador).funcao(FuncaoEquipe.MONTADOR).build());
        if (eletricista != null) {
            equipe.getMembros()
                    .add(EquipeMembro.builder()
                            .equipe(equipe)
                            .nome(eletricista)
                            .funcao(FuncaoEquipe.ELETRICISTA)
                            .build());
        }
        return equipe;
    }
}
