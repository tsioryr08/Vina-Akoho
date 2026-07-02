package mg.vinaAkoho.vina_akoho.dto.produit;

import java.math.BigDecimal;

public record DetailConsommationMpDTO(
        Integer idLotMp,
        String nomMp,
        BigDecimal quantiteConsommee
) {
}
