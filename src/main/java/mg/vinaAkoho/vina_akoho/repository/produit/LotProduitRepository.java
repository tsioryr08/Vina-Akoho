package mg.vinaAkoho.vina_akoho.repository.produit;

import mg.vinaAkoho.vina_akoho.entity.produit.LotProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface LotProduitRepository extends JpaRepository<LotProduit, Long> {

    @Query("SELECT COALESCE(SUM(l.quantiteRestante), 0) FROM LotProduit l WHERE l.produit.id = :produitId")
    BigDecimal sommeQuantiteRestante(Long produitId);

    List<LotProduit> findByProduitIdAndQuantiteRestanteGreaterThanOrderByDateFabricationAsc(
            Long produitId,
            BigDecimal seuil
    );
}
