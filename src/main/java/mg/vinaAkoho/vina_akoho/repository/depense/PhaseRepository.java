package mg.vinaAkoho.vina_akoho.repository.depense;

import java.util.Optional;
import mg.vinaAkoho.vina_akoho.entity.depense.Phase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhaseRepository extends JpaRepository<Phase, Integer> {

    Optional<Phase> findByLibelle(String libelle);
}
