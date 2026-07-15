package br.com.conectsol.backend.repository;

import br.com.conectsol.backend.model.Alerta;
import br.com.conectsol.backend.model.NivelAlerta;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByDataAlertaBetween(LocalDate de, LocalDate ate);

    List<Alerta> findByDataAlertaBetweenAndEquipeId(LocalDate de, LocalDate ate, Long equipeId);

    List<Alerta> findByDataAlertaBetweenAndNivel(LocalDate de, LocalDate ate, NivelAlerta nivel);

    List<Alerta> findByDataAlertaBetweenAndNivelAndEquipeId(
            LocalDate de, LocalDate ate, NivelAlerta nivel, Long equipeId);

    long countByDataAlerta(LocalDate dataAlerta);

    List<Alerta> findByLancamentoIdAndOrigem(Long lancamentoId, String origem);
}
