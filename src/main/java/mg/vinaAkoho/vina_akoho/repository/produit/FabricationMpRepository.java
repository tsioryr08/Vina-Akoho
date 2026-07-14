package mg.vinaAkoho.vina_akoho.repository.produit;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.produit.FabricationMp;

import java.util.List;

public interface FabricationMpRepository extends JpaRepository<FabricationMp, Integer> {
    List<FabricationMp> findByFabricationIdOrderByIdAsc(Integer fabricationId);
}
