package mg.vinaAkoho.vina_akoho.service.stockproduit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.TypeMouvement;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Unite;
import mg.vinaAkoho.vina_akoho.entity.produit.LotProduit;
import mg.vinaAkoho.vina_akoho.exception.stockmp.StockInsuffisantException;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.TypeMouvementRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.UniteRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.MouvementStockProduitRepository;

class SortieProduitServiceTest {

    private LotProduitRepository lotProduitRepository;
    private MouvementStockProduitRepository mouvementStockProduitRepository;
    private TypeMouvementRepository typeMouvementRepository;
    private EmployeRepository employeRepository;
    private UniteRepository uniteRepository;
    private SortieProduitService sortieProduitService;

    @BeforeEach
    void setUp() {
        lotProduitRepository = Mockito.mock(LotProduitRepository.class);
        mouvementStockProduitRepository = Mockito.mock(MouvementStockProduitRepository.class);
        typeMouvementRepository = Mockito.mock(TypeMouvementRepository.class);
        employeRepository = Mockito.mock(EmployeRepository.class);
        uniteRepository = Mockito.mock(UniteRepository.class);

        sortieProduitService = new SortieProduitService(
                lotProduitRepository,
                mouvementStockProduitRepository,
                typeMouvementRepository,
                employeRepository,
                uniteRepository
        );
    }

    @Test
    void allouerLots_shouldAllocateOldestLotsFirst_andCreateMovements() {
        Long produitId = 1L;
        BigDecimal quantiteDemande = new BigDecimal("35.00");

        LotProduit lot1 = new LotProduit();
        lot1.setId(1L);
        lot1.setQuantiteRestante(new BigDecimal("20.00"));
        lot1.setDateFabrication(LocalDate.of(2026, 1, 1));

        LotProduit lot2 = new LotProduit();
        lot2.setId(2L);
        lot2.setQuantiteRestante(new BigDecimal("30.00"));
        lot2.setDateFabrication(LocalDate.of(2026, 2, 1));

        LotProduit lot3 = new LotProduit();
        lot3.setId(3L);
        lot3.setQuantiteRestante(new BigDecimal("50.00"));
        lot3.setDateFabrication(LocalDate.of(2026, 3, 1));

        when(lotProduitRepository.sommeQuantiteRestante(produitId)).thenReturn(new BigDecimal("100.00"));
        when(lotProduitRepository.findByProduitIdAndQuantiteRestanteGreaterThanOrderByDateFabricationAsc(
                produitId, BigDecimal.ZERO)).thenReturn(List.of(lot1, lot2, lot3));
        when(employeRepository.findById(1)).thenReturn(Optional.of(new Employe()));
        when(typeMouvementRepository.findByLibelle("Sortie")).thenReturn(Optional.of(new TypeMouvement()));
        when(uniteRepository.findById(1)).thenReturn(Optional.of(new Unite()));

        List<SortieProduitService.Allocation> allocations = sortieProduitService.allouerLots(
                produitId,
                quantiteDemande,
                1,
                "VENTE-10"
        );

        assertEquals(2, allocations.size());
        assertEquals(new BigDecimal("20.00"), allocations.get(0).getQuantite());
        assertEquals(new BigDecimal("15.00"), allocations.get(1).getQuantite());
        assertEquals(new BigDecimal("0.00"), lot1.getQuantiteRestante());
        assertEquals(new BigDecimal("15.00"), lot2.getQuantiteRestante());
        assertEquals(new BigDecimal("50.00"), lot3.getQuantiteRestante());

        verify(lotProduitRepository, times(1)).save(lot1);
        verify(lotProduitRepository, times(1)).save(lot2);
        verify(lotProduitRepository, never()).save(lot3);

        verify(mouvementStockProduitRepository, times(2)).save(any());

        ArgumentCaptor<SortieProduitService.Allocation> captor = ArgumentCaptor.forClass(SortieProduitService.Allocation.class);
        assertEquals(2, allocations.size());
    }

    @Test
    void allouerLots_shouldThrowWhenStockInsufficient() {
        Long produitId = 2L;
        BigDecimal quantiteDemande = new BigDecimal("150.00");

        when(lotProduitRepository.sommeQuantiteRestante(produitId)).thenReturn(new BigDecimal("100.00"));

        StockInsuffisantException exception = assertThrows(
                StockInsuffisantException.class,
                () -> sortieProduitService.allouerLots(produitId, quantiteDemande, 1, "VENTE-11")
        );

        assertTrue(exception.getMessage().contains("Stock insuffisant"));
        verify(lotProduitRepository, never()).findByProduitIdAndQuantiteRestanteGreaterThanOrderByDateFabricationAsc(any(), any());
    }

    @Test
    void allouerLots_shouldCreateDefaultUnitWhenNoneExist() {
        Long produitId = 3L;
        BigDecimal quantiteDemande = new BigDecimal("10.00");

        LotProduit lot = new LotProduit();
        lot.setId(1L);
        lot.setQuantiteRestante(new BigDecimal("20.00"));
        lot.setDateFabrication(LocalDate.of(2026, 4, 1));

        when(lotProduitRepository.sommeQuantiteRestante(produitId)).thenReturn(new BigDecimal("20.00"));
        when(lotProduitRepository.findByProduitIdAndQuantiteRestanteGreaterThanOrderByDateFabricationAsc(
                produitId, BigDecimal.ZERO)).thenReturn(List.of(lot));
        when(employeRepository.findById(1)).thenReturn(Optional.of(new Employe()));
        when(typeMouvementRepository.findByLibelle("Sortie")).thenReturn(Optional.of(new TypeMouvement()));
        when(uniteRepository.findById(1)).thenReturn(Optional.empty());
        when(uniteRepository.findAll()).thenReturn(List.of());
        when(uniteRepository.save(any(Unite.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<SortieProduitService.Allocation> allocations = sortieProduitService.allouerLots(
                produitId,
                quantiteDemande,
                1,
                "VENTE-12"
        );

        assertEquals(1, allocations.size());
        assertEquals(new BigDecimal("10.00"), allocations.get(0).getQuantite());
        verify(uniteRepository).save(any(Unite.class));
        verify(mouvementStockProduitRepository, times(1)).save(any());
    }
}
