package mg.vinaAkoho.vina_akoho.service.produit;

import mg.vinaAkoho.vina_akoho.dto.produit.HistoriquePrixProduitDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.ProduitRequestDTO;
import mg.vinaAkoho.vina_akoho.entity.produit.Categorie;
import mg.vinaAkoho.vina_akoho.entity.produit.HistoriquePrixProduit;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.repository.produit.CategorieRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.HistoriquePrixProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceHistoriquePrixTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private LotProduitRepository lotProduitRepository;

    @Mock
    private HistoriquePrixProduitRepository historiquePrixProduitRepository;

    @Mock
    private PrixVenteService prixVenteService;

    @InjectMocks
    private ProduitService produitService;

    private Produit produit;
    private Categorie categorie;
    private HistoriquePrixProduit historiquePrix;

    @BeforeEach
    void setUp() {
        categorie = new Categorie();
        categorie.setId(1L);
        categorie.setLibelle("Volaille");
        categorie.setMargePourcentage(BigDecimal.valueOf(25));

        produit = new Produit();
        produit.setId(1L);
        produit.setRef("PROD001");
        produit.setNom("Poulet entier");
        produit.setCategorie(categorie);
        produit.setPrixVente(BigDecimal.valueOf(45000));
        produit.setActif(true);

        historiquePrix = new HistoriquePrixProduit();
        historiquePrix.setId(1L);
        historiquePrix.setProduit(produit);
        historiquePrix.setAncienPrix(BigDecimal.valueOf(40000));
        historiquePrix.setNouveauPrix(BigDecimal.valueOf(45000));
        historiquePrix.setDateModification(LocalDateTime.now());
    }

    @Test
    void testModifier_CreeHistoriquePrix_QuandPrixChange() {
        // Arrange
        Long produitId = 1L;
        BigDecimal ancienPrix = BigDecimal.valueOf(40000);
        BigDecimal nouveauPrix = BigDecimal.valueOf(45000);
        
        produit.setPrixVente(ancienPrix);
        
        ProduitRequestDTO requestDTO = ProduitRequestDTO.builder()
                .ref("PROD001")
                .idCategorie(1L)
                .nom("Poulet entier")
                .prixVente(nouveauPrix)
                .seuilAlerte(50)
                .description("Description")
                .actif(true)
                .build();

        when(produitRepository.findById(produitId)).thenReturn(Optional.of(produit));
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie));
        when(produitRepository.existsByRefIgnoreCaseAndIdNotAndActifTrue(any(), any())).thenReturn(false);
        when(produitRepository.existsByNomIgnoreCaseAndIdNotAndActifTrue(any(), any())).thenReturn(false);
        when(produitRepository.save(any())).thenReturn(produit);
        when(historiquePrixProduitRepository.save(any())).thenReturn(historiquePrix);
        when(prixVenteService.calculerPrixVente(produitId)).thenReturn(nouveauPrix);
        when(lotProduitRepository.sommeQuantiteRestante(produitId)).thenReturn(BigDecimal.valueOf(100));

        // Act
        produitService.modifier(produitId, requestDTO);

        // Assert
        verify(historiquePrixProduitRepository, times(1)).save(any(HistoriquePrixProduit.class));
        assertEquals(nouveauPrix, produit.getPrixVente());
    }

    @Test
    void testModifier_NeCreePasHistoriquePrix_QuandPrixNeChangePas() {
        // Arrange
        Long produitId = 1L;
        BigDecimal prixConstant = BigDecimal.valueOf(45000);
        
        produit.setPrixVente(prixConstant);
        
        ProduitRequestDTO requestDTO = ProduitRequestDTO.builder()
                .ref("PROD001")
                .idCategorie(1L)
                .nom("Poulet entier")
                .prixVente(prixConstant)
                .seuilAlerte(50)
                .description("Description")
                .actif(true)
                .build();

        when(produitRepository.findById(produitId)).thenReturn(Optional.of(produit));
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie));
        when(produitRepository.existsByRefIgnoreCaseAndIdNotAndActifTrue(any(), any())).thenReturn(false);
        when(produitRepository.existsByNomIgnoreCaseAndIdNotAndActifTrue(any(), any())).thenReturn(false);
        when(produitRepository.save(any())).thenReturn(produit);
        when(prixVenteService.calculerPrixVente(produitId)).thenReturn(prixConstant);
        when(lotProduitRepository.sommeQuantiteRestante(produitId)).thenReturn(prixConstant);

        // Act
        produitService.modifier(produitId, requestDTO);

        // Assert
        verify(historiquePrixProduitRepository, never()).save(any());
    }

    @Test
    void testListerHistoriquePrix_RetourneListeDTO() {
        // Arrange
        Long produitId = 1L;
        List<HistoriquePrixProduit> historiques = Arrays.asList(historiquePrix);
        
        when(historiquePrixProduitRepository.findByProduitIdOrderByDateModificationDesc(produitId))
                .thenReturn(historiques);

        // Act
        List<HistoriquePrixProduitDTO> result = produitService.listerHistoriquePrix(produitId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(historiquePrix.getId(), result.get(0).getId());
        assertEquals(historiquePrix.getAncienPrix(), result.get(0).getAncienPrix());
        assertEquals(historiquePrix.getNouveauPrix(), result.get(0).getNouveauPrix());
        assertEquals(historiquePrix.getProduit().getNom(), result.get(0).getNomProduit());
    }

    @Test
    void testListerHistoriquePrix_RetourneListeVide_QuandPasHistorique() {
        // Arrange
        Long produitId = 1L;
        
        when(historiquePrixProduitRepository.findByProduitIdOrderByDateModificationDesc(produitId))
                .thenReturn(List.of());

        // Act
        List<HistoriquePrixProduitDTO> result = produitService.listerHistoriquePrix(produitId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
