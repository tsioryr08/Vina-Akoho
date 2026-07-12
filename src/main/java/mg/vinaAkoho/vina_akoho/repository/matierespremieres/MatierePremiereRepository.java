package mg.vinaAkoho.vina_akoho.repository.matierespremieres;

import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MatierePremiere;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface MatierePremiereRepository extends JpaRepository<MatierePremiere, Integer> {

    long countByCodeStartingWith(String prefixe);
    Optional<MatierePremiere> findByNomIgnoreCase(String nom);
}
