package mg.vinaAkoho.vina_akoho.dto.entreeproduit;

import java.math.BigDecimal;

public record DetailConsommationMpDTO(
        Integer idLotMp,
        String nomMp,
        BigDecimal quantiteConsommee
) {
}