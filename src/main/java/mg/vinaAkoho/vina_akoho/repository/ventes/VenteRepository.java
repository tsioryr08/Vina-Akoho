package mg.vinaAkoho.vina_akoho.repository.ventes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.vinaAkoho.vina_akoho.entity.ventes.Vente;

public interface VenteRepository extends JpaRepository<Vente, Long> {

    @Query("SELECT SUM(v.montantTotal) FROM Vente v " +
       "WHERE v.dateVente BETWEEN :startDate AND :endDate "
       )
    BigDecimal sumRecettesEntreDeuxDates(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);


    List<Vente> findAllByOrderByDateVenteDesc();

    List<Vente> findByClientIdOrderByDateVenteDesc(Integer clientId);

    @Query("""
            SELECT COALESCE(SUM(v.montantTotal), 0)
            FROM Vente v
            WHERE v.client.id = :clientId
              AND LOWER(v.statutVente.libelle) NOT IN ('annulée', 'annulee')
            """)
    BigDecimal sommeAchatsClient(@Param("clientId") Integer clientId);

    @Query("""
            SELECT COALESCE(SUM(v.montantTotal), 0)
            FROM Vente v
            WHERE v.client.id = :clientId
              AND LOWER(v.statutVente.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
            """)
    BigDecimal sommeReglementsClient(@Param("clientId") Integer clientId);

    /**
     * IMPORTANT (point 4 du markdown) : une vente ne doit être comptabilisée
     * dans le chiffre d'affaires / les statistiques que si elle est
     * effectivement réalisée. On exclut donc explicitement les statuts
     * "En attente de paiement" ET "Annulée" (pas seulement "Annulée" comme
     * avant) : tout le reste du cycle (Validée, En préparation, En livraison,
     * Livrée) compte comme une vente réalisée.
     */
    @Query("""
            SELECT COUNT(v)
            FROM Vente v
            WHERE v.dateVente >= :debut
              AND v.dateVente < :fin
              AND LOWER(v.statutVente.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
            """)
    long compterVentesValideesEntre(@Param("debut") LocalDateTime debut,
                                     @Param("fin") LocalDateTime fin);

    @Query("""
            SELECT COALESCE(SUM(v.montantTotal), 0)
            FROM Vente v
            WHERE v.dateVente >= :debut
              AND v.dateVente < :fin
              AND LOWER(v.statutVente.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
            """)
    BigDecimal sommeVentesValideesEntre(@Param("debut") LocalDateTime debut,
                                         @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(v) FROM Vente v")
    long compterToutesLesVentes();

    @Query("""
            SELECT COUNT(v)
            FROM Vente v
            WHERE LOWER(v.statutVente.libelle) IN ('en attente', 'en attente de paiement')
            """)
    long compterVentesEnAttente();

    /**
     * IMPORTANT : requête en JPQL (et non native) afin que Spring Data puisse
     * correctement fusionner le tri (Sort) transmis via le Pageable, en utilisant
     * les noms de PROPRIÉTÉS JAVA (dateVente, montantTotal, client.nom) et non les
     * noms de colonnes SQL. Une requête native + un Pageable avec Sort provoque un
     * double ORDER BY (celui écrit en dur + celui ajouté automatiquement par
     * Spring) et/ou une erreur "colonne inconnue" -> Erreur 500.
     */
    @Query(value = """
            SELECT v FROM Vente v
            LEFT JOIN v.client c
            LEFT JOIN v.modePaiement mp
            LEFT JOIN v.statutVente sv
            WHERE (:client IS NULL OR LOWER(c.nom) LIKE LOWER(CONCAT('%', :client, '%'))
                   OR LOWER(c.prenom) LIKE LOWER(CONCAT('%', :client, '%')))
              AND (:produit IS NULL OR EXISTS (
                    SELECT lv FROM LigneVente lv
                    WHERE lv.vente = v
                      AND LOWER(lv.produit.nom) LIKE LOWER(CONCAT('%', :produit, '%'))
              ))
              AND (:numeroFacture IS NULL OR EXISTS (
                    SELECT f FROM Facture f
                    WHERE f.vente = v
                      AND LOWER(f.numero) LIKE LOWER(CONCAT('%', :numeroFacture, '%'))
              ))
              AND (:dateDebut IS NULL OR v.dateVente >= :dateDebut)
              AND (:dateFin IS NULL OR v.dateVente <= :dateFin)
              AND (:modePaiement IS NULL OR LOWER(mp.libelle) = LOWER(:modePaiement))
              AND (:statut IS NULL OR LOWER(sv.libelle) = LOWER(:statut))
              AND (:montantMin IS NULL OR v.montantTotal >= :montantMin)
              AND (:montantMax IS NULL OR v.montantTotal <= :montantMax)
            """,
            countQuery = """
            SELECT COUNT(v) FROM Vente v
            LEFT JOIN v.client c
            LEFT JOIN v.modePaiement mp
            LEFT JOIN v.statutVente sv
            WHERE (:client IS NULL OR LOWER(c.nom) LIKE LOWER(CONCAT('%', :client, '%'))
                   OR LOWER(c.prenom) LIKE LOWER(CONCAT('%', :client, '%')))
              AND (:produit IS NULL OR EXISTS (
                    SELECT lv FROM LigneVente lv
                    WHERE lv.vente = v
                      AND LOWER(lv.produit.nom) LIKE LOWER(CONCAT('%', :produit, '%'))
              ))
              AND (:numeroFacture IS NULL OR EXISTS (
                    SELECT f FROM Facture f
                    WHERE f.vente = v
                      AND LOWER(f.numero) LIKE LOWER(CONCAT('%', :numeroFacture, '%'))
              ))
              AND (:dateDebut IS NULL OR v.dateVente >= :dateDebut)
              AND (:dateFin IS NULL OR v.dateVente <= :dateFin)
              AND (:modePaiement IS NULL OR LOWER(mp.libelle) = LOWER(:modePaiement))
              AND (:statut IS NULL OR LOWER(sv.libelle) = LOWER(:statut))
              AND (:montantMin IS NULL OR v.montantTotal >= :montantMin)
              AND (:montantMax IS NULL OR v.montantTotal <= :montantMax)
            """)
    Page<Vente> rechercherVentes(
            @Param("client") String client,
            @Param("produit") String produit,
            @Param("numeroFacture") String numeroFacture,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            @Param("modePaiement") String modePaiement,
            @Param("statut") String statut,
            @Param("montantMin") BigDecimal montantMin,
            @Param("montantMax") BigDecimal montantMax,
            Pageable pageable
    );
    @Query("SELECT FUNCTION('TO_CHAR', v.dateVente, 'YYYY-MM'), SUM(v.montantTotal) FROM Vente v GROUP BY FUNCTION('TO_CHAR', v.dateVente, 'YYYY-MM') ORDER BY 1")
    List<Object[]> sumRecettesParMois();

    @Query("SELECT FUNCTION('TO_CHAR', v.dateVente, 'YYYY-MM'), SUM(v.montantTotal) " +
            "FROM Vente v GROUP BY FUNCTION('TO_CHAR', v.dateVente, 'YYYY-MM') ORDER BY 1")
    List<Object[]> getRecettesParMois();

    @Query(value = """
        SELECT date_trunc(:granularite, v.date_vente) AS periode,
               COALESCE(SUM(v.montant_total), 0) AS montant,
               COUNT(DISTINCT v.id) AS nombre_ventes,
               COALESCE(SUM(lv.quantite), 0) AS quantite
        FROM vente v
        LEFT JOIN ligne_vente lv ON lv.id_vente = v.id
        JOIN statut_vente sv ON sv.id = v.id_statut_vente
        WHERE v.date_vente >= :debut AND v.date_vente <= :fin
          AND LOWER(sv.libelle) NOT IN ('annulée', 'annulee', 'en attente de paiement')
          AND (:idCategorie IS NULL OR EXISTS (
                SELECT 1 FROM ligne_vente lv2
                JOIN produit p2 ON p2.id = lv2.id_produit
                WHERE lv2.id_vente = v.id AND p2.id_categorie = :idCategorie
          ))
        GROUP BY periode
        ORDER BY periode ASC
        """, nativeQuery = true)
        List<Object[]> evolutionVentes(@Param("granularite") String granularite,
                                        @Param("debut") LocalDateTime debut,
                                        @Param("fin") LocalDateTime fin,
                                        @Param("idCategorie") Long idCategorie);
        }
