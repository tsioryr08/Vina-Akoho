package mg.vinaAkoho.vina_akoho.dto.ventes;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class TopProduitStatDTO {
    private Long idProduit;
    private String nomProduit;
    private String categorieLibelle;
    private BigDecimal quantiteVendue;
    private BigDecimal montantVentes;
    private Long nombreVentes;
    private BigDecimal pourcentageQuantite;
    private BigDecimal pourcentageMontant;

    // Constructeur utilisé par la clause JPQL "SELECT new ...(...)" — ne pas
    // le remplacer par @AllArgsConstructor Lombok, les 2 champs pourcentage
    // sont calculés après coup dans le service, pas dans la requête.
    public TopProduitStatDTO(Long idProduit, String nomProduit, String categorieLibelle,
                              BigDecimal quantiteVendue, BigDecimal montantVentes, Long nombreVentes) {
        this.idProduit = idProduit;
        this.nomProduit = nomProduit;
        this.categorieLibelle = categorieLibelle;
        this.quantiteVendue = quantiteVendue;
        this.montantVentes = montantVentes;
        this.nombreVentes = nombreVentes;
    }
}