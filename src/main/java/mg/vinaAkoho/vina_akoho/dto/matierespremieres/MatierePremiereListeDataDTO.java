package mg.vinaAkoho.vina_akoho.dto.matierespremieres;

import java.math.BigDecimal;
import java.util.List;

public record MatierePremiereListeDataDTO(
    List<MatierePremiereListDTO> mps,
    BigDecimal totalStock,
    long totalArticles,
    long matieresSeuil
) {}
