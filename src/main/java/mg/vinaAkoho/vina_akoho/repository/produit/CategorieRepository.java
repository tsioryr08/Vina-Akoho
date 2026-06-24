package mg.vinaAkoho.vina_akoho.repository.produit;

import mg.vinaAkoho.vina_akoho.entity.produit.Categorie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    // Récupérer toutes les catégories actives
    @Query("SELECT c FROM Categorie c WHERE c.actif = true")
    List<Categorie> findAllByActifTrue();

    @Query("SELECT c FROM Categorie c WHERE c.actif = true")
    Page<Categorie> findAllByActifTrue(Pageable pageable);

    @Query("SELECT c FROM Categorie c WHERE c.actif = true AND c.id = :id")
    Optional<Categorie> findByIdAndActifTrue(@Param("id") Long id);

    boolean existsByLibelleIgnoreCaseAndActifTrue(String libelle);
    boolean existsByLibelleIgnoreCaseAndIdNotAndActifTrue(String libelle, Long id);

    long countByActifTrue();
    Optional<Categorie> findById(Long id);

    @Query("SELECT c FROM Categorie c WHERE c.actif = true AND LOWER(c.libelle) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Categorie> findByLibelleContainingIgnoreCaseAndActifTrue(@Param("search") String search, Pageable pageable);
}