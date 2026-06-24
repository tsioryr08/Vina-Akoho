package mg.vinaAkoho.vina_akoho.repository.clients;

import java.util.Optional;
import mg.vinaAkoho.vina_akoho.entity.clients.TypeClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeClientRepository extends JpaRepository<TypeClient, Integer> {

    Optional<TypeClient> findByLibelle(String libelle);
}
