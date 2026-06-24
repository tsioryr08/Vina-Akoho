package mg.vinaAkoho.vina_akoho.repository.matierespremieres;

import java.util.Optional;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeMouvementRepository extends JpaRepository<TypeMouvement, Integer> {

    Optional<TypeMouvement> findByLibelle(String libelle);
}
