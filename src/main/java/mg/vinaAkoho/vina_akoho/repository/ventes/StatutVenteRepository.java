package mg.vinaAkoho.vina_akoho.repository.ventes;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.ventes.StatutVente;

public interface StatutVenteRepository extends JpaRepository<StatutVente, Long> {

    Optional<StatutVente> findByLibelleIgnoreCase(String libelle);
}
