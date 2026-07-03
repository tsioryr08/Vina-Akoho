package mg.vinaAkoho.vina_akoho.entity.depense;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "depense")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Depense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "designation", nullable = false, length = 255)
    private String designation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categorie_depense", nullable = false)
    private CategorieDepense categorieDepense;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_phase", nullable = false)
    private Phase phase;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_statut_depense", nullable = false)
    private StatutDepense statutDepense;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Integer getIdCategorieDepense() {
        return categorieDepense == null ? null : categorieDepense.getId();
    }

    public void setIdCategorieDepense(Integer idCategorieDepense) {
        if (idCategorieDepense == null) {
            this.categorieDepense = null;
            return;
        }
        CategorieDepense categorieDepense = new CategorieDepense();
        categorieDepense.setId(idCategorieDepense);
        this.categorieDepense = categorieDepense;
    }

    public Integer getIdPhase() {
        return phase == null ? null : phase.getId();
    }

    public void setIdPhase(Integer idPhase) {
        if (idPhase == null) {
            this.phase = null;
            return;
        }
        Phase phase = new Phase();
        phase.setId(idPhase);
        this.phase = phase;
    }

    public Integer getIdStatutDepense() {
        return statutDepense == null ? null : statutDepense.getId();
    }

    public void setIdStatutDepense(Integer idStatutDepense) {
        if (idStatutDepense == null) {
            this.statutDepense = null;
            return;
        }
        StatutDepense statutDepense = new StatutDepense();
        statutDepense.setId(idStatutDepense);
        this.statutDepense = statutDepense;
    }

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDate.now();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
