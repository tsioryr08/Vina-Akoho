package mg.vinaAkoho.vina_akoho.service.ventes;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientHistoriqueAchatsDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.FactureDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.LigneVenteDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.PanierItemDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.RechercheVenteDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteFormDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteStatistiquesDTO;
import mg.vinaAkoho.vina_akoho.entity.clients.Client;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.entity.ventes.Facture;
import mg.vinaAkoho.vina_akoho.entity.ventes.LigneVente;
import mg.vinaAkoho.vina_akoho.entity.ventes.ModePaiement;
import mg.vinaAkoho.vina_akoho.entity.ventes.StatutVente;
import mg.vinaAkoho.vina_akoho.entity.ventes.Vente;
import mg.vinaAkoho.vina_akoho.exception.clients.ClientNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.produit.ProduitNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.ventes.VenteNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.clients.ClientRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.FactureRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.LigneVenteRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.ModePaiementRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.StatutVenteRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.VenteRepository;
import mg.vinaAkoho.vina_akoho.service.stockproduit.SortieProduitService;
import mg.vinaAkoho.vina_akoho.dto.livraison.LivraisonFormDTO;
import mg.vinaAkoho.vina_akoho.repository.livraison.LivraisonRepository;
import mg.vinaAkoho.vina_akoho.repository.livraison.StatutLivraisonRepository;
import mg.vinaAkoho.vina_akoho.service.livraison.LivraisonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VenteService {

    private final VenteRepository venteRepository;
    private final LigneVenteRepository ligneVenteRepository;
    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final ModePaiementRepository modePaiementRepository;
    private final StatutVenteRepository statutVenteRepository;
    private final SortieProduitService sortieProduitService;
    private final LivraisonService livraisonService;
    private final LivraisonRepository livraisonRepository;
    private final StatutLivraisonRepository statutLivraisonRepository;

    public List<VenteDTO> listerToutes() {
        return venteRepository.findAllByOrderByDateVenteDesc()
                .stream()
                .map(this::versDTO)
                .collect(Collectors.toList());
    }

    public VenteDTO trouverParId(Long id) {
        return venteRepository.findById(id)
                .map(this::versDTO)
                .orElseThrow(() -> VenteNotFoundException.parId(id));
    }

    @Transactional(readOnly = true)
    public ClientHistoriqueAchatsDTO obtenirHistoriqueClient(Integer clientId) {
        clientRepository.findByIdAndEstSupprimerFalse(clientId)
                .orElseThrow(() -> ClientNotFoundException.parId(clientId));

        List<VenteDTO> ventes = venteRepository.findByClientIdOrderByDateVenteDesc(clientId)
                .stream()
                .map(this::versDTO)
                .collect(Collectors.toList());

        BigDecimal totalAchats = venteRepository.sommeAchatsClient(clientId);
        BigDecimal totalRegle = venteRepository.sommeReglementsClient(clientId);

        return ClientHistoriqueAchatsDTO.builder()
                .ventes(ventes)
                .totalAchats(totalAchats)
                .totalRegle(totalRegle)
                .soldeRestant(totalAchats.subtract(totalRegle))
                .build();
    }

    public VenteStatistiquesDTO obtenirStatistiques() {
        LocalDate aujourdHui = LocalDate.now();
        LocalDate hier = aujourdHui.minusDays(1);
        LocalDate debutMois = aujourdHui.withDayOfMonth(1);

        long ventesDuJour = venteRepository.compterVentesValideesEntre(
                aujourdHui.atStartOfDay(), aujourdHui.plusDays(1).atStartOfDay());
        long ventesHier = venteRepository.compterVentesValideesEntre(
                hier.atStartOfDay(), aujourdHui.atStartOfDay());
        BigDecimal chiffreAffairesMois = venteRepository.sommeVentesValideesEntre(
                debutMois.atStartOfDay(), aujourdHui.plusDays(1).atStartOfDay());
        long ventesValidees = venteRepository.compterVentesValideesEntre(
                LocalDateTime.of(1900, 1, 1, 0, 0),
                LocalDateTime.of(3000, 1, 1, 0, 0));
        long toutesLesVentes = venteRepository.compterToutesLesVentes();
        BigDecimal tauxConversion = toutesLesVentes == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(ventesValidees)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(toutesLesVentes), 1, RoundingMode.HALF_UP);

        return VenteStatistiquesDTO.builder()
                .ventesDuJour(ventesDuJour)
                .variationVentesHier(ventesDuJour - ventesHier)
                .chiffreAffairesMois(chiffreAffairesMois)
                .ventesEnAttente(venteRepository.compterVentesEnAttente())
                .tauxConversion(tauxConversion)
                .build();
    }

public Page<VenteDTO> rechercherVentesAvecPagination(RechercheVenteDTO recherche) {
        if (recherche == null) {
            recherche = RechercheVenteDTO.parDefaut();
        }

        LocalDateTime dateDebut = null;
        LocalDateTime dateFin = null;

        if (recherche.getDateDebut() != null) {
            dateDebut = recherche.getDateDebut().atStartOfDay();
        }
        if (recherche.getDateFin() != null) {
            dateFin = recherche.getDateFin().atTime(23, 59, 59);
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(recherche.getOrdreTri())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        // IMPORTANT : la requête JPQL de VenteRepository.rechercherVentes utilise
        // les alias v (Vente), c (Client), mp (ModePaiement), sv (StatutVente).
        // Le Sort transmis à un Pageable pour une requête @Query doit utiliser
        // ces MEMES alias, sinon Hibernate ne sait pas résoudre la colonne de tri
        // (erreur 500) ou ignore silencieusement le tri.
        String champTriDemande = recherche.getTriPar();
        String champTri;
        if (champTriDemande == null || champTriDemande.isBlank()) {
            champTri = "v.dateVente";
        } else {
            switch (champTriDemande) {
                case "dateVente" -> champTri = "v.dateVente";
                case "montantTotal" -> champTri = "v.montantTotal";
                case "client.nom" -> champTri = "c.nom";
                default -> champTri = "v.dateVente"; // valeur inconnue -> tri par défaut sûr
            }
        }

        Pageable pageable = PageRequest.of(
                recherche.getPage() != null ? recherche.getPage() : 0,
                recherche.getTaille() != null ? recherche.getTaille() : 10,
                Sort.by(direction, champTri)
        );

        Page<Vente> pageVentes = venteRepository.rechercherVentes(
                recherche.getClient(),
                recherche.getProduit(),
                recherche.getNumeroFacture(),
                dateDebut,
                dateFin,
                recherche.getModePaiement(),
                recherche.getStatut(),
                recherche.getMontantMin(),
                recherche.getMontantMax(),
                pageable
        );

        return pageVentes.map(this::versDTO);
    }
    
    @Transactional
    public VenteDTO validerPaiement(Long id) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() -> VenteNotFoundException.parId(id));

        // Point 5 (option A) : si une livraison a été enregistrée pour cette
        // vente (cf. creer()), le paiement validé fait passer la vente en
        // "En livraison" plutôt que directement "Validée" (terminale).
        // C'est LivraisonService.modifierStatut(..., "Livrée", ...) qui fera
        // ensuite passer la vente à "Livrée" une fois la livraison effectuée.
        boolean avecLivraison = livraisonRepository.findByVenteId(id).isPresent();
        String libelleStatutCible = avecLivraison ? "En livraison" : "Validée";

        StatutVente statutCible = statutVenteRepository.findByLibelleIgnoreCase(libelleStatutCible)
                .orElseGet(() -> statutVenteRepository.save(creerStatutVente(libelleStatutCible)));

        vente.setStatutVente(statutCible);
        vente = venteRepository.save(vente);

        return versDTO(vente);
    }

    /**
     * Point 2 du markdown : annulation d'une commande tant qu'elle n'a pas été
     * finalisée. On n'autorise l'annulation que si le paiement n'a pas encore
     * été validé ("En attente de paiement") : une fois payée, une vente n'est
     * plus une simple commande annulable (il faudrait un vrai flux de
     * remboursement, hors périmètre ici).
     */
    @Transactional
    public VenteDTO annulerVente(Long id) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() -> VenteNotFoundException.parId(id));

        String statutActuel = vente.getStatutVente() != null ? vente.getStatutVente().getLibelle() : null;
        if (statutActuel == null || !"en attente de paiement".equalsIgnoreCase(statutActuel)) {
            throw new IllegalStateException(
                    "Seule une vente en attente de paiement peut être annulée (statut actuel : " + statutActuel + ")");
        }

        StatutVente statutAnnulee = statutVenteRepository.findByLibelleIgnoreCase("Annulée")
                .orElseGet(() -> statutVenteRepository.save(creerStatutVente("Annulée")));

        vente.setStatutVente(statutAnnulee);
        vente = venteRepository.save(vente);

        return versDTO(vente);
    }

    public VenteDTO creer(VenteFormDTO requete, List<PanierItemDTO> panier, Integer idEmploye) {
        if (panier == null || panier.isEmpty()) {
            throw new IllegalArgumentException("Le panier ne peut pas être vide");
        }

        Client client = clientRepository.findByIdAndEstSupprimerFalse(requete.getIdClient())
                .orElseThrow(() -> ClientNotFoundException.parId(requete.getIdClient()));

        ModePaiement modePaiement = modePaiementRepository.findById(requete.getIdModePaiement())
                .orElseThrow(() -> new IllegalArgumentException("Mode de paiement introuvable"));

        StatutVente statutVente = statutVenteRepository.findByLibelleIgnoreCase("En attente de paiement")
                .orElseGet(() -> statutVenteRepository.save(creerStatutVente("En attente de paiement")));

        BigDecimal montantTotal = panier.stream()
                .map(item -> {
                    Produit produit = produitRepository.findById(item.getIdProduit())
                            .orElseThrow(() -> ProduitNotFoundException.parId(item.getIdProduit()));
                    return produit.getPrixVente().multiply(item.getQuantite());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Vente vente = new Vente();
        vente.setClient(client);
        vente.setModePaiement(modePaiement);
        vente.setStatutVente(statutVente);
        vente.setMontantTotal(montantTotal);
        vente = venteRepository.save(vente);

        Vente venteFinal = vente;
        String referenceDocument = "VENTE-" + vente.getId();

        for (PanierItemDTO item : panier) {
            Produit produit = produitRepository.findById(item.getIdProduit())
                    .orElseThrow(() -> ProduitNotFoundException.parId(item.getIdProduit()));

            LigneVente ligneVente = new LigneVente();
            ligneVente.setVente(venteFinal);
            ligneVente.setProduit(produit);
            ligneVente.setQuantite(item.getQuantite());
            ligneVente.setPrixUnitaire(produit.getPrixVente());
            ligneVente.setMontant(produit.getPrixVente().multiply(item.getQuantite()));
            ligneVenteRepository.save(ligneVente);

            List<SortieProduitService.Allocation> allocations = sortieProduitService.allouerLots(
                    produit.getId(),
                    item.getQuantite(),
                    idEmploye,
                    referenceDocument
            );
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

        // Point 1 du markdown : "Livraison requise ?" - si oui, on enregistre
        // tout de suite les informations de livraison en réutilisant le module
        // livraison déjà existant (statut initial "En attente d'affectation").
        // La vente elle-même reste "En attente de paiement" jusqu'à
        // validerPaiement(...) ; c'est à ce moment-là qu'elle basculera sur
        // "En livraison" (cf. validerPaiement()).
        if (requete.isLivraisonRequise()) {
            Integer idStatutInitial = statutLivraisonRepository.findByLibelleIgnoreCase("En attente d'affectation")
                    .map(statut -> statut.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Statut de livraison \"En attente d'affectation\" introuvable : "
                                    + "exécutez la migration V24 avant de créer une vente avec livraison."));

            LivraisonFormDTO livraisonForm = LivraisonFormDTO.builder()
                    .idVente(vente.getId())
                    .lieuExact(requete.getAdresseLivraison())
                    .contact(requete.getContactLivraison())
                    .dateLivraison(requete.getDateLivraisonSouhaitee())
                    .commentaire(requete.getCommentaireLivraison())
                    .idStatutLivraison(idStatutInitial)
                    .build();

            livraisonService.creer(livraisonForm, idEmploye);
        }

        return versDTO(vente);
    }

    private StatutVente creerStatutVente(String libelle) {
        StatutVente statut = new StatutVente();
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

        Client client = vente.getClient();

        // Récupérer les informations de livraison si elles existent
        mg.vinaAkoho.vina_akoho.dto.livraison.LivraisonDTO livraisonDTO = null;
        try {
            var livraisonOpt = livraisonRepository.findByVenteId(vente.getId());
            if (livraisonOpt.isPresent()) {
                var livraison = livraisonOpt.get();
                try {
                    livraisonDTO = livraisonService.versDTO(livraison);
                } catch (Exception e) {
                    // Si versDTO échoue, on crée un DTO minimal
                    livraisonDTO = mg.vinaAkoho.vina_akoho.dto.livraison.LivraisonDTO.builder()
                            .id(livraison.getId())
                            .lieuExact(livraison.getLieuExact())
                            .contact(livraison.getContact())
                            .dateLivraison(livraison.getDateLivraison())
                            .commentaire(livraison.getCommentaire())
                            .statutLivraison(livraison.getStatutLivraison() != null ? livraison.getStatutLivraison().getLibelle() : null)
                            .livreurNom(livraison.getLivreur() != null ? livraison.getLivreur().getNom() : null)
                            .livreurPrenom(livraison.getLivreur() != null ? livraison.getLivreur().getPrenom() : null)
                            .build();
                }
            }
        } catch (Exception e) {
            // En cas d'erreur lors de la récupération de la livraison, on continue sans livraison
            livraisonDTO = null;
        }

        return VenteDTO.builder()
                .id(vente.getId())
                .clientNom(client.getNom())
                .clientPrenom(client.getPrenom())
                .clientTelephone(client.getNumeroTelephone())
                .clientAdresse(client.getAdresse())
                .clientZoneLivraison(client.getIdZoneLivraison())
                .dateVente(vente.getDateVente())
                .modePaiement(vente.getModePaiement().getLibelle())
                .statutVente(vente.getStatutVente().getLibelle())
                .montantTotal(vente.getMontantTotal())
                .lignes(lignes)
                .facture(factureDTO)
                .livraison(livraisonDTO)
                .build();
    }

    private LigneVenteDTO versLigneDTO(LigneVente ligne) {
        String unite = ligne.getProduit().getUnite() != null ? 
                ligne.getProduit().getUnite().getLibelle() : "";
        return LigneVenteDTO.builder()
                .nomProduit(ligne.getProduit().getNom())
                .quantite(ligne.getQuantite())
                .unite(unite)
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
                .remise(facture.getRemise() != null ? facture.getRemise() : BigDecimal.ZERO)
                .build();
    }
}
