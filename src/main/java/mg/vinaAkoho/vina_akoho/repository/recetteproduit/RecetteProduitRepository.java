package mg.vinaAkoho.vina_akoho.repository.recetteproduit;

import mg.vinaAkoho.vina_akoho.entity.recetteproduit.RecetteProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecetteProduitRepository extends JpaRepository<RecetteProduit, Integer> {
    List<RecetteProduit> findByIdCategorieAndIsActiveTrue(Integer idCategorie);
    List<RecetteProduit> findByIdCategorie(Integer idCategorie);
}
