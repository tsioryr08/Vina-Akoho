package mg.vinaAkoho.vina_akoho.repository.livraison;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.livraison.statutLivraison;

public interface StatutLivraisonRepository extends JpaRepository<statutLivraison, Integer> {

    Optional<statutLivraison> findByLibelleIgnoreCase(String libelle);
}
