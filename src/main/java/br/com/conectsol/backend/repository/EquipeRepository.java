package br.com.conectsol.backend.repository;

import br.com.conectsol.backend.model.Equipe;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface
EquipeRepository extends JpaRepository<Equipe, Long> {

    // membros é LAZY e sempre acessado na conversão para DTO (e na comparação de composição de equipe); busca-lo
    // junto evita N+1 nas listagens.
    @Override
    @EntityGraph(attributePaths = "membros")
    List<Equipe> findAll();

    @EntityGraph(attributePaths = "membros")
    List<Equipe> findByAtivaTrue();

    long countByAtivaTrue();
}
