package mg.vinaAkoho.vina_akoho.repository.livraison;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.livraison.livraison;
import org.springframework.data.jpa.repository.Query;

public interface LivraisonRepository extends JpaRepository<livraison, Long> {

    Optional<livraison> findByVenteId(Long venteId);

    @Query("SELECT z.libelle, COUNT(l) FROM livraison l JOIN l.zoneLivraison z GROUP BY z.libelle")
    List<Object[]> countLivraisonsByZone();
}