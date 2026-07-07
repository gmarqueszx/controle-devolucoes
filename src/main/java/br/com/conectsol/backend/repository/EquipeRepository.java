package br.com.conectsol.backend.repository;

import br.com.conectsol.backend.model.Equipe;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {

    List<Equipe> findByAtivaTrue();

    long countByAtivaTrue();
}
