package mg.vinaAkoho.vina_akoho.dto.produit;

import java.util.List;

public record HistoriqueDataDTO(
    List<ProductionHistoriqueItemDTO> productions,
    PaginationDTO pagination,
    Integer highlightProductionId,
    String success,
    List<ProduitDTO> produits
) {}
