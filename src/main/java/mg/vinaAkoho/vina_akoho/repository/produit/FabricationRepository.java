package mg.vinaAkoho.vina_akoho.repository.produit;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.produit.Fabrication;

import java.util.List;

public interface FabricationRepository extends JpaRepository<Fabrication, Integer> {
    List<Fabrication> findAllByOrderByDateFabricationDescIdDesc();
}
