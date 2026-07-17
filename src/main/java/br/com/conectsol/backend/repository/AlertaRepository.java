package br.com.conectsol.backend.repository;

import br.com.conectsol.backend.model.Alerta;
import br.com.conectsol.backend.model.NivelAlerta;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // equipe/lancamento (to-one) são LAZY e sempre acessados na conversão para DTO; busca-los junto evita N+1.
    // NÃO incluir "equipe.membros" aqui: é uma coleção alcançada via many-to-one (alerta.equipe) compartilhada
    // por várias linhas de Alerta, e o Hibernate reacrescenta os mesmos membros a cada linha que compartilha a
    // equipe (duplicando a coleção). equipe.membros fica a cargo do @BatchSize em Equipe.membros, que resolve
    // via query em lote (WHERE equipe_id IN (...)) sem esse risco.
    @EntityGraph(attributePaths = {"equipe", "lancamento"})
    List<Alerta> findByDataAlertaBetween(LocalDate de, LocalDate ate);

    @EntityGraph(attributePaths = {"equipe", "lancamento"})
    List<Alerta> findByDataAlertaBetweenAndEquipeId(LocalDate de, LocalDate ate, Long equipeId);

    @EntityGraph(attributePaths = {"equipe", "lancamento"})
    List<Alerta> findByDataAlertaBetweenAndNivel(LocalDate de, LocalDate ate, NivelAlerta nivel);

    @EntityGraph(attributePaths = {"equipe", "lancamento"})
    List<Alerta> findByDataAlertaBetweenAndNivelAndEquipeId(
            LocalDate de, LocalDate ate, NivelAlerta nivel, Long equipeId);

    long countByDataAlerta(LocalDate dataAlerta);

    List<Alerta> findByLancamentoIdAndOrigem(Long lancamentoId, String origem);
}
