package br.com.conectsol.backend.service;

import br.com.conectsol.backend.dto.EquipeDTO;
import br.com.conectsol.backend.dto.EquipeMembroDTO;
import br.com.conectsol.backend.dto.EquipeMembroRequest;
import br.com.conectsol.backend.dto.EquipeRequest;
import br.com.conectsol.backend.exception.RecursoNaoEncontradoException;
import br.com.conectsol.backend.model.Equipe;
import br.com.conectsol.backend.model.EquipeMembro;
import br.com.conectsol.backend.model.FuncaoEquipe;
import br.com.conectsol.backend.repository.EquipeRepository;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
        Equipe equipe = new Equipe();
        equipe.setAtiva(true);
        equipe.setMembros(new java.util.ArrayList<>());
        aplicarMembros(equipe, request);
        return paraDTO(equipeRepository.save(equipe));
    }

    @Transactional
    public EquipeDTO atualizar(Long id, EquipeRequest request) {
        Equipe equipe = buscarEntidade(id);
        aplicarMembros(equipe, request);
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

    /**
     * Usado no lançamento: cada obra informa montador/eletricista/ajudante diretamente. Reaproveita uma equipe
     * ativa existente com exatamente essa composição de pessoas, ou cria uma nova.
     */
    @Transactional
    Equipe buscarOuCriarPorMembros(String montador, String eletricista, String ajudante) {
        Set<String> chaveAlvo = montarChave(montador, eletricista, ajudante);

        for (Equipe equipe : equipeRepository.findByAtivaTrue()) {
            if (montarChave(equipe.getMembros()).equals(chaveAlvo)) {
                return equipe;
            }
        }

        Equipe equipe = new Equipe();
        equipe.setAtiva(true);
        equipe.setMembros(new ArrayList<>());
        adicionarMembro(equipe, montador, FuncaoEquipe.MONTADOR);
        adicionarMembro(equipe, eletricista, FuncaoEquipe.ELETRICISTA);
        adicionarMembro(equipe, ajudante, FuncaoEquipe.AJUDANTE);
        return equipeRepository.save(equipe);
    }

    private Set<String> montarChave(String montador, String eletricista, String ajudante) {
        Set<String> chave = new LinkedHashSet<>();
        adicionarNaChave(chave, montador, FuncaoEquipe.MONTADOR);
        adicionarNaChave(chave, eletricista, FuncaoEquipe.ELETRICISTA);
        adicionarNaChave(chave, ajudante, FuncaoEquipe.AJUDANTE);
        return chave;
    }

    private Set<String> montarChave(List<EquipeMembro> membros) {
        Set<String> chave = new LinkedHashSet<>();
        for (EquipeMembro membro : membros) {
            adicionarNaChave(chave, membro.getNome(), membro.getFuncao());
        }
        return chave;
    }

    private void adicionarNaChave(Set<String> chave, String nome, FuncaoEquipe funcao) {
        if (nome != null && !nome.isBlank()) {
            chave.add(funcao + ":" + nome.trim().toUpperCase());
        }
    }

    private void adicionarMembro(Equipe equipe, String nome, FuncaoEquipe funcao) {
        if (nome != null && !nome.isBlank()) {
            equipe.getMembros()
                    .add(EquipeMembro.builder()
                            .equipe(equipe)
                            .nome(nome.trim().toUpperCase())
                            .funcao(funcao)
                            .build());
        }
    }

    private void aplicarMembros(Equipe equipe, EquipeRequest request) {
        List<EquipeMembroRequest> membrosRequest = request.getMembros() != null ? request.getMembros() : List.of();
        boolean temMontador = membrosRequest.stream().anyMatch(m -> m.getFuncao() == FuncaoEquipe.MONTADOR);
        if (!temMontador) {
            throw new IllegalArgumentException("Equipe precisa de ao menos um montador");
        }

        equipe.getMembros().clear();
        List<EquipeMembro> membros = membrosRequest.stream()
                .map(m -> EquipeMembro.builder()
                        .equipe(equipe)
                        .nome(m.getNome())
                        .funcao(m.getFuncao())
                        .build())
                .toList();
        equipe.getMembros().addAll(membros);
    }

    private EquipeDTO paraDTO(Equipe equipe) {
        List<EquipeMembroDTO> membros = equipe.getMembros().stream()
                .map(m -> EquipeMembroDTO.builder().nome(m.getNome()).funcao(m.getFuncao()).build())
                .toList();

        return EquipeDTO.builder()
                .id(equipe.getId())
                .montador(equipe.getMontador())
                .eletricista(equipe.getEletricista())
                .ajudante(equipe.getAjudante())
                .membros(membros)
                .ativa(equipe.getAtiva())
                .build();
    }
}
