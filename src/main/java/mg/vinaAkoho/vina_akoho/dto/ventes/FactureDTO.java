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
public class FactureDTO {

    private Long id;
    private String numero;
    private LocalDate dateEmission;
    private BigDecimal montantHt;
    private BigDecimal tauxTva;
    private BigDecimal montantTva;
    private BigDecimal montantTtc;
}
