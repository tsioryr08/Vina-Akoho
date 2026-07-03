package mg.vinaAkoho.vina_akoho.repository.ventes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mg.vinaAkoho.vina_akoho.entity.ventes.Vente;

public interface VenteRepository extends JpaRepository<Vente, Long> {

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
}
