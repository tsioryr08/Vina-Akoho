package mg.vinaAkoho.vina_akoho.repository.entreeproduit;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mg.vinaAkoho.vina_akoho.entity.entreeproduit.LotProduit;

public interface LotProduitRepository extends JpaRepository<LotProduit, Integer> {

    List<LotProduit> findByProduitIdOrderByDateFabricationAscIdAsc(Long idProduit);

    @Query("SELECT COALESCE(SUM(l.quantiteRestante), 0) FROM LotProduit l WHERE l.produit.id = :idProduit")
    BigDecimal sommeQuantiteRestante(Long idProduit);
}