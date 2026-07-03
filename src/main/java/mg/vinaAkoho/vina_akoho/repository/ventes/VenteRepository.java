package mg.vinaAkoho.vina_akoho.repository.ventes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

}
