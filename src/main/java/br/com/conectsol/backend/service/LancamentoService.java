package br.com.conectsol.backend.service;

import br.com.conectsol.backend.dto.LancamentoDTO;
import br.com.conectsol.backend.dto.LancamentoRequest;
import br.com.conectsol.backend.exception.RecursoNaoEncontradoException;
import br.com.conectsol.backend.model.Equipe;
import br.com.conectsol.backend.model.Lancamento;
import br.com.conectsol.backend.repository.LancamentoRepository;
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
        Lancamento lancamento = Lancamento.builder()
                .equipe(equipe)
                .dataLancamento(request.getDataLancamento())
                .cliente(request.getCliente())
                .sistemas(request.getSistemas())
                .observacoes(request.getObservacoes())
                .criadoPor(request.getCriadoPor())
                .build();
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

    private LancamentoDTO paraDTO(Lancamento lancamento) {
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
                .build();
    }
}
