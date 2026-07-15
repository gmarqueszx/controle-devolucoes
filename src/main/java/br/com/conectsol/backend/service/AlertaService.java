package br.com.conectsol.backend.service;

import br.com.conectsol.backend.dto.AlertaDTO;
import br.com.conectsol.backend.dto.AlertaRequest;
import br.com.conectsol.backend.dto.AlertaStatusRequest;
import br.com.conectsol.backend.dto.ConfirmarDesvioRequest;
import br.com.conectsol.backend.exception.RecursoNaoEncontradoException;
import br.com.conectsol.backend.model.Alerta;
import br.com.conectsol.backend.model.Equipe;
import br.com.conectsol.backend.model.Lancamento;
import br.com.conectsol.backend.model.NivelAlerta;
import br.com.conectsol.backend.repository.AlertaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final EquipeService equipeService;
    private final LancamentoService lancamentoService;
    private final AlertaNivelClassificador classificador;

    @Transactional(readOnly = true)
    public List<AlertaDTO> listar(LocalDate de, LocalDate ate, NivelAlerta nivel, Long equipeId) {
        List<Alerta> alertas;
        if (nivel != null && equipeId != null) {
            alertas = alertaRepository.findByDataAlertaBetweenAndNivelAndEquipeId(de, ate, nivel, equipeId);
        } else if (nivel != null) {
            alertas = alertaRepository.findByDataAlertaBetweenAndNivel(de, ate, nivel);
        } else if (equipeId != null) {
            alertas = alertaRepository.findByDataAlertaBetweenAndEquipeId(de, ate, equipeId);
        } else {
            alertas = alertaRepository.findByDataAlertaBetween(de, ate);
        }
        return alertas.stream().map(this::paraDTO).toList();
    }

    @Transactional
    public Optional<AlertaDTO> criar(AlertaRequest request) {
        NivelAlerta nivel = classificador.classificar(request.getStatusOriginal());
        if (nivel == null) {
            return Optional.empty();
        }

        Equipe equipe = equipeService.buscarEntidade(request.getEquipeId());
        Lancamento lancamento = request.getLancamentoId() != null
                ? lancamentoService.buscarEntidade(request.getLancamentoId())
                : null;

        Alerta alerta = Alerta.builder()
                .equipe(equipe)
                .lancamento(lancamento)
                .dataAlerta(request.getDataAlerta())
                .descricao(request.getDescricao())
                .statusOriginal(request.getStatusOriginal())
                .nivel(nivel)
                .build();

        return Optional.of(paraDTO(alertaRepository.save(alerta)));
    }

    @Transactional
    public AlertaDTO atualizarStatus(Long id, AlertaStatusRequest request) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta nao encontrado"));
        alerta.setStatus(request.getStatus());
        return paraDTO(alertaRepository.save(alerta));
    }

    @Transactional
    public AlertaDTO confirmarDesvio(Long id, ConfirmarDesvioRequest request) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta nao encontrado"));
        alerta.setNivel(NivelAlerta.ALTO);
        alerta.setConfirmadoDesvioEm(LocalDateTime.now());
        alerta.setConfirmadoDesvioPor(request.getConfirmadoPor());
        alerta.setJustificativaConfirmacao(request.getJustificativa());
        return paraDTO(alertaRepository.save(alerta));
    }

    private AlertaDTO paraDTO(Alerta alerta) {
        return AlertaDTO.builder()
                .id(alerta.getId())
                .equipeId(alerta.getEquipe().getId())
                .montador(alerta.getEquipe().getMontador())
                .eletricista(alerta.getEquipe().getEletricista())
                .cliente(alerta.getLancamento() != null ? alerta.getLancamento().getCliente() : null)
                .dataAlerta(alerta.getDataAlerta())
                .descricao(alerta.getDescricao())
                .nivel(alerta.getNivel())
                .status(alerta.getStatus())
                .statusOriginal(alerta.getStatusOriginal())
                .origem(alerta.getOrigem())
                .confirmadoDesvioEm(alerta.getConfirmadoDesvioEm())
                .confirmadoDesvioPor(alerta.getConfirmadoDesvioPor())
                .justificativaConfirmacao(alerta.getJustificativaConfirmacao())
                .build();
    }
}
