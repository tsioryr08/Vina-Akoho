package mg.vinaAkoho.vina_akoho.dto.recetteproduit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecetteProduitDTO {
    private Integer id;
    private Integer idCategorie;
    private Integer version;
    private Integer idMp;
    private String nomMp;
    private BigDecimal quantiteMp;
    private Integer idUnite;
    private String libelleUnite;
    private Boolean isActive;
    private LocalDateTime dateCreation;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Integer idCategorie) { this.idCategorie = idCategorie; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Integer getIdMp() { return idMp; }
    public void setIdMp(Integer idMp) { this.idMp = idMp; }
    public String getNomMp() { return nomMp; }
    public void setNomMp(String nomMp) { this.nomMp = nomMp; }
    public BigDecimal getQuantiteMp() { return quantiteMp; }
    public void setQuantiteMp(BigDecimal quantiteMp) { this.quantiteMp = quantiteMp; }
    public Integer getIdUnite() { return idUnite; }
    public void setIdUnite(Integer idUnite) { this.idUnite = idUnite; }
    public String getLibelleUnite() { return libelleUnite; }
    public void setLibelleUnite(String libelleUnite) { this.libelleUnite = libelleUnite; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
