package mg.vinaAkoho.vina_akoho.repository.livraison;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.livraison.livraison;

public interface LivraisonRepository extends JpaRepository<livraison, Long> {

    Optional<livraison> findByVenteId(Long venteId);
}