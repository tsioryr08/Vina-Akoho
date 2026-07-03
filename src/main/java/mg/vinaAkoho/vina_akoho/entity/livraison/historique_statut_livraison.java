package mg.vinaAkoho.vina_akoho.entity.livraison;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.vinaAkoho.vina_akoho.entity.livraison.historique_statut_livraison;

@Entity
@Table(name = "historique_statut_livraison")
@Getter
@Setter
@NoArgsConstructor
public class historique_statut_livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_livraison", nullable = false)
    private Integer idLivraison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ancien_statut", referencedColumnName = "id")
    private statutLivraison ancienStatut;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nouveau_statut", nullable = false, referencedColumnName = "id")
    private statutLivraison nouveauStatut;

    @Column(name = "date_changement")
    private LocalDateTime dateChangement;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
