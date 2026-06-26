package mg.vinaAkoho.vina_akoho.repository.clients;

import java.util.List;
import java.util.Optional;
import mg.vinaAkoho.vina_akoho.entity.clients.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    List<Client> findByEstSupprimerFalse();

    Optional<Client> findByIdAndEstSupprimerFalse(Integer id);

    Optional<Client> findByIdAndNumeroTelephoneAndActifTrueAndEstSupprimerFalse(Integer id, String numeroTelephone);

    Optional<Client> findByNumeroTelephoneAndActifTrueAndEstSupprimerFalse(String numeroTelephone);
}
