package mg.vinaAkoho.vina_akoho.dto.matierespremieres;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LotDTO(
        Integer id,
        LocalDate dateAchat,
        BigDecimal quantiteRestante,
        String statut,
        String fournisseurNom,
        BigDecimal coutUnitaire
) {
}