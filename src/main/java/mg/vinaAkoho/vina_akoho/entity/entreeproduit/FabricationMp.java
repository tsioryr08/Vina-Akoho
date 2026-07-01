package mg.vinaAkoho.vina_akoho.entity.entreeproduit;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.LotMp;

@Entity
@Table(name = "fabrication_mp")
public class FabricationMp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_fabrication")
    private Fabrication fabrication;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lot_mp")
    private LotMp lotMp;

    @Column(nullable = false)
    private BigDecimal quantite;

    @Column(name = "id_unite", nullable = false)
    private Integer idUnite;

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Fabrication getFabrication() { return fabrication; }
    public void setFabrication(Fabrication fabrication) { this.fabrication = fabrication; }
    public LotMp getLotMp() { return lotMp; }
    public void setLotMp(LotMp lotMp) { this.lotMp = lotMp; }
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
    public Integer getIdUnite() { return idUnite; }
    public void setIdUnite(Integer idUnite) { this.idUnite = idUnite; }
}