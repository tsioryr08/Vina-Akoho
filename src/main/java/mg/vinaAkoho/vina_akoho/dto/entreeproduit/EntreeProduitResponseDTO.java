package mg.vinaAkoho.vina_akoho.dto.entreeproduit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EntreeProduitResponseDTO(
        Integer idLotProduit,
        String nomProduit,
        BigDecimal quantiteProduite,
        LocalDate dateFabrication,
        List<DetailConsommationMpDTO> matieresPremieresConsommees
) {
}