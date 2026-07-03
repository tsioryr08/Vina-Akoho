package mg.vinaAkoho.vina_akoho.repository.ventes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
              AND LOWER(v.statutVente.libelle) IN ('validée', 'validee')
            """)
    BigDecimal sommeReglementsClient(@Param("clientId") Integer clientId);

    @Query("""
            SELECT COUNT(v)
            FROM Vente v
            WHERE v.dateVente >= :debut
              AND v.dateVente < :fin
              AND LOWER(v.statutVente.libelle) NOT IN ('annulée', 'annulee')
            """)
    long compterVentesValideesEntre(@Param("debut") LocalDateTime debut,
                                     @Param("fin") LocalDateTime fin);

    @Query("""
            SELECT COALESCE(SUM(v.montantTotal), 0)
            FROM Vente v
            WHERE v.dateVente >= :debut
              AND v.dateVente < :fin
              AND LOWER(v.statutVente.libelle) NOT IN ('annulée', 'annulee')
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
}
