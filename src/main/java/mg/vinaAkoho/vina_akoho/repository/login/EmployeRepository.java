package mg.vinaAkoho.vina_akoho.repository.login;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.login.Employe;


public interface EmployeRepository extends JpaRepository<Employe, Integer> {

    Optional<Employe> findByEmail(String email);

    boolean existsByEmail(String email);
}
