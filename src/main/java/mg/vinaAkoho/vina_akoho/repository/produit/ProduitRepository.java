package mg.vinaAkoho.vina_akoho.repository.produit;

import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long>, JpaSpecificationExecutor<Produit> {

    // Ne récupérer que les produits actifs
    @Query("SELECT p FROM Produit p WHERE p.actif = true")
    List<Produit> findAllActifs();

    @Query("SELECT p FROM Produit p WHERE p.actif = true")
    Page<Produit> findAllActifs(Pageable pageable);

    @Query("SELECT p FROM Produit p WHERE p.actif = true AND p.id = :id")
    Optional<Produit> findByIdAndActifTrue(@Param("id") Long id);

    // Vérifications
    boolean existsByRefIgnoreCaseAndActifTrue(String ref);
    boolean existsByNomIgnoreCaseAndActifTrue(String nom);
    boolean existsByRefIgnoreCaseAndIdNotAndActifTrue(String ref, Long id);
    boolean existsByNomIgnoreCaseAndIdNotAndActifTrue(String nom, Long id);

    @Query("SELECT COUNT(p) FROM Produit p WHERE p.categorie.id = :categorieId AND p.actif = true")
    long countByCategorieIdAndActifTrue(@Param("categorieId") Long categorieId);  // ← AJOUTER CETTE MÉTHODE

    @Query("SELECT COUNT(p) > 0 FROM Produit p WHERE p.categorie.id = :categorieId AND p.actif = true")
    boolean existsByCategorieIdAndActifTrue(@Param("categorieId") Long categorieId);
}