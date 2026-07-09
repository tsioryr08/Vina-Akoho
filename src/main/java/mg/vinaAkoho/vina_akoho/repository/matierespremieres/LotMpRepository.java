package mg.vinaAkoho.vina_akoho.repository.matierespremieres;

import java.math.BigDecimal;
import java.util.List;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.LotMp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LotMpRepository extends JpaRepository<LotMp, Integer> {

    List<LotMp> findByMatierePremiereIdOrderByDateAchatAscIdAsc(Integer idMp);

    @Query("SELECT COALESCE(SUM(l.quantiteRestante), 0) FROM LotMp l WHERE l.matierePremiere.id = :idMp")
    BigDecimal sommeQuantiteRestante(Integer idMp);

    //rajout de cette methode pour que ca soit compatible avec mp
    List<LotMp> findByMatierePremiereIdAndQuantiteRestanteGreaterThanOrderByDateAchatAsc(
            Integer idMp,
            BigDecimal seuil
    );

    // Nombre de lots MP expirant bientôt (datePeremption dans la fenêtre, et quantité restante > 0)
    @Query("SELECT COUNT(l) FROM LotMp l WHERE l.quantiteRestante > 0 AND l.datePeremption IS NOT NULL AND l.datePeremption BETWEEN :debut AND :fin")
    long compterLotsExpirantBientot(@org.springframework.data.repository.query.Param("debut") java.time.LocalDate debut,
                                    @org.springframework.data.repository.query.Param("fin") java.time.LocalDate fin);

}

