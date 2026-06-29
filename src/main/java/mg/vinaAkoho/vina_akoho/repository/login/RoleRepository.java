package mg.vinaAkoho.vina_akoho.repository.login;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.login.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByPoste(String poste);
}
