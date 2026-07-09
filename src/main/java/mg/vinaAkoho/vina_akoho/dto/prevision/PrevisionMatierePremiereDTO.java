package mg.vinaAkoho.vina_akoho.dto.prevision;

import java.math.BigDecimal;

public record PrevisionMatierePremiereDTO(
        Integer matierePremiereId,
        String code,
        String nom,
        String fournisseur,
        String unite,
        BigDecimal besoinEstime,
        BigDecimal stockActuel,
        BigDecimal stockSecurite,
        BigDecimal quantiteACommander,
        String statut) {
}
