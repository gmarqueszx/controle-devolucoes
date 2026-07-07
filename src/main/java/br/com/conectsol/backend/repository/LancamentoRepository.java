package br.com.conectsol.backend.repository;

import br.com.conectsol.backend.model.Lancamento;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    List<Lancamento> findByDataLancamentoBetween(LocalDate de, LocalDate ate);

    List<Lancamento> findByDataLancamentoBetweenAndEquipeId(LocalDate de, LocalDate ate, Long equipeId);

    @Query("select coalesce(sum(l.sistemas), 0) from Lancamento l "
            + "where l.dataLancamento between :de and :ate")
    Integer somarSistemasNoPeriodo(@Param("de") LocalDate de, @Param("ate") LocalDate ate);
}
