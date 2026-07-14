package mg.vinaAkoho.vina_akoho.repository.depense;

import java.util.Optional;
import mg.vinaAkoho.vina_akoho.entity.depense.StatutDepense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatutDepenseRepository extends JpaRepository<StatutDepense, Integer> {

    Optional<StatutDepense> findByLibelle(String libelle);
}
