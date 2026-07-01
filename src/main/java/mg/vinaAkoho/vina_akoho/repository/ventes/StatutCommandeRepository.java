package mg.vinaAkoho.vina_akoho.repository.ventes;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.ventes.StatutCommande;

public interface StatutCommandeRepository extends JpaRepository<StatutCommande, Long> {

    Optional<StatutCommande> findByLibelleIgnoreCase(String libelle);
}
