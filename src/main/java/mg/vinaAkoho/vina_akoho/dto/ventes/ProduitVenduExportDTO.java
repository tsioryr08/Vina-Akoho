package mg.vinaAkoho.vina_akoho.dto.ventes;

import java.math.BigDecimal;

public record ProduitVenduExportDTO(
        String produit,
        BigDecimal quantite,
        BigDecimal chiffreAffaires,
        BigDecimal partDuCA
) {
}
