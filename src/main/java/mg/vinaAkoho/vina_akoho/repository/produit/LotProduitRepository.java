package mg.vinaAkoho.vina_akoho.repository.produit;

import mg.vinaAkoho.vina_akoho.entity.produit.LotProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface LotProduitRepository extends JpaRepository<LotProduit, Long> {

    List<LotProduit> findByProduitIdOrderByDateFabricationAscIdAsc(Long idProduit);

    List<LotProduit> findByProduitIdAndQuantiteRestanteGreaterThanOrderByDateFabricationAsc(
            Long produitId,
            BigDecimal seuil
    );

    @Query("SELECT COALESCE(SUM(l.quantiteRestante), 0) FROM LotProduit l WHERE l.produit.id = :produitId")
    BigDecimal sommeQuantiteRestante(Long produitId);

    // Nombre de lots produits actifs (quantité restante > 0)
    @Query("SELECT COUNT(l) FROM LotProduit l WHERE l.quantiteRestante > 0")
    long compterLotsProduitsActifs();
}

