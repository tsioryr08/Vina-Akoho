package mg.vinaAkoho.vina_akoho.entity.depense;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "depense")
public class Depense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date")
    private LocalDate date = LocalDate.now();

    @Column(name = "designation", nullable = false, length = 255)
    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categorie_depense", nullable = false)
    private CategorieDepense categorieDepense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phase", nullable = false)
    private Phase phase;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    // Jointure vers le statut de la dépense
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_depense", nullable = false)
    private StatutDepense statutDepense;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Constructeurs
    public Depense() {}

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public CategorieDepense getCategorieDepense() { return categorieDepense; }
    public void setCategorieDepense(CategorieDepense categorieDepense) { this.categorieDepense = categorieDepense; }

    public Phase getPhase() { return phase; }
    public void setPhase(Phase phase) { this.phase = phase; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public StatutDepense getStatutDepense() { return statutDepense; }
    public void setStatutDepense(StatutDepense statutDepense) { this.statutDepense = statutDepense; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
