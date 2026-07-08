package mg.vinaAkoho.vina_akoho.dto.ventes;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TopCategorieStatDTO {
    private Long idCategorie;
    private String libelleCategorie;
    private BigDecimal quantiteVendue;
    private BigDecimal montantVentes;
    private Long nombreVentes;
    private BigDecimal pourcentageQuantite;
    private BigDecimal pourcentageMontant;

    public TopCategorieStatDTO(Long idCategorie, String libelleCategorie,
                                BigDecimal quantiteVendue, BigDecimal montantVentes, Long nombreVentes) {
        this.idCategorie = idCategorie;
        this.libelleCategorie = libelleCategorie;
        this.quantiteVendue = quantiteVendue;
        this.montantVentes = montantVentes;
        this.nombreVentes = nombreVentes;
    }
}