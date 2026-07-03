package mg.vinaAkoho.vina_akoho.dto.ventes;

import java.math.BigDecimal;
import java.time.LocalDate;

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
public class RecetteVenteDTO {

    private LocalDate dateVente;
    private Long produitId;
    private String produitNom;
    private BigDecimal prixVente;
    private BigDecimal quantiteVendue;
    private BigDecimal montantRecette;
}
