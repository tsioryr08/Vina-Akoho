package mg.vinaAkoho.vina_akoho.dto.stockmp;

import java.math.BigDecimal;

public class MouvementStockMpDTO {
    private Integer id;
    private Integer idLotMp;
    private String nomMp;
    private BigDecimal quantite;
    private Integer idUnite;
    private String typeMouvement;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdLotMp() { return idLotMp; }
    public void setIdLotMp(Integer idLotMp) { this.idLotMp = idLotMp; }
    public String getNomMp() { return nomMp; }
    public void setNomMp(String nomMp) { this.nomMp = nomMp; }
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
    public Integer getIdUnite() { return idUnite; }
    public void setIdUnite(Integer idUnite) { this.idUnite = idUnite; }
    public String getTypeMouvement() { return typeMouvement; }
    public void setTypeMouvement(String typeMouvement) { this.typeMouvement = typeMouvement; }
}