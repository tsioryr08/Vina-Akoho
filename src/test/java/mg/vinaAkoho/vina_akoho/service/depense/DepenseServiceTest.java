package mg.vinaAkoho.vina_akoho.service.depense;

import mg.vinaAkoho.vina_akoho.dto.depense.DepenseDTO;
import mg.vinaAkoho.vina_akoho.dto.depense.DepenseRequestDTO;
import mg.vinaAkoho.vina_akoho.entity.depense.CategorieDepense;
import mg.vinaAkoho.vina_akoho.entity.depense.Depense;
import mg.vinaAkoho.vina_akoho.entity.depense.DepenseLot;
import mg.vinaAkoho.vina_akoho.entity.depense.Phase;
import mg.vinaAkoho.vina_akoho.entity.depense.StatutDepense;
import mg.vinaAkoho.vina_akoho.entity.produit.LotProduit;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.exception.depense.DepenseNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.depense.DepenseLotRepository;
import mg.vinaAkoho.vina_akoho.repository.depense.DepenseRepository;
import mg.vinaAkoho.vina_akoho.service.produit.PrixVenteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepenseServiceTest {

    @Mock
    private DepenseRepository depenseRepository;

    @Mock
    private CategorieDepenseService categorieDepenseService;

    @Mock
    private PhaseService phaseService;

    @Mock
    private StatutDepenseService statutDepenseService;

    @Mock
    private PrixVenteService prixVenteService;

    @Mock
    private DepenseLotRepository depenseLotRepository;

    @InjectMocks
    private DepenseService depenseService;

    private DepenseRequestDTO requete;
    private Depense depense;
    private CategorieDepense categorieDepense;
    private Phase phase;
    private StatutDepense statutDepense;

    @BeforeEach
    void setUp() {
        requete = new DepenseRequestDTO();
        requete.setDesignation("Électricité");
        requete.setMontant(new BigDecimal("50000"));
        requete.setDate(LocalDate.now());
        requete.setIdCategorieDepense(1);
        requete.setIdPhase(1);
        requete.setIdStatutDepense(1);

        categorieDepense = new CategorieDepense();
        categorieDepense.setId(1);
        categorieDepense.setLibelle("Énergie");

        phase = new Phase();
        phase.setId(1);
        phase.setLibelle("Fabrication");

        statutDepense = new StatutDepense();
        statutDepense.setId(1);
        statutDepense.setLibelle("Payée");

        depense = new Depense();
        depense.setId(1);
        depense.setDesignation("Électricité");
        depense.setMontant(new BigDecimal("50000"));
        depense.setDate(LocalDate.now());
        depense.setCategorieDepense(categorieDepense);
        depense.setPhase(phase);
        depense.setStatutDepense(statutDepense);
    }

    @Test
    void testCreer_DepenseEtRecalculePrixVente() {
        when(categorieDepenseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.CategorieDepenseDTO(1, "Énergie", null));
        when(phaseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.PhaseDTO(1, "Fabrication", null, null, null));
        when(statutDepenseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.StatutDepenseDTO(1, "Payée", null));
        when(depenseRepository.save(any(Depense.class))).thenReturn(depense);
        when(depenseLotRepository.findByDepenseId(1)).thenReturn(List.of());

        DepenseDTO resultat = depenseService.creer(requete);

        assertNotNull(resultat);
        assertEquals(1, resultat.getId());
        verify(depenseRepository, times(1)).save(any(Depense.class));
        verify(prixVenteService, times(0)).calculerPrixVente(anyLong());
    }

    @Test
    void testCreer_AvecDepenseLot_RecalculePrixVente() {
        Produit produit = new Produit();
        produit.setId(1L);
        produit.setNom("Poulet entier");

        LotProduit lotProduit = new LotProduit();
        lotProduit.setId(1L);
        lotProduit.setProduit(produit);

        DepenseLot depenseLot = new DepenseLot();
        depenseLot.setId(1);
        depenseLot.setDepense(depense);
        depenseLot.setLotProduit(lotProduit);

        when(categorieDepenseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.CategorieDepenseDTO(1, "Énergie", null));
        when(phaseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.PhaseDTO(1, "Fabrication", null, null, null));
        when(statutDepenseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.StatutDepenseDTO(1, "Payée", null));
        when(depenseRepository.save(any(Depense.class))).thenReturn(depense);
        when(depenseLotRepository.findByDepenseId(1)).thenReturn(List.of(depenseLot));

        DepenseDTO resultat = depenseService.creer(requete);

        assertNotNull(resultat);
        verify(prixVenteService, times(1)).calculerPrixVente(1L);
    }

    @Test
    void testModifier_DepenseEtRecalculePrixVente() {
        when(depenseRepository.findById(1)).thenReturn(Optional.of(depense));
        when(categorieDepenseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.CategorieDepenseDTO(1, "Énergie", null));
        when(phaseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.PhaseDTO(1, "Fabrication", null, null, null));
        when(statutDepenseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.StatutDepenseDTO(1, "Payée", null));
        when(depenseRepository.save(any(Depense.class))).thenReturn(depense);
        when(depenseLotRepository.findByDepenseId(1)).thenReturn(List.of());

        DepenseDTO resultat = depenseService.modifier(1, requete);

        assertNotNull(resultat);
        verify(depenseRepository, times(1)).save(any(Depense.class));
        verify(prixVenteService, times(0)).calculerPrixVente(anyLong());
    }

    @Test
    void testModifier_AvecDepenseLot_RecalculePrixVente() {
        Produit produit = new Produit();
        produit.setId(1L);
        produit.setNom("Poulet entier");

        LotProduit lotProduit = new LotProduit();
        lotProduit.setId(1L);
        lotProduit.setProduit(produit);

        DepenseLot depenseLot = new DepenseLot();
        depenseLot.setId(1);
        depenseLot.setDepense(depense);
        depenseLot.setLotProduit(lotProduit);

        when(depenseRepository.findById(1)).thenReturn(Optional.of(depense));
        when(categorieDepenseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.CategorieDepenseDTO(1, "Énergie", null));
        when(phaseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.PhaseDTO(1, "Fabrication", null, null, null));
        when(statutDepenseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.StatutDepenseDTO(1, "Payée", null));
        when(depenseRepository.save(any(Depense.class))).thenReturn(depense);
        when(depenseLotRepository.findByDepenseId(1)).thenReturn(List.of(depenseLot));

        DepenseDTO resultat = depenseService.modifier(1, requete);

        assertNotNull(resultat);
        verify(prixVenteService, times(1)).calculerPrixVente(1L);
    }

    @Test
    void testSupprimer_RecalculePrixVenteAvantSuppression() {
        Produit produit = new Produit();
        produit.setId(1L);
        produit.setNom("Poulet entier");

        LotProduit lotProduit = new LotProduit();
        lotProduit.setId(1L);
        lotProduit.setProduit(produit);

        DepenseLot depenseLot = new DepenseLot();
        depenseLot.setId(1);
        depenseLot.setDepense(depense);
        depenseLot.setLotProduit(lotProduit);

        when(depenseRepository.findById(1)).thenReturn(Optional.of(depense));
        when(depenseLotRepository.findByDepenseId(1)).thenReturn(List.of(depenseLot));
        doNothing().when(depenseRepository).delete(depense);

        depenseService.supprimer(1);

        verify(depenseRepository, times(1)).delete(depense);
        verify(prixVenteService, times(1)).calculerPrixVente(1L);
    }

    @Test
    void testRecalculPrixVente_SansDepenseLot_NeRecalculePas() {
        when(depenseRepository.findById(1)).thenReturn(Optional.of(depense));
        when(depenseLotRepository.findByDepenseId(1)).thenReturn(List.of());
        doNothing().when(depenseRepository).delete(depense);

        depenseService.supprimer(1);

        verify(depenseRepository, times(1)).delete(depense);
        verify(prixVenteService, times(0)).calculerPrixVente(anyLong());
    }

    @Test
    void testRecalculPrixVente_AvecPlusieursProduits_RecalculeTous() {
        Produit produit1 = new Produit();
        produit1.setId(1L);
        produit1.setNom("Poulet entier");

        Produit produit2 = new Produit();
        produit2.setId(2L);
        produit2.setNom("Aliment croissance");

        LotProduit lotProduit1 = new LotProduit();
        lotProduit1.setId(1L);
        lotProduit1.setProduit(produit1);

        LotProduit lotProduit2 = new LotProduit();
        lotProduit2.setId(2L);
        lotProduit2.setProduit(produit2);

        DepenseLot depenseLot1 = new DepenseLot();
        depenseLot1.setId(1);
        depenseLot1.setDepense(depense);
        depenseLot1.setLotProduit(lotProduit1);

        DepenseLot depenseLot2 = new DepenseLot();
        depenseLot2.setId(2);
        depenseLot2.setDepense(depense);
        depenseLot2.setLotProduit(lotProduit2);

        when(categorieDepenseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.CategorieDepenseDTO(1, "Énergie", null));
        when(phaseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.PhaseDTO(1, "Fabrication", null, null, null));
        when(statutDepenseService.trouverParId(1)).thenReturn(
                new mg.vinaAkoho.vina_akoho.dto.depense.StatutDepenseDTO(1, "Payée", null));
        when(depenseRepository.save(any(Depense.class))).thenReturn(depense);
        when(depenseLotRepository.findByDepenseId(1)).thenReturn(List.of(depenseLot1, depenseLot2));

        depenseService.creer(requete);

        verify(prixVenteService, times(1)).calculerPrixVente(1L);
        verify(prixVenteService, times(1)).calculerPrixVente(2L);
    }

    @Test
    void testFormulePrixVente_CoutPlusMarge() {
        BigDecimal coutFabrication = new BigDecimal("10000");
        BigDecimal margePourcentage = new BigDecimal("25");
        BigDecimal prixAttendu = new BigDecimal("12500.00");

        BigDecimal coefficientMarge = BigDecimal.ONE.add(
                margePourcentage.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)
        );
        BigDecimal prixCalcule = coutFabrication.multiply(coefficientMarge).setScale(2, BigDecimal.ROUND_HALF_UP);

        assertEquals(prixAttendu, prixCalcule);
    }

    @Test
    void testFormulePrixVente_MargeNulle() {
        BigDecimal coutFabrication = new BigDecimal("10000");
        BigDecimal margePourcentage = BigDecimal.ZERO;
        BigDecimal prixAttendu = new BigDecimal("10000.00");

        BigDecimal coefficientMarge = BigDecimal.ONE.add(
                margePourcentage.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)
        );
        BigDecimal prixCalcule = coutFabrication.multiply(coefficientMarge).setScale(2, BigDecimal.ROUND_HALF_UP);

        assertEquals(prixAttendu, prixCalcule);
    }

    @Test
    void testFormulePrixVente_CoutNul() {
        BigDecimal coutFabrication = BigDecimal.ZERO;
        BigDecimal margePourcentage = new BigDecimal("25");
        BigDecimal prixAttendu = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal coefficientMarge = BigDecimal.ONE.add(
                margePourcentage.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP)
        );
        BigDecimal prixCalcule = coutFabrication.multiply(coefficientMarge).setScale(2, BigDecimal.ROUND_HALF_UP);

        assertEquals(prixAttendu, prixCalcule);
    }
}
