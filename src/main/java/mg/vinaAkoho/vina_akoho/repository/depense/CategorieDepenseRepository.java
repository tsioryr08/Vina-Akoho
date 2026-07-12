package mg.vinaAkoho.vina_akoho.repository.depense;

import java.util.Optional;
import mg.vinaAkoho.vina_akoho.entity.depense.CategorieDepense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieDepenseRepository extends JpaRepository<CategorieDepense, Integer> {

    Optional<CategorieDepense> findByLibelle(String libelle);
}