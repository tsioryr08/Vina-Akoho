package mg.vinaAkoho.vina_akoho.entity.matierespremieres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "lot_mp")
public class LotMp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_mp")
    private MatierePremiere matierePremiere;

    @Column(name = "quantite_initiale", nullable = false)
    private BigDecimal quantiteInitiale;

    @Column(name = "quantite_restante", nullable = false)
    private BigDecimal quantiteRestante;

    @Column(name = "date_achat", nullable = false)
    private LocalDate dateAchat;

    @Column(name = "date_peremption")
    private LocalDate datePeremption;

    // rajout de ces colonnes 
    @Column(name = "id_mouvement_entree")
    private Integer idMouvementEntree;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MatierePremiere getMatierePremiere() {
        return matierePremiere;
    }

    public void setMatierePremiere(MatierePremiere matierePremiere) {
        this.matierePremiere = matierePremiere;
    }

    public BigDecimal getQuantiteInitiale() {
        return quantiteInitiale;
    }

    public void setQuantiteInitiale(BigDecimal quantiteInitiale) {
        this.quantiteInitiale = quantiteInitiale;
    }

    public BigDecimal getQuantiteRestante() {
        return quantiteRestante;
    }

    public void setQuantiteRestante(BigDecimal quantiteRestante) {
        this.quantiteRestante = quantiteRestante;
    }

    public LocalDate getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDate dateAchat) {
        this.dateAchat = dateAchat;
    }

    public LocalDate getDatePeremption() {
        return datePeremption;
    }

    public void setDatePeremption(LocalDate datePeremption) {
        this.datePeremption = datePeremption;
    }

    //ajout de getters et setters pour idMouvementEntree et createdAt
    public Integer getIdMouvementEntree() {
        return idMouvementEntree;
    }

    public void setIdMouvementEntree(Integer idMouvementEntree) {
        this.idMouvementEntree = idMouvementEntree;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
