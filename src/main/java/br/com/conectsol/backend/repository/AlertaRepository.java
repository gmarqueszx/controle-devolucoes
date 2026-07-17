package br.com.conectsol.backend.repository;

import br.com.conectsol.backend.model.Alerta;
import br.com.conectsol.backend.model.NivelAlerta;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // equipe/equipe.membros/lancamento são LAZY e sempre acessados na conversão para DTO; busca-los junto evita
    // N+1 nas listagens.
    @EntityGraph(attributePaths = {"equipe", "equipe.membros", "lancamento"})
    List<Alerta> findByDataAlertaBetween(LocalDate de, LocalDate ate);

    @EntityGraph(attributePaths = {"equipe", "equipe.membros", "lancamento"})
    List<Alerta> findByDataAlertaBetweenAndEquipeId(LocalDate de, LocalDate ate, Long equipeId);

    @EntityGraph(attributePaths = {"equipe", "equipe.membros", "lancamento"})
    List<Alerta> findByDataAlertaBetweenAndNivel(LocalDate de, LocalDate ate, NivelAlerta nivel);

    @EntityGraph(attributePaths = {"equipe", "equipe.membros", "lancamento"})
    List<Alerta> findByDataAlertaBetweenAndNivelAndEquipeId(
            LocalDate de, LocalDate ate, NivelAlerta nivel, Long equipeId);

    long countByDataAlerta(LocalDate dataAlerta);

    List<Alerta> findByLancamentoIdAndOrigem(Long lancamentoId, String origem);
}
