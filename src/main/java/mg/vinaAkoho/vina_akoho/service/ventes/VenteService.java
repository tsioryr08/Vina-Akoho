package mg.vinaAkoho.vina_akoho.service.ventes;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.ventes.FactureDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.LigneVenteDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.PanierItemDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteFormDTO;
import mg.vinaAkoho.vina_akoho.entity.clients.Client;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.entity.ventes.Commande;
import mg.vinaAkoho.vina_akoho.entity.ventes.Facture;
import mg.vinaAkoho.vina_akoho.entity.ventes.LigneCommande;
import mg.vinaAkoho.vina_akoho.entity.ventes.LigneVente;
import mg.vinaAkoho.vina_akoho.entity.ventes.LigneVenteLot;
import mg.vinaAkoho.vina_akoho.entity.ventes.ModePaiement;
import mg.vinaAkoho.vina_akoho.entity.ventes.StatutCommande;
import mg.vinaAkoho.vina_akoho.entity.ventes.StatutVente;
import mg.vinaAkoho.vina_akoho.entity.ventes.Vente;
import mg.vinaAkoho.vina_akoho.exception.clients.ClientNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.produit.ProduitNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.ventes.VenteNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.clients.ClientRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.CommandeRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.FactureRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.LigneCommandeRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.LigneVenteLotRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.LigneVenteRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.ModePaiementRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.StatutCommandeRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.StatutVenteRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.VenteRepository;
import mg.vinaAkoho.vina_akoho.service.stockproduit.SortieProduitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VenteService {

    private final VenteRepository venteRepository;
    private final CommandeRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final LigneVenteRepository ligneVenteRepository;
    private final LigneVenteLotRepository ligneVenteLotRepository;
    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final ModePaiementRepository modePaiementRepository;
    private final StatutVenteRepository statutVenteRepository;
    private final StatutCommandeRepository statutCommandeRepository;
    private final SortieProduitService sortieProduitService;

    public List<VenteDTO> listerToutes() {
        return venteRepository.findAll()
                .stream()
                .map(this::versDTO)
                .collect(Collectors.toList());
    }

    public VenteDTO trouverParId(Long id) {
        return venteRepository.findById(id)
                .map(this::versDTO)
                .orElseThrow(() -> VenteNotFoundException.parId(id));
    }

    public VenteDTO creer(VenteFormDTO requete, List<PanierItemDTO> panier, Integer idEmploye) {
        if (panier == null || panier.isEmpty()) {
            throw new IllegalArgumentException("Le panier ne peut pas être vide");
        }

        Client client = clientRepository.findByIdAndEstSupprimerFalse(requete.getIdClient())
                .orElseThrow(() -> ClientNotFoundException.parId(requete.getIdClient()));

        ModePaiement modePaiement = modePaiementRepository.findById(requete.getIdModePaiement())
                .orElseThrow(() -> new IllegalArgumentException("Mode de paiement introuvable"));

        StatutVente statutVente = statutVenteRepository.findByLibelleIgnoreCase("Validée")
                .orElseGet(() -> statutVenteRepository.save(creerStatutVente("Validée")));

        StatutCommande statutCommande = statutCommandeRepository.findByLibelleIgnoreCase("Validée")
                .orElseGet(() -> statutCommandeRepository.save(creerStatutCommande("Validée")));

        Commande commande = new Commande();
        commande.setClient(client);
        commande.setStatutCommande(statutCommande);
        commande.setCommentaire("Commande créée depuis le module ventes");
        commande = commandeRepository.save(commande);

        BigDecimal montantTotal = panier.stream()
                .map(item -> item.getPrixUnitaire().multiply(item.getQuantite()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Vente vente = new Vente();
        vente.setCommande(commande);
        vente.setModePaiement(modePaiement);
        vente.setStatutVente(statutVente);
        vente.setMontantTotal(montantTotal);
        vente = venteRepository.save(vente);

        Commande commandeFinal = commande;
        Vente venteFinal = vente;

        for (PanierItemDTO item : panier) {
            Produit produit = produitRepository.findById(item.getIdProduit())
                    .orElseThrow(() -> ProduitNotFoundException.parId(item.getIdProduit()));

            LigneCommande ligneCommande = new LigneCommande();
            ligneCommande.setCommande(commandeFinal);
            ligneCommande.setProduit(produit);
            ligneCommande.setQuantite(item.getQuantite());
            ligneCommande.setPrixUnitaire(item.getPrixUnitaire());
            ligneCommandeRepository.save(ligneCommande);

            LigneVente ligneVente = new LigneVente();
            ligneVente.setVente(venteFinal);
            ligneVente.setProduit(produit);
            ligneVente.setQuantite(item.getQuantite());
            ligneVente.setPrixUnitaire(item.getPrixUnitaire());
            ligneVente.setMontant(item.getPrixUnitaire().multiply(item.getQuantite()));
            ligneVente = ligneVenteRepository.save(ligneVente);

            for (SortieProduitService.Allocation allocation : sortieProduitService.allouerLots(
                    produit.getId(), item.getQuantite(), idEmploye,
                    "VENTE-" + venteFinal.getId())) {
                LigneVenteLot ligneVenteLot = new LigneVenteLot();
                ligneVenteLot.setLigneVente(ligneVente);
                ligneVenteLot.setLotProduit(allocation.getLotProduit());
                ligneVenteLot.setQuantite(allocation.getQuantite());
                ligneVenteLotRepository.save(ligneVenteLot);
            }
        }

        Facture facture = new Facture();
        facture.setVente(vente);
        facture.setNumero(genererNumeroFacture());
        facture.setDateEmission(LocalDate.now());
        facture.setMontantHt(montantTotal);
        facture.setTauxTva(BigDecimal.ZERO);
        facture.setMontantTva(BigDecimal.ZERO);
        facture.setMontantTtc(montantTotal);
        factureRepository.save(facture);

        return versDTO(vente);
    }

    private StatutVente creerStatutVente(String libelle) {
        StatutVente statut = new StatutVente();
        statut.setLibelle(libelle);
        return statut;
    }

    private StatutCommande creerStatutCommande(String libelle) {
        StatutCommande statut = new StatutCommande();
        statut.setLibelle(libelle);
        return statut;
    }

    private String genererNumeroFacture() {
        return "FACT-" + System.currentTimeMillis();
    }

    private VenteDTO versDTO(Vente vente) {
        List<LigneVenteDTO> lignes = ligneVenteRepository.findByVenteId(vente.getId())
                .stream()
                .map(this::versLigneDTO)
                .collect(Collectors.toList());

        FactureDTO factureDTO = factureRepository.findByVenteId(vente.getId())
                .map(this::versFactureDTO)
                .orElse(null);

        Client client = vente.getCommande().getClient();

        return VenteDTO.builder()
                .id(vente.getId())
                .clientNom(client.getNom())
                .clientPrenom(client.getPrenom())
                .clientTelephone(client.getNumeroTelephone())
                .clientAdresse(client.getAdresse())
                .dateVente(vente.getDateVente())
                .modePaiement(vente.getModePaiement().getLibelle())
                .statutVente(vente.getStatutVente().getLibelle())
                .montantTotal(vente.getMontantTotal())
                .lignes(lignes)
                .facture(factureDTO)
                .build();
    }

    private LigneVenteDTO versLigneDTO(LigneVente ligne) {
        return LigneVenteDTO.builder()
                .nomProduit(ligne.getProduit().getNom())
                .quantite(ligne.getQuantite())
                .prixUnitaire(ligne.getPrixUnitaire())
                .montant(ligne.getMontant())
                .build();
    }

    private FactureDTO versFactureDTO(Facture facture) {
        return FactureDTO.builder()
                .id(facture.getId())
                .numero(facture.getNumero())
                .dateEmission(facture.getDateEmission())
                .montantHt(facture.getMontantHt())
                .tauxTva(facture.getTauxTva())
                .montantTva(facture.getMontantTva())
                .montantTtc(facture.getMontantTtc())
                .build();
    }
}
