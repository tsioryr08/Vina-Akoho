package mg.vinaAkoho.vina_akoho.dto.ventes;

import java.math.BigDecimal;
import java.util.List;

public record PanierResumeDTO(
        List<PanierItemDTO> items,
        BigDecimal total,
        String message
) {
}
