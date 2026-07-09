package mg.vinaAkoho.vina_akoho.repository.login;

import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeRepository extends JpaRepository<Employe, Integer> {

    Optional<Employe> findByEmail(String email);

    // Liste des emp actifs/non actifs 
    List<Employe> findByActif(Boolean actif);

    // Filtre par role
    List<Employe> findByRoleId(Integer idRole);

    // Filtre par role+statut
    List<Employe> findByActifAndRoleId(Boolean actif, Integer idRole);

    //recherche par nom, prenom ou mail (insensible a la casse)
    @Query("SELECT e FROM Employe e WHERE " +
           "LOWER(e.nom) LIKE LOWER(CONCAT('%', :terme, '%')) OR " +
           "LOWER(e.prenom) LIKE LOWER(CONCAT('%', :terme, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :terme, '%'))")
    List<Employe> rechercher(@Param("terme") String terme);

    // Recherche + filtre statut
    @Query("SELECT e FROM Employe e WHERE e.actif = :actif AND (" +
           "LOWER(e.nom) LIKE LOWER(CONCAT('%', :terme, '%')) OR " +
           "LOWER(e.prenom) LIKE LOWER(CONCAT('%', :terme, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :terme, '%')))")
    List<Employe> rechercherParStatut(@Param("terme") String terme, @Param("actif") Boolean actif);

    boolean existsByEmail(String email);
}