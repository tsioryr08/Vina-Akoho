package mg.vinaAkoho.vina_akoho.repository.ventes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mg.vinaAkoho.vina_akoho.entity.ventes.Commande;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    @Query("""
            SELECT COUNT(c)
            FROM Commande c
            WHERE LOWER(c.statutCommande.libelle) IN ('en attente', 'en cours')
            """)
    long compterCommandesEnAttente();
}
