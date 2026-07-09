package mg.vinaAkoho.vina_akoho.service.finance;

import org.springframework.stereotype.Service;

import mg.vinaAkoho.vina_akoho.dto.finance.EvolutionMensuelleDTO;
import mg.vinaAkoho.vina_akoho.dto.finance.RapportFinancierMensuelDTO;
import mg.vinaAkoho.vina_akoho.repository.depense.DepenseRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.VenteRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Rapport Financier mensuel (F7 - Sprint 3) : Dépenses du mois, Recettes du mois,
 * Bénéfice et évolution mensuelle. Réutilise les mêmes requêtes que le module
 * Bénéfices existant (VenteRepository/DepenseRepository), sans modifier ce module.
 */
@Service
public class RapportFinancierService {

    private static final DateTimeFormatter FORMAT_MOIS = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);

    private final VenteRepository venteRepository;
    private final DepenseRepository depenseRepository;

    public RapportFinancierService(VenteRepository venteRepository, DepenseRepository depenseRepository) {
        this.venteRepository = venteRepository;
        this.depenseRepository = depenseRepository;
    }

    public RapportFinancierMensuelDTO calculerRapportFinancierMensuel(int nombreMois) {
        YearMonth moisCourant = YearMonth.now();
        List<EvolutionMensuelleDTO> evolution = new ArrayList<>();

        for (int i = nombreMois - 1; i >= 0; i--) {
            YearMonth mois = moisCourant.minusMonths(i);
            LocalDate debutMois = mois.atDay(1);
            LocalDate finMois = mois.atEndOfMonth();

            BigDecimal recettes = venteRepository.sumRecettesEntreDeuxDates(
                    debutMois.atStartOfDay(), finMois.atTime(LocalTime.MAX));
            BigDecimal depenses = depenseRepository.sumDepensesEntreDeuxDatesEtCategorie(
                    debutMois, finMois, null);

            String libelleMois = mois.format(FORMAT_MOIS);
            libelleMois = Character.toUpperCase(libelleMois.charAt(0)) + libelleMois.substring(1);

            evolution.add(new EvolutionMensuelleDTO(libelleMois, recettes, depenses));
        }

        EvolutionMensuelleDTO donneesMoisCourant = evolution.get(evolution.size() - 1);

        return new RapportFinancierMensuelDTO(
                donneesMoisCourant.getMois(),
                donneesMoisCourant.getTotalDepenses(),
                donneesMoisCourant.getTotalRecettes(),
                donneesMoisCourant.getBenefice(),
                evolution
        );
    }
}
