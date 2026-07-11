package mg.vinaAkoho.vina_akoho.repository.matierespremieres;

import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Integer> {
    Optional<Fournisseur> findByNomIgnoreCase(String nom);
}
