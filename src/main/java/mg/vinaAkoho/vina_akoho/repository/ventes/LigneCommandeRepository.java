package mg.vinaAkoho.vina_akoho.repository.ventes;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.ventes.LigneCommande;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
}
