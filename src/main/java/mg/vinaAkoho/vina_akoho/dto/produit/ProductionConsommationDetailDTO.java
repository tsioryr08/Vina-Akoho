package mg.vinaAkoho.vina_akoho.dto.produit;

import java.math.BigDecimal;

public record ProductionConsommationDetailDTO(
        Integer idLotMp,
        String nomMatierePremiere,
        BigDecimal quantiteConsommee,
        BigDecimal quantiteRestanteLot,
        String unite
) {
}
