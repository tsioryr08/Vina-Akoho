package mg.vinaAkoho.vina_akoho.repository.livraison;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.livraison.ZoneLivraison;

public interface ZoneLivraisonRepository extends JpaRepository<ZoneLivraison, String> {

    List<ZoneLivraison> findAllByOrderByLibelleAsc();
}
