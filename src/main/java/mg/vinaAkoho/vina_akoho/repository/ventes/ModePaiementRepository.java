package mg.vinaAkoho.vina_akoho.repository.ventes;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.ventes.ModePaiement;

public interface ModePaiementRepository extends JpaRepository<ModePaiement, Long> {

    Optional<ModePaiement> findByLibelleIgnoreCase(String libelle);
}
