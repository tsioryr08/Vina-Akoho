package mg.vinaAkoho.vina_akoho.repository.matierespremieres;

import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MatierePremiere;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatierePremiereRepository extends JpaRepository<MatierePremiere, Integer> {

    long countByCodeStartingWith(String prefixe);
}
