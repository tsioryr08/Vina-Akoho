package mg.vinaAkoho.vina_akoho.dto.ventes;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneVenteDTO {

    private String nomProduit;
    private BigDecimal quantite;
    private String unite;
    private BigDecimal prixUnitaire;
    private BigDecimal montant;
}
