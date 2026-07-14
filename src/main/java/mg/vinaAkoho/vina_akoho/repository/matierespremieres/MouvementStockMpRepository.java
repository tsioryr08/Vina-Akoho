package mg.vinaAkoho.vina_akoho.repository.matierespremieres;

import java.math.BigDecimal;
import java.time.LocalDate;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MouvementStockMp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MouvementStockMpRepository extends JpaRepository<MouvementStockMp, Integer> {

    @Query("SELECT COALESCE(SUM(m.quantite), 0) FROM MouvementStockMp m WHERE m.lotMp.matierePremiere.id = :idMp AND m.typeMouvement.libelle = 'Sortie'")
    BigDecimal sommeTotalSorties(Integer idMp);

    @Query("SELECT MIN(m.dateMouvement) FROM MouvementStockMp m WHERE m.lotMp.matierePremiere.id = :idMp AND m.typeMouvement.libelle = 'Sortie' AND m.dateMouvement IS NOT NULL")
    LocalDate premiereDateSortieAvecDate(Integer idMp);
}