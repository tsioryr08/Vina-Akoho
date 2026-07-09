package mg.vinaAkoho.vina_akoho.dto.prevision;

import java.math.BigDecimal;

public record PrevisionProductionDTO(
        Long produitId,
        String produitNom,
        String categorie,
        String unite,
        BigDecimal quantiteVendue,
        BigDecimal moyenneJournaliere,
        BigDecimal stockActuel,
        BigDecimal objectifStock,
        BigDecimal propositionProduction,
        String statut,
        boolean recetteDisponible) {
}
