package mg.vinaAkoho.vina_akoho.service.ventes;

import mg.vinaAkoho.vina_akoho.dto.ventes.PanierItemDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteFormDTO;
import mg.vinaAkoho.vina_akoho.entity.clients.Client;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.entity.ventes.Facture;
import mg.vinaAkoho.vina_akoho.entity.ventes.LigneVente;
import mg.vinaAkoho.vina_akoho.entity.ventes.ModePaiement;
import mg.vinaAkoho.vina_akoho.entity.ventes.StatutVente;
import mg.vinaAkoho.vina_akoho.entity.ventes.Vente;
import mg.vinaAkoho.vina_akoho.repository.clients.ClientRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.FactureRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.LigneVenteRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.ModePaiementRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.StatutVenteRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.VenteRepository;
import mg.vinaAkoho.vina_akoho.service.stockproduit.SortieProduitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenteServiceTest {

    @Mock
    private VenteRepository venteRepository;

    @Mock
    private LigneVenteRepository ligneVenteRepository;

    @Mock
    private FactureRepository factureRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ModePaiementRepository modePaiementRepository;

    @Mock
    private StatutVenteRepository statutVenteRepository;

    @Mock
    private SortieProduitService sortieProduitService;

    @InjectMocks
    private VenteService venteService;

    private Client client;
    private Produit produit;
    private ModePaiement modePaiement;
    private StatutVente statutVente;
    private Vente vente;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setNom("Client");
        client.setPrenom("Test");
        client.setNumeroTelephone("0341234567");
        client.setAdresse("Antananarivo");
        client.setEstSupprimer(false);

        produit = new Produit();
        produit.setId(1L);
        produit.setNom("Poulet entier");
        produit.setPrixVente(BigDecimal.valueOf(45000));

        modePaiement = new ModePaiement();
        modePaiement.setId(1L);
        modePaiement.setLibelle("Espèces");

        statutVente = new StatutVente();
        statutVente.setId(1L);
        statutVente.setLibelle("En attente de paiement");

        vente = new Vente();
        vente.setId(1L);
        vente.setClient(client);
        vente.setModePaiement(modePaiement);
        vente.setStatutVente(statutVente);
        vente.setMontantTotal(BigDecimal.valueOf(45000));
    }

    @Test
    void testCreer_PanierVide_ThrowsException() {
        VenteFormDTO requete = new VenteFormDTO();
        requete.setIdClient(1L);
        requete.setIdModePaiement(1L);

        assertThrows(IllegalArgumentException.class, () -> {
            venteService.creer(requete, List.of(), 1);
        });
    }

    @Test
    void testCreer_ClientIntrouvable_ThrowsException() {
        VenteFormDTO requete = new VenteFormDTO();
        requete.setIdClient(999L);
        requete.setIdModePaiement(1L);

        PanierItemDTO item = PanierItemDTO.builder()
                .idProduit(1L)
                .quantite(BigDecimal.ONE)
                .prixUnitaire(BigDecimal.valueOf(45000))
                .montant(BigDecimal.valueOf(45000))
                .build();

        when(clientRepository.findByIdAndEstSupprimerFalse(999L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            venteService.creer(requete, List.of(item), 1);
        });
    }

    @Test
    void testCreer_VenteReussi_AvecFIFO() {
        VenteFormDTO requete = new VenteFormDTO();
        requete.setIdClient(1L);
        requete.setIdModePaiement(1L);

        PanierItemDTO item = PanierItemDTO.builder()
                .idProduit(1L)
                .quantite(BigDecimal.ONE)
                .prixUnitaire(BigDecimal.valueOf(45000))
                .montant(BigDecimal.valueOf(45000))
                .build();

        when(clientRepository.findByIdAndEstSupprimerFalse(1L)).thenReturn(Optional.of(client));
        when(modePaiementRepository.findById(1L)).thenReturn(Optional.of(modePaiement));
        when(statutVenteRepository.findByLibelleIgnoreCase("En attente de paiement"))
                .thenReturn(Optional.of(statutVente));
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(venteRepository.save(any(Vente.class))).thenReturn(vente);
        when(ligneVenteRepository.save(any(LigneVente.class))).thenReturn(new LigneVente());
        when(factureRepository.save(any(Facture.class))).thenReturn(new Facture());
        when(sortieProduitService.allouerLots(any(), any(), any(), anyString()))
                .thenReturn(new SortieProduitService.Allocation(null, BigDecimal.ONE));

        VenteDTO result = venteService.creer(requete, List.of(item), 1);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(sortieProduitService, times(1)).allouerLots(any(), any(), any(), anyString());
    }

    @Test
    void testListerToutes_RetourneListeDTO() {
        when(venteRepository.findAllByOrderByDateVenteDesc()).thenReturn(Arrays.asList(vente));

        List<VenteDTO> result = venteService.listerToutes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testTrouverParId_RetourneDTO() {
        when(venteRepository.findById(1L)).thenReturn(Optional.of(vente));

        VenteDTO result = venteService.trouverParId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testTrouverParId_VenteIntrouvable_ThrowsException() {
        when(venteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            venteService.trouverParId(999L);
        });
    }

    @Test
    void testValiderPaiement_ChangeStatut() {
        when(venteRepository.findById(1L)).thenReturn(Optional.of(vente));
        when(statutVenteRepository.findByLibelleIgnoreCase("Validée"))
                .thenReturn(Optional.of(statutVente));
        when(venteRepository.save(any(Vente.class))).thenReturn(vente);

        VenteDTO result = venteService.validerPaiement(1L);

        assertNotNull(result);
        verify(venteRepository, times(1)).save(any(Vente.class));
    }
}
