package mg.vinaAkoho.vina_akoho.dto.produit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProductionDetailDTO(
        Integer idFabrication,
        Long idLotProduit,
        Long idProduit,
        String nomProduit,
        BigDecimal quantiteProduite,
        BigDecimal quantiteRestanteLot,
        LocalDateTime dateFabrication,
        LocalDate datePeremption,
        String employeNomComplet,
        List<ProductionConsommationDetailDTO> consommations
) {
}
