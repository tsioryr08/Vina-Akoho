package mg.vinaAkoho.vina_akoho.entity.recetteproduit;

import jakarta.persistence.*;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MatierePremiere;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recette_produit")
public class RecetteProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_categorie", nullable = false)
    private Integer idCategorie;

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mp", nullable = false)
    private MatierePremiere matierePremiere;

    @Column(name = "quantite_mp", nullable = false)
    private BigDecimal quantiteMp;

    @Column(name = "id_unite", nullable = false)
    private Integer idUnite;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "id_employe_creation")
    private Integer idEmployeCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Integer idCategorie) { this.idCategorie = idCategorie; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public MatierePremiere getMatierePremiere() { return matierePremiere; }
    public void setMatierePremiere(MatierePremiere matierePremiere) { this.matierePremiere = matierePremiere; }
    public BigDecimal getQuantiteMp() { return quantiteMp; }
    public void setQuantiteMp(BigDecimal quantiteMp) { this.quantiteMp = quantiteMp; }
    public Integer getIdUnite() { return idUnite; }
    public void setIdUnite(Integer idUnite) { this.idUnite = idUnite; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public Integer getIdEmployeCreation() { return idEmployeCreation; }
    public void setIdEmployeCreation(Integer idEmployeCreation) { this.idEmployeCreation = idEmployeCreation; }
}
