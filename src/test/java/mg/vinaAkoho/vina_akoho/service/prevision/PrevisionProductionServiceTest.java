package mg.vinaAkoho.vina_akoho.service.prevision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mg.vinaAkoho.vina_akoho.dto.prevision.PrevisionMatierePremiereDTO;
import mg.vinaAkoho.vina_akoho.dto.prevision.PrevisionProductionDTO;
import mg.vinaAkoho.vina_akoho.dto.prevision.VenteProduitProjection;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MatierePremiere;
import mg.vinaAkoho.vina_akoho.entity.produit.Categorie;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.entity.recetteproduit.RecetteProduit;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.LotMpRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.recetteproduit.RecetteProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.LigneVenteRepository;

class PrevisionProductionServiceTest {

    private ProduitRepository produitRepository;
    private LigneVenteRepository ligneVenteRepository;
    private LotProduitRepository lotProduitRepository;
    private RecetteProduitRepository recetteProduitRepository;
    private LotMpRepository lotMpRepository;
    private PrevisionProductionService service;

    @BeforeEach
    void setUp() {
        produitRepository = mock(ProduitRepository.class);
        ligneVenteRepository = mock(LigneVenteRepository.class);
        lotProduitRepository = mock(LotProduitRepository.class);
        recetteProduitRepository = mock(RecetteProduitRepository.class);
        lotMpRepository = mock(LotMpRepository.class);
        service = new PrevisionProductionService(
                produitRepository,
                ligneVenteRepository,
                lotProduitRepository,
                recetteProduitRepository,
                lotMpRepository);
    }

    @Test
    void calculeLaProductionDepuisLaMoyenneLaCouvertureEtLeStockDisponible() {
        Categorie categorie = new Categorie();
        categorie.setId(1L);
        categorie.setLibelle("Poussin");

        Produit produit = new Produit();
        produit.setId(10L);
        produit.setNom("Aliment poussin");
        produit.setCategorie(categorie);

        VenteProduitProjection vente = mock(VenteProduitProjection.class);
        when(vente.getProduitId()).thenReturn(10L);
        when(vente.getQuantiteVendue()).thenReturn(new BigDecimal("60"));
        when(ligneVenteRepository.sommerQuantitesVenduesParProduit(any(), any()))
                .thenReturn(List.of(vente));
        when(produitRepository.findAllActifs()).thenReturn(List.of(produit));
        when(lotProduitRepository.sommeQuantiteRestante(10L)).thenReturn(new BigDecimal("4"));
        when(recetteProduitRepository.findByIdCategorieAndIsActiveTrue(1))
                .thenReturn(List.of(new RecetteProduit()));

        PrevisionProductionDTO resultat = service.calculerProductions(30, 7).get(0);

        assertEquals(new BigDecimal("2.00"), resultat.moyenneJournaliere());
        assertEquals(new BigDecimal("4.00"), resultat.stockActuel());
        assertEquals(new BigDecimal("14.00"), resultat.objectifStock());
        assertEquals(new BigDecimal("10.00"), resultat.propositionProduction());
        assertEquals("À PRODUIRE", resultat.statut());
    }

    @Test
    void neProposeRienQuandLeStockCouvreDejaLeBesoin() {
        Categorie categorie = new Categorie();
        categorie.setId(1L);
        categorie.setLibelle("Poussin");

        Produit produit = new Produit();
        produit.setId(10L);
        produit.setNom("Aliment poussin");
        produit.setCategorie(categorie);

        VenteProduitProjection vente = mock(VenteProduitProjection.class);
        when(vente.getProduitId()).thenReturn(10L);
        when(vente.getQuantiteVendue()).thenReturn(new BigDecimal("60"));
        when(ligneVenteRepository.sommerQuantitesVenduesParProduit(any(), any()))
                .thenReturn(List.of(vente));
        when(produitRepository.findAllActifs()).thenReturn(List.of(produit));
        when(lotProduitRepository.sommeQuantiteRestante(10L)).thenReturn(new BigDecimal("20"));
        when(recetteProduitRepository.findByIdCategorieAndIsActiveTrue(1))
                .thenReturn(List.of(new RecetteProduit()));

        PrevisionProductionDTO resultat = service.calculerProductions(30, 7).get(0);

        assertEquals(new BigDecimal("14.00"), resultat.objectifStock());
        assertEquals(new BigDecimal("0.00"), resultat.propositionProduction());
        assertEquals("STOCK SUFFISANT", resultat.statut());
    }

    @Test
    void calculeLesMatieresDepuisLaRecetteEtDeduitLeStockExistant() {
        Categorie categorie = new Categorie();
        categorie.setId(1L);
        Produit produit = new Produit();
        produit.setId(10L);
        produit.setCategorie(categorie);

        MatierePremiere matiere = new MatierePremiere();
        matiere.setId(20);
        matiere.setCode("MP-20");
        matiere.setNom("Maïs");
        matiere.setSeuilMinimum(new BigDecimal("5"));

        RecetteProduit ligneRecette = new RecetteProduit();
        ligneRecette.setMatierePremiere(matiere);
        ligneRecette.setQuantiteMp(new BigDecimal("1.5"));

        when(produitRepository.findById(10L)).thenReturn(java.util.Optional.of(produit));
        when(recetteProduitRepository.findByIdCategorieAndIsActiveTrue(1))
                .thenReturn(List.of(ligneRecette));
        when(lotMpRepository.sommeQuantiteRestante(20)).thenReturn(new BigDecimal("8"));

        PrevisionProductionDTO production = new PrevisionProductionDTO(
                10L, "Aliment poussin", "Poussin", "sac",
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("10"), "À PRODUIRE", true);

        PrevisionMatierePremiereDTO resultat =
                service.calculerMatieresPremieres(List.of(production)).get(0);

        assertEquals(new BigDecimal("15.00"), resultat.besoinEstime());
        assertEquals(new BigDecimal("8.00"), resultat.stockActuel());
        assertEquals(new BigDecimal("5.00"), resultat.stockSecurite());
        assertEquals(new BigDecimal("12.00"), resultat.quantiteACommander());
        assertEquals("À COMMANDER", resultat.statut());
    }
}
