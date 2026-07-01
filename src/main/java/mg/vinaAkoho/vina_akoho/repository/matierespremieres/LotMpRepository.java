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

}
