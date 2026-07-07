package br.com.conectsol.backend.service;

import br.com.conectsol.backend.dto.EquipeDTO;
import br.com.conectsol.backend.dto.EquipeRequest;
import br.com.conectsol.backend.exception.RecursoNaoEncontradoException;
import br.com.conectsol.backend.model.Equipe;
import br.com.conectsol.backend.repository.EquipeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EquipeService {

    private final EquipeRepository equipeRepository;

    @Transactional(readOnly = true)
    public List<EquipeDTO> listar() {
        return equipeRepository.findAll().stream().map(this::paraDTO).toList();
    }

    @Transactional(readOnly = true)
    public EquipeDTO buscarPorId(Long id) {
        return paraDTO(buscarEntidade(id));
    }

    @Transactional
    public EquipeDTO criar(EquipeRequest request) {
        Equipe equipe = Equipe.builder()
                .montador(request.getMontador())
                .eletricista(request.getEletricista())
                .ativa(true)
                .build();
        return paraDTO(equipeRepository.save(equipe));
    }

    @Transactional
    public EquipeDTO atualizar(Long id, EquipeRequest request) {
        Equipe equipe = buscarEntidade(id);
        equipe.setMontador(request.getMontador());
        equipe.setEletricista(request.getEletricista());
        return paraDTO(equipeRepository.save(equipe));
    }

    @Transactional
    public void excluir(Long id) {
        Equipe equipe = buscarEntidade(id);
        equipe.setAtiva(false);
        equipeRepository.save(equipe);
    }

    Equipe buscarEntidade(Long id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipe nao encontrada"));
    }

    private EquipeDTO paraDTO(Equipe equipe) {
        return EquipeDTO.builder()
                .id(equipe.getId())
                .montador(equipe.getMontador())
                .eletricista(equipe.getEletricista())
                .ativa(equipe.getAtiva())
                .build();
    }
}
