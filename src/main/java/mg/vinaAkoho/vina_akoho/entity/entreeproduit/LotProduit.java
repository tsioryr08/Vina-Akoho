package mg.vinaAkoho.vina_akoho.entity.entreeproduit;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;

@Entity
@Table(name = "lot_produit")
public class LotProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_produit")
    private Produit produit;

    @Column(name = "quantite_initiale", nullable = false)
    private BigDecimal quantiteInitiale;

    @Column(name = "quantite_restante", nullable = false)
    private BigDecimal quantiteRestante;

    @Column(name = "date_fabrication", nullable = false)
    private LocalDate dateFabrication;

    @Column(name = "date_peremption")
    private LocalDate datePeremption;

    @Column(name = "id_mouvement_entree")
    private Integer idMouvementEntree;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }
    public BigDecimal getQuantiteInitiale() { return quantiteInitiale; }
    public void setQuantiteInitiale(BigDecimal quantiteInitiale) { this.quantiteInitiale = quantiteInitiale; }
    public BigDecimal getQuantiteRestante() { return quantiteRestante; }
    public void setQuantiteRestante(BigDecimal quantiteRestante) { this.quantiteRestante = quantiteRestante; }
    public LocalDate getDateFabrication() { return dateFabrication; }
    public void setDateFabrication(LocalDate dateFabrication) { this.dateFabrication = dateFabrication; }
    public LocalDate getDatePeremption() { return datePeremption; }
    public void setDatePeremption(LocalDate datePeremption) { this.datePeremption = datePeremption; }
    public Integer getIdMouvementEntree() { return idMouvementEntree; }
    public void setIdMouvementEntree(Integer idMouvementEntree) { this.idMouvementEntree = idMouvementEntree; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}