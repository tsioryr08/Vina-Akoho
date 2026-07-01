package mg.vinaAkoho.vina_akoho.dto.recetteproduit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class CreateRecetteProduitDTO {

    @NotNull(message = "La catégorie est obligatoire")
    private Integer idCategorie;

    @NotNull(message = "Les lignes de recette sont obligatoires")
    private List<LigneRecetteProduitDTO> lignes;

    private Integer idEmployeCreation;

    public Integer getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Integer idCategorie) { this.idCategorie = idCategorie; }
    public List<LigneRecetteProduitDTO> getLignes() { return lignes; }
    public void setLignes(List<LigneRecetteProduitDTO> lignes) { this.lignes = lignes; }
    public Integer getIdEmployeCreation() { return idEmployeCreation; }
    public void setIdEmployeCreation(Integer idEmployeCreation) { this.idEmployeCreation = idEmployeCreation; }
}
