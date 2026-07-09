package mg.vinaAkoho.vina_akoho.dto.finance;

import java.math.BigDecimal;

public class EvolutionMensuelleDTO {
    private String mois;
    private BigDecimal totalRecettes;
    private BigDecimal totalDepenses;
    private BigDecimal benefice;

    public EvolutionMensuelleDTO(String mois, BigDecimal totalRecettes, BigDecimal totalDepenses) {
        this.mois = mois;
        this.totalRecettes = totalRecettes != null ? totalRecettes : BigDecimal.ZERO;
        this.totalDepenses = totalDepenses != null ? totalDepenses : BigDecimal.ZERO;
        this.benefice = this.totalRecettes.subtract(this.totalDepenses);
    }

    public String getMois() { return mois; }
    public BigDecimal getTotalRecettes() { return totalRecettes; }
    public BigDecimal getTotalDepenses() { return totalDepenses; }
    public BigDecimal getBenefice() { return benefice; }
}
