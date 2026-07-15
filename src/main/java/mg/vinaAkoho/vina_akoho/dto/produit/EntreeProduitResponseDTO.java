package mg.vinaAkoho.vina_akoho.dto.produit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EntreeProduitResponseDTO(
        Integer idFabrication,
        Long idLotProduit,
        String nomProduit,
        BigDecimal quantiteProduite,
        LocalDate dateFabrication,
        List<DetailConsommationMpDTO> matieresPremieresConsommees
) {
}
