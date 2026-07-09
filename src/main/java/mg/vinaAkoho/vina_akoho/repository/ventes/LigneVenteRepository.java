package mg.vinaAkoho.vina_akoho.repository.ventes;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.vinaAkoho.vina_akoho.dto.ventes.RecetteVenteProjection;
import mg.vinaAkoho.vina_akoho.dto.ventes.TopCategorieStatDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.TopProduitStatDTO;
import mg.vinaAkoho.vina_akoho.dto.prevision.VenteProduitProjection;
import mg.vinaAkoho.vina_akoho.entity.ventes.LigneVente;

public interface LigneVenteRepository extends JpaRepository<LigneVente, Long> {

    List<LigneVente> findByVenteId(Long venteId);

    @Query(value = """
            SELECT p.id AS produitId, COALESCE(SUM(lv.quantite), 0) AS quantiteVendue
            FROM ligne_vente lv
            JOIN vente v ON v.id = lv.id_vente
            JOIN produit p ON p.id = lv.id_produit
            JOIN statut_vente sv ON sv.id = v.id_statut_vente
            WHERE v.date_vente >= :debut
              AND v.date_vente < :fin
              AND LOWER(sv.libelle) NOT IN ('annulée', 'annulee', 'en attente', 'en attente de paiement')
            GROUP BY p.id
            """, nativeQuery = true)
    List<VenteProduitProjection> sommerQuantitesVenduesParProduit(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query(value = """
            SELECT
                CAST(v.date_vente AS date) AS dateVente,
                p.id AS produitId,
                p.nom AS produitNom,
                p.prix_vente AS prixVente,
                COALESCE(SUM(lv.quantite), 0) AS quantiteVendue,
                COALESCE(SUM(lv.quantite * p.prix_vente), 0) AS montantRecette
            FROM ligne_vente lv
            JOIN vente v ON v.id = lv.id_vente
            JOIN produit p ON p.id = lv.id_produit
            JOIN statut_vente sv ON sv.id = v.id_statut_vente
            WHERE v.date_vente >= :debut
              AND v.date_vente < :fin
              AND LOWER(sv.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
            GROUP BY CAST(v.date_vente AS date), p.id, p.nom, p.prix_vente
            ORDER BY CAST(v.date_vente AS date) DESC, p.nom ASC
            """, nativeQuery = true)
    List<RecetteVenteProjection> calculerRecettesParPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("""
            SELECT new mg.vinaAkoho.vina_akoho.dto.ventes.TopProduitStatDTO(
                p.id, p.nom, c.libelle,
                SUM(lv.quantite), SUM(lv.montant), COUNT(DISTINCT v.id))
            FROM LigneVente lv
            JOIN lv.produit p
            JOIN p.categorie c
            JOIN lv.vente v
            WHERE v.dateVente >= :debut AND v.dateVente <= :fin
              AND LOWER(v.statutVente.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
              AND (:idCategorie IS NULL OR c.id = :idCategorie)
            GROUP BY p.id, p.nom, c.libelle
            ORDER BY SUM(lv.quantite) DESC
            """)
    List<TopProduitStatDTO> topProduitsParQuantite(@Param("debut") LocalDateTime debut,
                                                    @Param("fin") LocalDateTime fin,
                                                    @Param("idCategorie") Long idCategorie,
                                                    Pageable pageable);

    @Query("""
            SELECT new mg.vinaAkoho.vina_akoho.dto.ventes.TopProduitStatDTO(
                p.id, p.nom, c.libelle,
                SUM(lv.quantite), SUM(lv.montant), COUNT(DISTINCT v.id))
            FROM LigneVente lv
            JOIN lv.produit p
            JOIN p.categorie c
            JOIN lv.vente v
            WHERE v.dateVente >= :debut AND v.dateVente <= :fin
              AND LOWER(v.statutVente.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
              AND (:idCategorie IS NULL OR c.id = :idCategorie)
            GROUP BY p.id, p.nom, c.libelle
            ORDER BY SUM(lv.montant) DESC
            """)
    List<TopProduitStatDTO> topProduitsParMontant(@Param("debut") LocalDateTime debut,
                                                  @Param("fin") LocalDateTime fin,
                                                  @Param("idCategorie") Long idCategorie,
                                                  Pageable pageable);

    @Query("""
            SELECT new mg.vinaAkoho.vina_akoho.dto.ventes.TopCategorieStatDTO(
                c.id, c.libelle,
                SUM(lv.quantite), SUM(lv.montant), COUNT(DISTINCT v.id))
            FROM LigneVente lv
            JOIN lv.produit p
            JOIN p.categorie c
            JOIN lv.vente v
            WHERE v.dateVente >= :debut AND v.dateVente <= :fin
              AND LOWER(v.statutVente.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
              AND (:idCategorie IS NULL OR c.id = :idCategorie)
            GROUP BY c.id, c.libelle
            ORDER BY SUM(lv.quantite) DESC
            """)
    List<TopCategorieStatDTO> topCategoriesParQuantite(@Param("debut") LocalDateTime debut,
                                                        @Param("fin") LocalDateTime fin,
                                                        @Param("idCategorie") Long idCategorie,
                                                        Pageable pageable);

    @Query("""
            SELECT new mg.vinaAkoho.vina_akoho.dto.ventes.TopCategorieStatDTO(
                c.id, c.libelle,
                SUM(lv.quantite), SUM(lv.montant), COUNT(DISTINCT v.id))
            FROM LigneVente lv
            JOIN lv.produit p
            JOIN p.categorie c
            JOIN lv.vente v
            WHERE v.dateVente >= :debut AND v.dateVente <= :fin
              AND LOWER(v.statutVente.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
              AND (:idCategorie IS NULL OR c.id = :idCategorie)
            GROUP BY c.id, c.libelle
            ORDER BY SUM(lv.montant) DESC
            """)
    List<TopCategorieStatDTO> topCategoriesParMontant(@Param("debut") LocalDateTime debut,
                                                      @Param("fin") LocalDateTime fin,
                                                      @Param("idCategorie") Long idCategorie,
                                                      Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(lv.quantite), 0), COALESCE(SUM(lv.montant), 0)
            FROM LigneVente lv
            JOIN lv.produit p
            JOIN lv.vente v
            WHERE v.dateVente >= :debut AND v.dateVente <= :fin
              AND LOWER(v.statutVente.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
              AND (:idCategorie IS NULL OR p.categorie.id = :idCategorie)
            """)
    List<Object[]> totauxPeriode(@Param("debut") LocalDateTime debut,
                                  @Param("fin") LocalDateTime fin,
                                  @Param("idCategorie") Long idCategorie);
}
