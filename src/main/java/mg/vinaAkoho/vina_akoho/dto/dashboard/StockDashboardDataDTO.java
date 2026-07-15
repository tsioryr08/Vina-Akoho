package mg.vinaAkoho.vina_akoho.dto.dashboard;

import java.util.List;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.MatierePremiereListDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.ProduitDTO;

public record StockDashboardDataDTO(
    List<MatierePremiereListDTO> alertesMp,
    List<ProduitDTO> produits
) {}
