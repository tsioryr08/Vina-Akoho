package mg.vinaAkoho.vina_akoho.dto.stockmp;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;

public class SortieMpRequestDTO {

    @NotNull(message = "La catégorie du produit est obligatoire")
    private Integer idCategorie;

    @NotNull(message = "L'employé est obligatoire")
    private Integer idEmploye;

    private String referenceDocument;

    // Nouveau champ — quantité de produit à fabriquer. Valeur par défaut à 1
    // pour rester rétro-compatible avec le code existant (Tsiory) qui ne le
    // renseignait pas : besoin = quantiteMp × 1 = quantiteMp (comportement inchangé).
    private BigDecimal quantiteAProduire = BigDecimal.ONE;

    public Integer getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Integer idCategorie) { this.idCategorie = idCategorie; }
    public Integer getIdEmploye() { return idEmploye; }
    public void setIdEmploye(Integer idEmploye) { this.idEmploye = idEmploye; }
    public String getReferenceDocument() { return referenceDocument; }
    public void setReferenceDocument(String referenceDocument) { this.referenceDocument = referenceDocument; }

    public BigDecimal getQuantiteAProduire() { return quantiteAProduire; }
    public void setQuantiteAProduire(BigDecimal quantiteAProduire) {
        this.quantiteAProduire = quantiteAProduire != null ? quantiteAProduire : BigDecimal.ONE;
    }
}