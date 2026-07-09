package mg.vinaAkoho.vina_akoho.dto.finance;

import java.math.BigDecimal;
import java.util.List;

public class RapportFinancierMensuelDTO {
    private String moisCourant;
    private BigDecimal depensesMois;
    private BigDecimal recettesMois;
    private BigDecimal beneficeMois;
    private List<EvolutionMensuelleDTO> evolutionMensuelle;

    public RapportFinancierMensuelDTO(String moisCourant, BigDecimal depensesMois, BigDecimal recettesMois,
                                       BigDecimal beneficeMois, List<EvolutionMensuelleDTO> evolutionMensuelle) {
        this.moisCourant = moisCourant;
        this.depensesMois = depensesMois;
        this.recettesMois = recettesMois;
        this.beneficeMois = beneficeMois;
        this.evolutionMensuelle = evolutionMensuelle;
    }

    public String getMoisCourant() { return moisCourant; }
    public BigDecimal getDepensesMois() { return depensesMois; }
    public BigDecimal getRecettesMois() { return recettesMois; }
    public BigDecimal getBeneficeMois() { return beneficeMois; }
    public List<EvolutionMensuelleDTO> getEvolutionMensuelle() { return evolutionMensuelle; }
}
