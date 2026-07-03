package mg.vinaAkoho.vina_akoho.repository.produit;

import mg.vinaAkoho.vina_akoho.entity.produit.HistoriquePrixProduit;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriquePrixProduitRepository extends JpaRepository<HistoriquePrixProduit, Long> {
    
    List<HistoriquePrixProduit> findByProduitIdOrderByDateModificationDesc(Long produitId);
    
    List<HistoriquePrixProduit> findByProduitOrderByDateModificationDesc(Produit produit);
}
