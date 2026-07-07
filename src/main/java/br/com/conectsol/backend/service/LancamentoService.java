package br.com.conectsol.backend.service;

import br.com.conectsol.backend.dto.InversorDTO;
import br.com.conectsol.backend.dto.LancamentoDTO;
import br.com.conectsol.backend.dto.LancamentoRequest;
import br.com.conectsol.backend.exception.RecursoNaoEncontradoException;
import br.com.conectsol.backend.model.Equipe;
import br.com.conectsol.backend.model.Lancamento;
import br.com.conectsol.backend.model.LancamentoInversor;
import br.com.conectsol.backend.repository.LancamentoRepository;
import br.com.conectsol.backend.service.calculo.CaboCalculator;
import br.com.conectsol.backend.service.calculo.Inversor;
import br.com.conectsol.backend.service.calculo.ResultadoCabo;
import br.com.conectsol.backend.service.calculo.StringCalculator;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final EquipeService equipeService;
    private final StringCalculator stringCalculator;
    private final CaboCalculator caboCalculator;

    @Transactional(readOnly = true)
    public List<LancamentoDTO> listar(LocalDate de, LocalDate ate, Long equipeId) {
        List<Lancamento> lancamentos = equipeId != null
                ? lancamentoRepository.findByDataLancamentoBetweenAndEquipeId(de, ate, equipeId)
                : lancamentoRepository.findByDataLancamentoBetween(de, ate);
        return lancamentos.stream().map(this::paraDTO).toList();
    }

    @Transactional(readOnly = true)
    public LancamentoDTO buscarPorId(Long id) {
        return paraDTO(buscarEntidade(id));
    }

    @Transactional
    public LancamentoDTO criar(LancamentoRequest request) {
        Equipe equipe = equipeService.buscarEntidade(request.getEquipeId());

        List<InversorDTO> inversoresRequest = request.getInversores() != null ? request.getInversores() : List.of();
        List<Inversor> inversoresCalculo = inversoresRequest.stream()
                .map(i -> new Inversor(i.getKw(), i.getQuantidade()))
                .toList();

        int placas = request.getPlacas() != null ? request.getPlacas() : 0;
        int strings = stringCalculator.calcular(placas, inversoresCalculo);
        int qtdInversores = inversoresCalculo.stream().mapToInt(Inversor::qtd).sum();

        ResultadoCabo caboEnviado = caboCalculator.calcular(request.getTelhado(), strings, qtdInversores);
        double ajusteFino = request.getAjusteFino() != null ? request.getAjusteFino() : 0;

        double caboVermEnviado = caboEnviado.verm() + ajusteFino;
        double caboPretoEnviado = caboEnviado.preto() + ajusteFino;
        double caboHeprEnviado = caboEnviado.hepr() + ajusteFino;

        Double caboVermDevolvido = request.getCaboSolarVermDevolvido();
        Double caboPretoDevolvido = request.getCaboSolarPretoDevolvido();
        Double caboHeprDevolvido = request.getCaboHeprDevolvido();

        Lancamento lancamento = Lancamento.builder()
                .equipe(equipe)
                .dataLancamento(request.getDataLancamento())
                .cliente(request.getCliente())
                .sistemas(request.getSistemas())
                .observacoes(request.getObservacoes())
                .criadoPor(request.getCriadoPor())
                .retornou(request.getRetornou())
                .tipoSistema(request.getTipoSistema())
                .telhado(request.getTelhado())
                .placas(request.getPlacas())
                .strings(strings)
                .caboSolarVermEnviado(caboVermEnviado)
                .caboSolarPretoEnviado(caboPretoEnviado)
                .caboHeprEnviado(caboHeprEnviado)
                .caboSolarVermDevolvido(caboVermDevolvido)
                .caboSolarPretoDevolvido(caboPretoDevolvido)
                .caboHeprDevolvido(caboHeprDevolvido)
                .caboSolarVermUsado(subtrairSeInformado(caboVermEnviado, caboVermDevolvido))
                .caboSolarPretoUsado(subtrairSeInformado(caboPretoEnviado, caboPretoDevolvido))
                .caboHeprUsado(subtrairSeInformado(caboHeprEnviado, caboHeprDevolvido))
                .qtdMateriaisEnviados(request.getQtdMateriaisEnviados())
                .qtdMateriaisDivergentes(request.getQtdMateriaisDivergentes())
                .aproveitamento(
                        calcularAproveitamento(request.getQtdMateriaisEnviados(), request.getQtdMateriaisDivergentes()))
                .fotoSobrasGrupo(request.getFotoSobrasGrupo())
                .ajusteFino(request.getAjusteFino())
                .build();

        List<LancamentoInversor> inversoresEntidade = inversoresRequest.stream()
                .map(i -> LancamentoInversor.builder()
                        .lancamento(lancamento)
                        .kw(i.getKw())
                        .quantidade(i.getQuantidade())
                        .build())
                .toList();
        lancamento.getInversores().addAll(inversoresEntidade);

        return paraDTO(lancamentoRepository.save(lancamento));
    }

    @Transactional
    public void excluir(Long id) {
        Lancamento lancamento = buscarEntidade(id);
        lancamentoRepository.delete(lancamento);
    }

    Lancamento buscarEntidade(Long id) {
        return lancamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lancamento nao encontrado"));
    }

    private Double subtrairSeInformado(double enviado, Double devolvido) {
        return devolvido != null ? enviado - devolvido : null;
    }

    private Double calcularAproveitamento(Integer enviados, Integer divergentes) {
        if (enviados == null || enviados == 0 || divergentes == null) {
            return null;
        }
        return (enviados - divergentes) / (double) enviados;
    }

    private LancamentoDTO paraDTO(Lancamento lancamento) {
        List<InversorDTO> inversores = lancamento.getInversores().stream()
                .map(i -> InversorDTO.builder().kw(i.getKw()).quantidade(i.getQuantidade()).build())
                .toList();

        return LancamentoDTO.builder()
                .id(lancamento.getId())
                .equipeId(lancamento.getEquipe().getId())
                .montador(lancamento.getEquipe().getMontador())
                .eletricista(lancamento.getEquipe().getEletricista())
                .dataLancamento(lancamento.getDataLancamento())
                .cliente(lancamento.getCliente())
                .sistemas(lancamento.getSistemas())
                .observacoes(lancamento.getObservacoes())
                .criadoPor(lancamento.getCriadoPor())
                .retornou(lancamento.getRetornou())
                .tipoSistema(lancamento.getTipoSistema())
                .telhado(lancamento.getTelhado())
                .placas(lancamento.getPlacas())
                .strings(lancamento.getStrings())
                .inversores(inversores)
                .caboSolarVermEnviado(lancamento.getCaboSolarVermEnviado())
                .caboSolarPretoEnviado(lancamento.getCaboSolarPretoEnviado())
                .caboHeprEnviado(lancamento.getCaboHeprEnviado())
                .caboSolarVermDevolvido(lancamento.getCaboSolarVermDevolvido())
                .caboSolarPretoDevolvido(lancamento.getCaboSolarPretoDevolvido())
                .caboHeprDevolvido(lancamento.getCaboHeprDevolvido())
                .caboSolarVermUsado(lancamento.getCaboSolarVermUsado())
                .caboSolarPretoUsado(lancamento.getCaboSolarPretoUsado())
                .caboHeprUsado(lancamento.getCaboHeprUsado())
                .qtdMateriaisEnviados(lancamento.getQtdMateriaisEnviados())
                .qtdMateriaisDivergentes(lancamento.getQtdMateriaisDivergentes())
                .aproveitamento(lancamento.getAproveitamento())
                .fotoSobrasGrupo(lancamento.getFotoSobrasGrupo())
                .ajusteFino(lancamento.getAjusteFino())
                .build();
    }
}
