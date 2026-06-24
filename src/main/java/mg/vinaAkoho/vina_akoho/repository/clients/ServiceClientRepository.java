package mg.vinaAkoho.vina_akoho.repository.clients;

import java.util.Optional;
import mg.vinaAkoho.vina_akoho.entity.clients.ServiceClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceClientRepository extends JpaRepository<ServiceClient, Integer> {

    Optional<ServiceClient> findByLibelle(String libelle);
}
