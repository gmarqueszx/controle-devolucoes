package br.com.conectsol.backend.repository;

import br.com.conectsol.backend.model.Lancamento;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    // equipe/inversores são LAZY e sempre acessados na conversão para DTO; busca-los junto evita N+1 nas
    // listagens. equipe.membros fica de fora (não dá pra fazer JOIN FETCH de duas coleções List na mesma query -
    // MultipleBagFetchException) e é resolvido via @BatchSize em Equipe.membros.
    @EntityGraph(attributePaths = {"equipe", "inversores"})
    List<Lancamento> findByDataLancamentoBetween(LocalDate de, LocalDate ate);

    @EntityGraph(attributePaths = {"equipe", "inversores"})
    List<Lancamento> findByDataLancamentoBetweenAndEquipeId(LocalDate de, LocalDate ate, Long equipeId);

    @Query("select coalesce(sum(l.sistemas), 0) from Lancamento l "
            + "where l.dataLancamento between :de and :ate")
    Integer somarSistemasNoPeriodo(@Param("de") LocalDate de, @Param("ate") LocalDate ate);

    /**
     * Médias históricas (só de lançamentos já retornados). Vermelho e preto são cabo solar, calculados
     * por string (cada string consome uma metragem parecida, independente do tamanho da instalação); HEPR é o
     * total do lançamento, já que não varia por string. Solo usa base de cálculo diferente do telhado comum
     * (60m/string vs 30m/string), então são comparados separadamente — misturar os dois distorce a média.
     */
    @Query("select avg(l.caboSolarVermUsado / l.strings), avg(l.caboSolarPretoUsado / l.strings), avg(l.caboHeprUsado) "
            + "from Lancamento l where l.retornou = true and l.tipoSistema = :tipoSistema and l.id <> :excluirId "
            + "and l.strings is not null and l.strings > 0 and l.solo = :solo")
    List<Object[]> calcularMediasUso(
            @Param("tipoSistema") String tipoSistema, @Param("excluirId") Long excluirId, @Param("solo") boolean solo);

    /** Mesma média usada nos alertas automáticos, mas com a contagem de amostras, para exibir na tela. */
    @Query("select count(l), avg(l.caboSolarVermUsado / l.strings), avg(l.caboSolarPretoUsado / l.strings), "
            + "avg(l.caboHeprUsado) from Lancamento l where l.retornou = true and l.tipoSistema = :tipoSistema "
            + "and l.strings is not null and l.strings > 0 and l.solo = :solo")
    List<Object[]> consultarMediasUso(@Param("tipoSistema") String tipoSistema, @Param("solo") boolean solo);

    @EntityGraph(attributePaths = {"equipe", "inversores"})
    List<Lancamento> findByRetornouFalseOrderByDataLancamentoAsc();

    @EntityGraph(attributePaths = {"equipe", "inversores"})
    List<Lancamento> findByRetornouTrue();
}
