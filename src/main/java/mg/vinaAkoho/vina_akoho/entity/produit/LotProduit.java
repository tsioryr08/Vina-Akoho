package mg.vinaAkoho.vina_akoho.entity.produit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lot_produit")
@Getter
@Setter
@NoArgsConstructor
public class LotProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    @Column(name = "quantite_initiale", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantiteInitiale;

    @Column(name = "quantite_restante", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantiteRestante;

    @Column(name = "date_fabrication")
    private LocalDate dateFabrication;

    @Column(name = "date_peremption")
    private LocalDate datePeremption;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
