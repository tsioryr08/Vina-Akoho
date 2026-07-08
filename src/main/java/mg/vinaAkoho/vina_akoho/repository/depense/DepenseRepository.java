package mg.vinaAkoho.vina_akoho.repository.depense;

import mg.vinaAkoho.vina_akoho.entity.depense.Depense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DepenseRepository extends JpaRepository<Depense, Integer> {

    @Query("SELECT SUM(d.montant) FROM Depense d " +
           "WHERE d.date BETWEEN :startDate AND :endDate " +
           "AND (:categorieId IS NULL OR d.categorieDepense.id = :categorieId) " +
           "AND d.statutDepense.id = 1")
    BigDecimal sumDepensesEntreDeuxDatesEtCategorie(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categorieId") Integer categorieId
    );

    @Query("SELECT c.libelle, SUM(d.montant) FROM Depense d JOIN d.categorieDepense c GROUP BY c.libelle")
    List<Object[]> sumDepensesParCategorie();

    @Query("SELECT p.libelle, SUM(d.montant) FROM Depense d JOIN d.phase p GROUP BY p.libelle")
    List<Object[]> sumDepensesParPhase();

    @Query("SELECT FUNCTION('TO_CHAR', d.date, 'YYYY-MM'), SUM(d.montant) " +
            "FROM Depense d GROUP BY FUNCTION('TO_CHAR', d.date, 'YYYY-MM') ORDER BY 1")
    List<Object[]> getDepensesParMois();
}