package mg.vinaAkoho.vina_akoho.service.produit;

import mg.vinaAkoho.vina_akoho.entity.depense.DepenseLot;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MatierePremiere;
import mg.vinaAkoho.vina_akoho.entity.produit.Categorie;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.entity.recetteproduit.RecetteProduit;
import mg.vinaAkoho.vina_akoho.repository.depense.DepenseLotRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.MatierePremiereRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.CategorieRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.recetteproduit.RecetteProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrixVenteServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private RecetteProduitRepository recetteProduitRepository;

    @Mock
    private MatierePremiereRepository matierePremiereRepository;

    @Mock
    private LotProduitRepository lotProduitRepository;

    @Mock
    private DepenseLotRepository depenseLotRepository;

    @InjectMocks
    private PrixVenteService prixVenteService;

    private Produit produit;
    private Categorie categorie;
    private MatierePremiere matierePremiere;
    private RecetteProduit recetteProduit;

    @BeforeEach
    void setUp() {
        produit = new Produit();
        produit.setId(1L);
        produit.setNom("Poulet entier");

        categorie = new Categorie();
        categorie.setId(1L);
        categorie.setLibelle("Volaille");
        categorie.setMargePourcentage(new BigDecimal("25"));

        produit.setCategorie(categorie);

        matierePremiere = new MatierePremiere();
        matierePremiere.setId(1);
        matierePremiere.setNom("Maïs");
        matierePremiere.setCoutUnitaire(new BigDecimal("500"));

        recetteProduit = new RecetteProduit();
        recetteProduit.setIdCategorie(1);
        recetteProduit.setMatierePremiere(matierePremiere);
        recetteProduit.setQuantiteMp(new BigDecimal("10"));
        recetteProduit.setIsActive(true);
    }

    @Test
    void testCalculerPrixVente_CoutTotalAvecMarge() {
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie));
        when(recetteProduitRepository.findByIdCategorieAndIsActiveTrue(1)).thenReturn(List.of(recetteProduit));
        when(matierePremiereRepository.findById(1)).thenReturn(Optional.of(matierePremiere));
        when(depenseLotRepository.sumDepensesByProduitId(1L)).thenReturn(new BigDecimal("1000"));

        BigDecimal prixVente = prixVenteService.calculerPrixVente(1L);

        BigDecimal coutMp = new BigDecimal("500").multiply(new BigDecimal("10"));
        BigDecimal coutTotal = coutMp.add(new BigDecimal("1000"));
        BigDecimal expected = coutTotal.multiply(new BigDecimal("1.25")).setScale(2, BigDecimal.ROUND_HALF_UP);

        assertEquals(expected, prixVente);
    }

    @Test
    void testCalculerPrixVente_MargeNulle() {
        categorie.setMargePourcentage(BigDecimal.ZERO);

        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie));
        when(recetteProduitRepository.findByIdCategorieAndIsActiveTrue(1)).thenReturn(List.of(recetteProduit));
        when(matierePremiereRepository.findById(1)).thenReturn(Optional.of(matierePremiere));
        when(depenseLotRepository.sumDepensesByProduitId(1L)).thenReturn(BigDecimal.ZERO);

        BigDecimal prixVente = prixVenteService.calculerPrixVente(1L);

        BigDecimal coutMp = new BigDecimal("500").multiply(new BigDecimal("10"));
        BigDecimal expected = coutMp.setScale(2, BigDecimal.ROUND_HALF_UP);
        assertEquals(expected, prixVente);
    }

    @Test
    void testCalculerPrixVente_CoutNul() {
        matierePremiere.setCoutUnitaire(BigDecimal.ZERO);
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie));
        when(recetteProduitRepository.findByIdCategorieAndIsActiveTrue(1)).thenReturn(List.of(recetteProduit));
        when(matierePremiereRepository.findById(1)).thenReturn(Optional.of(matierePremiere));
        when(depenseLotRepository.sumDepensesByProduitId(1L)).thenReturn(BigDecimal.ZERO);

        BigDecimal prixVente = prixVenteService.calculerPrixVente(1L);

        assertEquals(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP), prixVente);
    }

    @Test
    void testCalculerPrixVente_ProduitIntrouvable() {
        when(produitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            prixVenteService.calculerPrixVente(999L);
        });
    }

    @Test
    void testCalculerCoutMatierePremiere() {
        when(recetteProduitRepository.findByIdCategorieAndIsActiveTrue(1)).thenReturn(List.of(recetteProduit));
        when(matierePremiereRepository.findById(1)).thenReturn(Optional.of(matierePremiere));

        BigDecimal cout = prixVenteService.calculerCoutMatierePremiere(1L);

        BigDecimal expected = new BigDecimal("500").multiply(new BigDecimal("10"));
        assertEquals(expected, cout);
    }

    @Test
    void testCalculerCoutDepensesFabrication() {
        when(depenseLotRepository.sumDepensesByProduitId(1L)).thenReturn(new BigDecimal("2000"));

        BigDecimal cout = prixVenteService.calculerCoutDepensesFabrication(1L);

        assertEquals(new BigDecimal("2000"), cout);
    }

    @Test
    void testCalculerCoutDepensesFabrication_Nulle() {
        when(depenseLotRepository.sumDepensesByProduitId(1L)).thenReturn(null);

        BigDecimal cout = prixVenteService.calculerCoutDepensesFabrication(1L);

        assertEquals(BigDecimal.ZERO, cout);
    }
}
