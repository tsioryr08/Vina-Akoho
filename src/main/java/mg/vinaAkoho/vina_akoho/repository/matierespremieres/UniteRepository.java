package mg.vinaAkoho.vina_akoho.repository.matierespremieres;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Unite;

public interface UniteRepository extends JpaRepository<Unite, Integer> {
    Optional<Unite> findByLibelle(String libelle);
}
