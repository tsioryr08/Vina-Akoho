package mg.vinaAkoho.vina_akoho.repository.entreeproduit;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.entreeproduit.MouvementStockProduit;

public interface MouvementStockProduitRepository extends JpaRepository<MouvementStockProduit, Integer> {
}