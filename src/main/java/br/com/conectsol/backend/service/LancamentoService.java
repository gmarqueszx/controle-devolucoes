package br.com.conectsol.backend.service;

import br.com.conectsol.backend.dto.InversorDTO;
import br.com.conectsol.backend.dto.LancamentoDTO;
import br.com.conectsol.backend.dto.LancamentoRequest;
import br.com.conectsol.backend.dto.MediaCaboUsoDTO;
import br.com.conectsol.backend.exception.RecursoNaoEncontradoException;
import br.com.conectsol.backend.model.Equipe;
import br.com.conectsol.backend.model.Lancamento;
import br.com.conectsol.backend.model.LancamentoInversor;
import br.com.conectsol.backend.repository.LancamentoRepository;
import br.com.conectsol.backend.service.calculo.CaboCalculator;
import br.com.conectsol.backend.service.calculo.Inversor;
import br.com.conectsol.backend.service.calculo.MaterialSistemaCalculator;
import br.com.conectsol.backend.service.calculo.ResultadoCabo;
import br.com.conectsol.backend.service.calculo.StringCalculator;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private final MaterialSistemaCalculator materialSistemaCalculator;
    private final AlertaAutomaticoService alertaAutomaticoService;

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

    @Transactional(readOnly = true)
    public List<LancamentoDTO> listarSobrasPendentes() {
        return lancamentoRepository.findByRetornouFalseOrderByDataLancamentoAsc().stream()
                .map(this::paraDTO)
                .toList();
    }

    /**
     * Reavalia os alertas automáticos (desvio de cabo/aproveitamento) de todos os lançamentos já retornados.
     * Útil para aplicar a regra a lançamentos que existiam antes dessa funcionalidade, ou depois de mudar limites.
     */
    @Transactional
    public int recalcularAlertas() {
        List<Lancamento> retornados = lancamentoRepository.findByRetornouTrue();
        retornados.forEach(alertaAutomaticoService::avaliar);
        return retornados.size();
    }

    /**
     * Médias de uso (por string para vermelho/preto, total para HEPR) usadas para gerar os alertas automáticos.
     * Separadas por tipo de sistema e por solo/telhado, já que usam bases de cálculo diferentes.
     */
    @Transactional(readOnly = true)
    public List<MediaCaboUsoDTO> consultarMediasUso() {
        List<MediaCaboUsoDTO> medias = new java.util.ArrayList<>();
        for (String tipoSistema : List.of("PROJETO", "AMPLIAÇÃO")) {
            for (boolean solo : List.of(false, true)) {
                medias.add(consultarMediaPorGrupo(tipoSistema, solo));
            }
        }
        return medias;
    }

    private MediaCaboUsoDTO consultarMediaPorGrupo(String tipoSistema, boolean solo) {
        List<Object[]> resultado = lancamentoRepository.consultarMediasUso(tipoSistema, solo);
        Object[] linha = resultado.isEmpty() ? null : resultado.get(0);
        long amostras = linha != null ? (Long) linha[0] : 0L;

        return MediaCaboUsoDTO.builder()
                .tipoSistema(tipoSistema)
                .solo(solo)
                .amostras(amostras)
                .mediaSolarVermPorString(linha != null ? (Double) linha[1] : null)
                .mediaSolarPretoPorString(linha != null ? (Double) linha[2] : null)
                .mediaHeprTotal(linha != null ? (Double) linha[3] : null)
                .build();
    }

    @Transactional
    public LancamentoDTO criar(LancamentoRequest request) {
        Lancamento lancamento = new Lancamento();
        lancamento.setInversores(new java.util.ArrayList<>());
        montarLancamento(lancamento, request);
        Lancamento salvo = lancamentoRepository.save(lancamento);
        alertaAutomaticoService.avaliar(salvo);
        return paraDTO(salvo);
    }

    @Transactional
    public LancamentoDTO atualizar(Long id, LancamentoRequest request) {
        Lancamento lancamento = buscarEntidade(id);
        montarLancamento(lancamento, request);
        Lancamento salvo = lancamentoRepository.save(lancamento);
        alertaAutomaticoService.avaliar(salvo);
        return paraDTO(salvo);
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

    private void montarLancamento(Lancamento lancamento, LancamentoRequest request) {
        Equipe equipe = equipeService.buscarOuCriarPorMembros(
                request.getMontador(), request.getEletricista(), request.getAjudante());

        List<InversorDTO> inversoresRequest = request.getInversores() != null ? request.getInversores() : List.of();
        List<Inversor> inversoresCalculo = inversoresRequest.stream()
                .map(i -> new Inversor(i.getKw(), i.getQuantidade()))
                .toList();

        int placas = request.getPlacas() != null ? request.getPlacas() : 0;
        int strings = stringCalculator.calcular(placas, inversoresCalculo);
        int qtdInversores = inversoresCalculo.stream().mapToInt(Inversor::qtd).sum();

        ResultadoCabo caboEnviado = caboCalculator.calcular(
                Boolean.TRUE.equals(request.getSolo()), request.getTipoSistema(), strings, qtdInversores);

        double ajusteFinoVerm = request.getAjusteFinoVerm() != null ? request.getAjusteFinoVerm() : 0;
        double ajusteFinoPreto = request.getAjusteFinoPreto() != null ? request.getAjusteFinoPreto() : 0;
        double ajusteFinoHepr = request.getAjusteFinoHepr() != null ? request.getAjusteFinoHepr() : 0;

        double caboVermEnviado = caboEnviado.verm() + ajusteFinoVerm;
        double caboPretoEnviado = caboEnviado.preto() + ajusteFinoPreto;
        double caboHeprEnviado = caboEnviado.hepr() + ajusteFinoHepr;

        Double caboVermDevolvido = request.getCaboSolarVermDevolvido();
        Double caboPretoDevolvido = request.getCaboSolarPretoDevolvido();
        Double caboHeprDevolvido = request.getCaboHeprDevolvido();

        Integer qtdMateriaisEnviados = materialSistemaCalculator
                .calcular(request.getTipoSistema())
                .orElse(request.getQtdMateriaisEnviados());

        lancamento.setEquipe(equipe);
        lancamento.setDataLancamento(request.getDataLancamento());
        lancamento.setCliente(request.getCliente());
        lancamento.setSistemas(request.getSistemas());
        lancamento.setObservacoes(request.getObservacoes());
        lancamento.setCriadoPor(request.getCriadoPor());
        lancamento.setRetornou(request.getRetornou());
        lancamento.setTipoSistema(request.getTipoSistema());
        lancamento.setSolo(request.getSolo());
        lancamento.setPlacas(request.getPlacas());
        lancamento.setStrings(strings);
        lancamento.setCaboSolarVermEnviado(caboVermEnviado);
        lancamento.setCaboSolarPretoEnviado(caboPretoEnviado);
        lancamento.setCaboHeprEnviado(caboHeprEnviado);
        lancamento.setCaboSolarVermDevolvido(caboVermDevolvido);
        lancamento.setCaboSolarPretoDevolvido(caboPretoDevolvido);
        lancamento.setCaboHeprDevolvido(caboHeprDevolvido);
        lancamento.setCaboSolarVermUsado(subtrairSeInformado(caboVermEnviado, caboVermDevolvido));
        lancamento.setCaboSolarPretoUsado(subtrairSeInformado(caboPretoEnviado, caboPretoDevolvido));
        lancamento.setCaboHeprUsado(subtrairSeInformado(caboHeprEnviado, caboHeprDevolvido));
        lancamento.setQtdMateriaisEnviados(qtdMateriaisEnviados);
        lancamento.setQtdMateriaisDivergentes(request.getQtdMateriaisDivergentes());
        lancamento.setAproveitamento(calcularAproveitamento(qtdMateriaisEnviados, request.getQtdMateriaisDivergentes()));
        lancamento.setFotoSobrasGrupo(request.getFotoSobrasGrupo());
        lancamento.setAjusteFinoVerm(request.getAjusteFinoVerm());
        lancamento.setAjusteFinoPreto(request.getAjusteFinoPreto());
        lancamento.setAjusteFinoHepr(request.getAjusteFinoHepr());
        lancamento.setLocalizacaoSobra(request.getLocalizacaoSobra());

        lancamento.getInversores().clear();
        List<LancamentoInversor> inversoresEntidade = inversoresRequest.stream()
                .map(i -> LancamentoInversor.builder()
                        .lancamento(lancamento)
                        .kw(i.getKw())
                        .quantidade(i.getQuantidade())
                        .build())
                .toList();
        lancamento.getInversores().addAll(inversoresEntidade);
    }

    private Double subtrairSeInformado(double enviado, Double devolvido) {
        return devolvido != null ? enviado - devolvido : null;
    }

    private Long calcularDiasParado(Lancamento lancamento) {
        if (!Boolean.FALSE.equals(lancamento.getRetornou()) || lancamento.getDataLancamento() == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(lancamento.getDataLancamento(), LocalDate.now());
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
                .ajudante(lancamento.getEquipe().getAjudante())
                .dataLancamento(lancamento.getDataLancamento())
                .cliente(lancamento.getCliente())
                .sistemas(lancamento.getSistemas())
                .observacoes(lancamento.getObservacoes())
                .criadoPor(lancamento.getCriadoPor())
                .retornou(lancamento.getRetornou())
                .tipoSistema(lancamento.getTipoSistema())
                .solo(lancamento.getSolo())
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
                .ajusteFinoVerm(lancamento.getAjusteFinoVerm())
                .ajusteFinoPreto(lancamento.getAjusteFinoPreto())
                .ajusteFinoHepr(lancamento.getAjusteFinoHepr())
                .localizacaoSobra(lancamento.getLocalizacaoSobra())
                .diasParado(calcularDiasParado(lancamento))
                .build();
    }
}
