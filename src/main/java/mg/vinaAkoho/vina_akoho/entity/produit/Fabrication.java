package mg.vinaAkoho.vina_akoho.entity.produit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;

@Entity
@Table(name = "fabrication")
public class Fabrication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_fabrication")
    private LocalDateTime dateFabrication;

    @Column(name = "quantite_produite", nullable = false)
    private BigDecimal quantiteProduite;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lot_produit")
    private LotProduit lotProduit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_employe")
    private Employe employe;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.dateFabrication == null) this.dateFabrication = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getDateFabrication() { return dateFabrication; }
    public void setDateFabrication(LocalDateTime dateFabrication) { this.dateFabrication = dateFabrication; }
    public BigDecimal getQuantiteProduite() { return quantiteProduite; }
    public void setQuantiteProduite(BigDecimal quantiteProduite) { this.quantiteProduite = quantiteProduite; }
    public LotProduit getLotProduit() { return lotProduit; }
    public void setLotProduit(LotProduit lotProduit) { this.lotProduit = lotProduit; }
    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
