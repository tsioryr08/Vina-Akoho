package mg.vinaAkoho.vina_akoho.dto.produit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriquePrixProduitDTO {
    
    private Long id;
    private Long idProduit;
    private String nomProduit;
    private BigDecimal ancienPrix;
    private BigDecimal nouveauPrix;
    private LocalDateTime dateModification;
    private String nomEmploye;
}
