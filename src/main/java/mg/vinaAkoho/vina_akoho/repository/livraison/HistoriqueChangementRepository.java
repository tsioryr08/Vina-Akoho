package mg.vinaAkoho.vina_akoho.repository.livraison;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.livraison.historique_statut_livraison;

public interface HistoriqueChangementRepository extends JpaRepository<historique_statut_livraison, Integer> {
}
