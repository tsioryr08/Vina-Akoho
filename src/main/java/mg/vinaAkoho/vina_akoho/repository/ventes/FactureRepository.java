package mg.vinaAkoho.vina_akoho.repository.ventes;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.ventes.Facture;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    Optional<Facture> findByVenteId(Long venteId);
}
