package mg.vinaAkoho.vina_akoho.repository.clients;

import java.util.Optional;
import mg.vinaAkoho.vina_akoho.entity.clients.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    Optional<Client> findByIdAndNumeroTelephoneAndActifTrue(Integer id, String numeroTelephone);

    Optional<Client> findByNumeroTelephoneAndActifTrue(String numeroTelephone);
}
