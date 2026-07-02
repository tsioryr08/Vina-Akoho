package mg.vinaAkoho.vina_akoho.repository.produit;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.produit.MouvementStockProduit;

public interface MouvementStockProduitRepository extends JpaRepository<MouvementStockProduit, Integer> {
}