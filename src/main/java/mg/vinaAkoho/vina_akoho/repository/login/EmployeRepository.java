package mg.vinaAkoho.vina_akoho.repository.login;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.login.Employe;

import java.util.Optional;

public interface EmployeRepository extends JpaRepository<Employe, Integer> {

    Optional<Employe> findByEmail(String email);
}
