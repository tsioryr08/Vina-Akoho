package mg.vinaAkoho.vina_akoho.dto.recetteproduit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class LigneRecetteProduitDTO {

    @NotNull(message = "La matière première est obligatoire")
    private Integer idMp;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private BigDecimal quantiteMp;

    @NotNull(message = "L'unité est obligatoire")
    private Integer idUnite;

    public Integer getIdMp() { return idMp; }
    public void setIdMp(Integer idMp) { this.idMp = idMp; }
    public BigDecimal getQuantiteMp() { return quantiteMp; }
    public void setQuantiteMp(BigDecimal quantiteMp) { this.quantiteMp = quantiteMp; }
    public Integer getIdUnite() { return idUnite; }
    public void setIdUnite(Integer idUnite) { this.idUnite = idUnite; }
}
