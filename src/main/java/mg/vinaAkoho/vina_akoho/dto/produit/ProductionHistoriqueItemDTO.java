package mg.vinaAkoho.vina_akoho.dto.produit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProductionHistoriqueItemDTO(
        Integer idFabrication,
        Long idLotProduit,
        Long idProduit,
        String nomProduit,
        BigDecimal quantiteProduite,
        BigDecimal quantiteRestanteLot,
        LocalDateTime dateFabrication,
        LocalDate datePeremption,
        String employeNomComplet
) {
}
