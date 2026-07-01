package mg.vinaAkoho.vina_akoho.entity.entreeproduit;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Unite;

@Entity
@Table(name = "mouvement_stock_produit")
public class MouvementStockProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Réutilise l'entité TypeMouvement existante (module matierespremieres)
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_type_mouvement")
    private mg.vinaAkoho.vina_akoho.entity.matierespremieres.TypeMouvement typeMouvement;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lot_produit")
    private LotProduit lotProduit;

    @Column(nullable = false)
    private BigDecimal quantite;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_unite")
    private Unite unite;

    @Column(name = "id_employe", nullable = false)
    private Integer idEmploye;

    @Column(name = "date_mouvement")
    private LocalDate dateMouvement;

    private String observation;

    @Column(name = "reference_document")
    private String referenceDocument;

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public mg.vinaAkoho.vina_akoho.entity.matierespremieres.TypeMouvement getTypeMouvement() { return typeMouvement; }
    public void setTypeMouvement(mg.vinaAkoho.vina_akoho.entity.matierespremieres.TypeMouvement typeMouvement) { this.typeMouvement = typeMouvement; }
    public LotProduit getLotProduit() { return lotProduit; }
    public void setLotProduit(LotProduit lotProduit) { this.lotProduit = lotProduit; }
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
    public Unite getUnite() { return unite; }
    public void setUnite(Unite unite) { this.unite = unite; }
    public Integer getIdEmploye() { return idEmploye; }
    public void setIdEmploye(Integer idEmploye) { this.idEmploye = idEmploye; }
    public LocalDate getDateMouvement() { return dateMouvement; }
    public void setDateMouvement(LocalDate dateMouvement) { this.dateMouvement = dateMouvement; }
    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }
    public String getReferenceDocument() { return referenceDocument; }
    public void setReferenceDocument(String referenceDocument) { this.referenceDocument = referenceDocument; }
}