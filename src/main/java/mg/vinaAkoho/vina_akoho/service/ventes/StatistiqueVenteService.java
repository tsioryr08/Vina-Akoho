package mg.vinaAkoho.vina_akoho.service.ventes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.ventes.EvolutionVenteStatDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.StatistiqueVenteReponseDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.TopCategorieStatDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.TopProduitStatDTO;
import mg.vinaAkoho.vina_akoho.repository.ventes.LigneVenteRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.VenteRepository;

@Service
@RequiredArgsConstructor
public class StatistiqueVenteService {

    private static final int TOP_N = 10;

    private final LigneVenteRepository ligneVenteRepository;
    private final VenteRepository venteRepository;

    public StatistiqueVenteReponseDTO obtenirStatistiques(LocalDate dateDebut, LocalDate dateFin,
                                                           Long idCategorie, String triProduits,
                                                           String granularite) {
        LocalDateTime debut = dateDebut.atStartOfDay();
        LocalDateTime fin = dateFin.atTime(23, 59, 59);
        Pageable top10 = PageRequest.of(0, TOP_N);
        boolean parMontant = "montant".equalsIgnoreCase(triProduits);

        List<TopProduitStatDTO> topProduits = parMontant
                ? ligneVenteRepository.topProduitsParMontant(debut, fin, idCategorie, top10)
                : ligneVenteRepository.topProduitsParQuantite(debut, fin, idCategorie, top10);

        List<TopCategorieStatDTO> topCategories = parMontant
                ? ligneVenteRepository.topCategoriesParMontant(debut, fin, idCategorie, top10)
                : ligneVenteRepository.topCategoriesParQuantite(debut, fin, idCategorie, top10);

        // Pourcentages calculés sur le total réel de la période filtrée (pas seulement le Top 10)
        List<Object[]> totaux = ligneVenteRepository.totauxPeriode(debut, fin, idCategorie);
        BigDecimal quantiteTotale = BigDecimal.ZERO;
        BigDecimal montantTotal = BigDecimal.ZERO;
        if (!totaux.isEmpty() && totaux.get(0) != null) {
            Object[] ligne = totaux.get(0);
            quantiteTotale = (BigDecimal) ligne[0];
            montantTotal = (BigDecimal) ligne[1];
        }

        for (TopProduitStatDTO dto : topProduits) {
            dto.setPourcentageQuantite(pourcentage(dto.getQuantiteVendue(), quantiteTotale));
            dto.setPourcentageMontant(pourcentage(dto.getMontantVentes(), montantTotal));
        }
        for (TopCategorieStatDTO dto : topCategories) {
            dto.setPourcentageQuantite(pourcentage(dto.getQuantiteVendue(), quantiteTotale));
            dto.setPourcentageMontant(pourcentage(dto.getMontantVentes(), montantTotal));
        }

        List<EvolutionVenteStatDTO> evolution = construireEvolution(granularite, debut, fin, idCategorie);

        return StatistiqueVenteReponseDTO.builder()
                .topProduits(topProduits)
                .topCategories(topCategories)
                .evolution(evolution)
                .build();
    }

    // IMPORTANT : toujours scale + RoundingMode sur BigDecimal.divide(), sinon
    // ArithmeticException dès que le résultat n'est pas exact (même piège déjà
    // corrigé dans VenteController.historique()).
    private BigDecimal pourcentage(BigDecimal partie, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0 || partie == null) {
            return BigDecimal.ZERO;
        }
        return partie.divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    private List<EvolutionVenteStatDTO> construireEvolution(String granularite, LocalDateTime debut,
                                                              LocalDateTime fin, Long idCategorie) {
        String granulariteSql = switch (granularite == null ? "jour" : granularite.toLowerCase()) {
            case "semaine" -> "week";
            case "mois" -> "month";
            default -> "day";
        };

        List<Object[]> lignes = venteRepository.evolutionVentes(granulariteSql, debut, fin, idCategorie);

        DateTimeFormatter formatJour = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatMoisAnnee = DateTimeFormatter.ofPattern("MM/yyyy");

        List<EvolutionVenteStatDTO> resultat = new ArrayList<>();
        for (Object[] ligne : lignes) {
            LocalDateTime periodeTs = (LocalDateTime) ligne[0];
            LocalDateTime periode = periodeTs;

            String label = switch (granulariteSql) {
                case "week" -> "Semaine du " + periode.toLocalDate().format(formatJour);
                case "month" -> periode.toLocalDate().format(formatMoisAnnee);
                default -> periode.toLocalDate().format(formatJour);
            };

            resultat.add(EvolutionVenteStatDTO.builder()
                    .periode(label)
                    .quantiteVendue((BigDecimal) ligne[3])
                    .montantVentes((BigDecimal) ligne[1])
                    .nombreVentes(((Number) ligne[2]).longValue())
                    .build());
        }
        return resultat;
    }
}