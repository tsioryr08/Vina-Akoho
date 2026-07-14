package mg.vinaAkoho.vina_akoho.service.benefice;

import mg.vinaAkoho.vina_akoho.entity.depense.CategorieDepense;
import mg.vinaAkoho.vina_akoho.repository.depense.CategorieDepenseRepository;
import mg.vinaAkoho.vina_akoho.repository.depense.DepenseRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.VenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficeServiceTest {

    @Mock
    private VenteRepository venteRepository;

    @Mock
    private DepenseRepository depenseRepository;

    @Mock
    private CategorieDepenseRepository categorieDepenseRepository;

    @InjectMocks
    private BeneficeService beneficeService;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @BeforeEach
    void setUp() {
        dateDebut = LocalDate.of(2026, 7, 1);
        dateFin = LocalDate.of(2026, 7, 31);
    }

    @Test
    void testCalculerBeneficeParPeriodeEtCategorie_RecettesSuperieuresDepenses() {
        LocalDateTime startDateTime = dateDebut.atStartOfDay();
        LocalDateTime endDateTime = dateFin.atTime(java.time.LocalTime.MAX);

        when(venteRepository.sumRecettesEntreDeuxDates(startDateTime, endDateTime))
                .thenReturn(new BigDecimal("500000"));
        when(depenseRepository.sumDepensesEntreDeuxDatesEtCategorie(dateDebut, dateFin, null))
                .thenReturn(new BigDecimal("300000"));
        when(categorieDepenseRepository.findAll()).thenReturn(List.of());

        var rapport = beneficeService.calculerBeneficeParPeriodeEtCategorie(dateDebut, dateFin, null);

        assertEquals(new BigDecimal("500000"), rapport.getTotalRecettes());
        assertEquals(new BigDecimal("300000"), rapport.getTotalDepenses());
        assertEquals(new BigDecimal("200000"), rapport.getBenefice());
    }

    @Test
    void testCalculerBeneficeParPeriodeEtCategorie_DepensesSuperieuresRecettes() {
        LocalDateTime startDateTime = dateDebut.atStartOfDay();
        LocalDateTime endDateTime = dateFin.atTime(java.time.LocalTime.MAX);

        when(venteRepository.sumRecettesEntreDeuxDates(startDateTime, endDateTime))
                .thenReturn(new BigDecimal("200000"));
        when(depenseRepository.sumDepensesEntreDeuxDatesEtCategorie(dateDebut, dateFin, null))
                .thenReturn(new BigDecimal("350000"));
        when(categorieDepenseRepository.findAll()).thenReturn(List.of());

        var rapport = beneficeService.calculerBeneficeParPeriodeEtCategorie(dateDebut, dateFin, null);

        assertEquals(new BigDecimal("200000"), rapport.getTotalRecettes());
        assertEquals(new BigDecimal("350000"), rapport.getTotalDepenses());
        assertEquals(new BigDecimal("-150000"), rapport.getBenefice());
    }

    @Test
    void testCalculerBeneficeParPeriodeEtCategorie_AvecCategorie() {
        LocalDateTime startDateTime = dateDebut.atStartOfDay();
        LocalDateTime endDateTime = dateFin.atTime(java.time.LocalTime.MAX);
        Integer categorieId = 1;

        when(venteRepository.sumRecettesEntreDeuxDates(startDateTime, endDateTime))
                .thenReturn(new BigDecimal("500000"));
        when(depenseRepository.sumDepensesEntreDeuxDatesEtCategorie(dateDebut, dateFin, categorieId))
                .thenReturn(new BigDecimal("100000"));
        when(categorieDepenseRepository.findAll()).thenReturn(List.of());

        var rapport = beneficeService.calculerBeneficeParPeriodeEtCategorie(dateDebut, dateFin, categorieId);

        assertEquals(new BigDecimal("500000"), rapport.getTotalRecettes());
        assertEquals(new BigDecimal("100000"), rapport.getTotalDepenses());
        assertEquals(new BigDecimal("400000"), rapport.getBenefice());
        assertEquals(categorieId, rapport.getCategorieId());
    }

    @Test
    void testCalculerBeneficeParPeriodeEtCategorie_SansVentesNiDepenses() {
        LocalDateTime startDateTime = dateDebut.atStartOfDay();
        LocalDateTime endDateTime = dateFin.atTime(java.time.LocalTime.MAX);

        when(venteRepository.sumRecettesEntreDeuxDates(startDateTime, endDateTime))
                .thenReturn(null);
        when(depenseRepository.sumDepensesEntreDeuxDatesEtCategorie(dateDebut, dateFin, null))
                .thenReturn(null);
        when(categorieDepenseRepository.findAll()).thenReturn(List.of());

        var rapport = beneficeService.calculerBeneficeParPeriodeEtCategorie(dateDebut, dateFin, null);

        assertEquals(BigDecimal.ZERO, rapport.getTotalRecettes());
        assertEquals(BigDecimal.ZERO, rapport.getTotalDepenses());
        assertEquals(BigDecimal.ZERO, rapport.getBenefice());
    }

    @Test
    void testGetEvolutionBeneficeParMois() {
        Object[] recette1 = new Object[]{"2026-07", new BigDecimal("500000")};
        Object[] recette2 = new Object[]{"2026-08", new BigDecimal("600000")};
        List<Object[]> recettes = List.<Object[]>of(recette1, recette2);

        Object[] depense1 = new Object[]{"2026-07", new BigDecimal("300000")};
        Object[] depense2 = new Object[]{"2026-08", new BigDecimal("400000")};
        List<Object[]> depenses = List.<Object[]>of(depense1, depense2);

        when(venteRepository.getRecettesParMois()).thenReturn(recettes);
        when(depenseRepository.getDepensesParMois()).thenReturn(depenses);

        var evolution = beneficeService.getEvolutionBeneficeParMois();

        assertEquals(2, evolution.size());
        assertEquals(new BigDecimal("200000"), evolution.get("2026-07"));
        assertEquals(new BigDecimal("200000"), evolution.get("2026-08"));
    }

    @Test
    void testGetEvolutionBeneficeParMois_MoisAvecDepensesSeules() {
        Object[] recette1 = new Object[]{"2026-07", new BigDecimal("500000")};
        List<Object[]> recettes = List.<Object[]>of(recette1);

        Object[] depense1 = new Object[]{"2026-07", new BigDecimal("300000")};
        Object[] depense2 = new Object[]{"2026-08", new BigDecimal("100000")};
        List<Object[]> depenses = List.<Object[]>of(depense1, depense2);

        when(venteRepository.getRecettesParMois()).thenReturn(recettes);
        when(depenseRepository.getDepensesParMois()).thenReturn(depenses);

        var evolution = beneficeService.getEvolutionBeneficeParMois();

        assertEquals(2, evolution.size());
        assertEquals(new BigDecimal("200000"), evolution.get("2026-07"));
        assertEquals(new BigDecimal("-100000"), evolution.get("2026-08"));
    }

    @Test
    void testGetEvolutionBeneficeParMois_TriParMois() {
        Object[] recette1 = new Object[]{"2026-08", new BigDecimal("600000")};
        Object[] recette2 = new Object[]{"2026-07", new BigDecimal("500000")};
        List<Object[]> recettes = List.<Object[]>of(recette1, recette2);

        Object[] depense1 = new Object[]{"2026-08", new BigDecimal("400000")};
        Object[] depense2 = new Object[]{"2026-07", new BigDecimal("300000")};
        List<Object[]> depenses = List.<Object[]>of(depense1, depense2);

        when(venteRepository.getRecettesParMois()).thenReturn(recettes);
        when(depenseRepository.getDepensesParMois()).thenReturn(depenses);

        var evolution = beneficeService.getEvolutionBeneficeParMois();

        var keys = evolution.keySet().stream().toList();
        assertEquals("2026-07", keys.get(0));
        assertEquals("2026-08", keys.get(1));
    }
}
