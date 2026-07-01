package mg.vinaAkoho.vina_akoho.dto.stockmp;

import jakarta.validation.constraints.NotNull;

public class SortieMpRequestDTO {

    @NotNull(message = "La catégorie du produit est obligatoire")
    private Integer idCategorie;

    @NotNull(message = "L'employé est obligatoire")
    private Integer idEmploye;

    private String referenceDocument;

    public Integer getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Integer idCategorie) { this.idCategorie = idCategorie; }
    public Integer getIdEmploye() { return idEmploye; }
    public void setIdEmploye(Integer idEmploye) { this.idEmploye = idEmploye; }
    public String getReferenceDocument() { return referenceDocument; }
    public void setReferenceDocument(String referenceDocument) { this.referenceDocument = referenceDocument; }
}