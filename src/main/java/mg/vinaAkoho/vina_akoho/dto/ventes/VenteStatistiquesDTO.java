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
public class VenteStatistiquesDTO {

    private long ventesDuJour;
    private long variationVentesHier;
    private BigDecimal chiffreAffairesMois;
    private long commandesEnAttente;
    private BigDecimal tauxConversion;
}
