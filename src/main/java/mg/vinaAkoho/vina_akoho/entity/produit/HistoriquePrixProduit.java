package mg.vinaAkoho.vina_akoho.entity.produit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_prix_produit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoriquePrixProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    @Column(name = "ancien_prix", nullable = false, precision = 10, scale = 2)
    private BigDecimal ancienPrix;

    @Column(name = "nouveau_prix", nullable = false, precision = 10, scale = 2)
    private BigDecimal nouveauPrix;

    @Column(name = "date_modification", nullable = false, updatable = false)
    private LocalDateTime dateModification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employe")
    private Employe employe;

    @PrePersist
    protected void onCreate() {
        this.dateModification = LocalDateTime.now();
    }
}
