package mg.vinaAkoho.vina_akoho.repository.livraison;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.livraison.historique_statut_livraison;

public interface HistoriqueChangementRepository extends JpaRepository<historique_statut_livraison, Integer> {

	List<historique_statut_livraison> findByIdLivraisonOrderByDateChangementDesc(Integer idLivraison);

	List<historique_statut_livraison> findAllByOrderByDateChangementDesc();
}
