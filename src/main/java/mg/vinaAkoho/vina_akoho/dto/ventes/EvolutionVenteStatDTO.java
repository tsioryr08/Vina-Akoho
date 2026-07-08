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
public class EvolutionVenteStatDTO {
    private String periode;
    private BigDecimal quantiteVendue;
    private BigDecimal montantVentes;
    private Long nombreVentes;
}