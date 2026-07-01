package mg.vinaAkoho.vina_akoho.repository.matierespremieres;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.vinaAkoho.vina_akoho.entity.matierespremieres.TypeMouvement;

public interface TypeMouvementRepository extends JpaRepository<TypeMouvement, Integer> {

    Optional<TypeMouvement> findByLibelle(String libelle);
}
