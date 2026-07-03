package mg.vinaAkoho.vina_akoho.repository.depense;

import mg.vinaAkoho.vina_akoho.entity.depense.Depense; // Ajustez le package selon votre projet
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface DepenseRepository extends JpaRepository<Depense, Integer> {

    @Query("SELECT SUM(d.montant) FROM Depense d " +
           "WHERE d.date BETWEEN :startDate AND :endDate " +
           "AND (:categorieId IS NULL OR d.categorieDepense.id = :categorieId) " +
           "AND d.statutDepense.id = 1")
    BigDecimal sumDepensesEntreDeuxDatesEtCategorie(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate,
            @Param("categorieId") Integer categorieId);
}