package mg.vinaAkoho.vina_akoho.repository.depense;

import mg.vinaAkoho.vina_akoho.entity.depense.DepenseLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DepenseLotRepository extends JpaRepository<DepenseLot, Integer> {

    List<DepenseLot> findByDepenseId(Integer depenseId);

    @Query("SELECT SUM(dl.depense.montant) FROM DepenseLot dl WHERE dl.lotProduit.id = :lotProduitId")
    BigDecimal sumDepensesByLotProduitId(@Param("lotProduitId") Long lotProduitId);

    @Query("SELECT SUM(dl.depense.montant) FROM DepenseLot dl WHERE dl.lotProduit.produit.id = :produitId")
    BigDecimal sumDepensesByProduitId(@Param("produitId") Long produitId);
}
