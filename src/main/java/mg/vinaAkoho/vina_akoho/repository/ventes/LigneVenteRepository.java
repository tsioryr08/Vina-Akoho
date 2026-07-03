package mg.vinaAkoho.vina_akoho.repository.ventes;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.vinaAkoho.vina_akoho.dto.ventes.RecetteVenteProjection;
import mg.vinaAkoho.vina_akoho.entity.ventes.LigneVente;

public interface LigneVenteRepository extends JpaRepository<LigneVente, Long> {

    List<LigneVente> findByVenteId(Long venteId);

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
              AND LOWER(sv.libelle) NOT IN ('annulée', 'annulee')
            GROUP BY CAST(v.date_vente AS date), p.id, p.nom, p.prix_vente
            ORDER BY CAST(v.date_vente AS date) DESC, p.nom ASC
            """, nativeQuery = true)
    List<RecetteVenteProjection> calculerRecettesParPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);
}
