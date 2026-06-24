package mg.vinaAkoho.vina_akoho.dto.matierespremieres;

import java.math.BigDecimal;
import java.util.List;

public record FicheDetailDTO(
        Integer id,
        String code,
        String nom,
        String fournisseurNom,
        String uniteLibelle,
        BigDecimal coutUnitaire,
        BigDecimal seuilMinimum,
        BigDecimal stockGlobal,
        BigDecimal pamp,
        List<LotDTO> lots
) {
}
