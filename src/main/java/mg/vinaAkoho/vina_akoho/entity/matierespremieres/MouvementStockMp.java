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

@Entity
@Table(name = "mouvement_stock_mp")
public class MouvementStockMp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_type_mouvement")
    private TypeMouvement typeMouvement;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lot_mp")
    private LotMp lotMp;

    @Column(nullable = false)
    private BigDecimal quantite;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_unite")
    private Unite unite;

    @Column(name = "id_employe", nullable = false)
    private Integer idEmploye;

    @Column(name = "date_mouvement")
    private java.time.LocalDate dateMouvement;

    @Column
    private String observation;

    @Column(name = "reference_document")
    private String referenceDocument;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TypeMouvement getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(TypeMouvement typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public LotMp getLotMp() {
        return lotMp;
    }

    public void setLotMp(LotMp lotMp) {
        this.lotMp = lotMp;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public Unite getUnite() {
        return unite;
    }

    public void setUnite(Unite unite) {
        this.unite = unite;
    }

    public Integer getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(Integer idEmploye) {
        this.idEmploye = idEmploye;
    }

    public java.time.LocalDate getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(java.time.LocalDate dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getReferenceDocument() {
        return referenceDocument;
    }

    public void setReferenceDocument(String referenceDocument) {
        this.referenceDocument = referenceDocument;
    }
}