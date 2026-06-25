package mg.vinaAkoho.vina_akoho.dto.matierespremieres;

import java.math.BigDecimal;

public record MatierePremiereListDTO(
        Integer id,
        String nom,
        String fournisseurNom,
        String uniteLibelle,
        BigDecimal quantiteStock,
        BigDecimal seuilMinimum,
        BigDecimal pamp,
        String statut
) {
}
