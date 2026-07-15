package mg.vinaAkoho.vina_akoho.service.produit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.vinaAkoho.vina_akoho.dto.produit.DetailConsommationMpDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.EntreeProduitRequestDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.EntreeProduitResponseDTO;
import mg.vinaAkoho.vina_akoho.dto.stockmp.MouvementStockMpDTO;
import mg.vinaAkoho.vina_akoho.dto.stockmp.SortieMpRequestDTO;
import mg.vinaAkoho.vina_akoho.entity.produit.Fabrication;
import mg.vinaAkoho.vina_akoho.entity.produit.FabricationMp;
import mg.vinaAkoho.vina_akoho.entity.produit.LotProduit;
import mg.vinaAkoho.vina_akoho.entity.produit.MouvementStockProduit;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.LotMp;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.TypeMouvement;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Unite;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.entity.recetteproduit.RecetteProduit;
import mg.vinaAkoho.vina_akoho.exception.produit.ProduitNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.produit.RecetteInexistanteException;
import mg.vinaAkoho.vina_akoho.repository.produit.FabricationMpRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.FabricationRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.MouvementStockProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.LotMpRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.TypeMouvementRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.UniteRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.recetteproduit.RecetteProduitRepository;
import mg.vinaAkoho.vina_akoho.service.stockmp.SortieMpService;

@Service
public class EntreeProduitService {

    private final ProduitRepository produitRepository;
    private final RecetteProduitRepository recetteProduitRepository;
    private final LotMpRepository lotMpRepository;
    private final LotProduitRepository lotProduitRepository;
    private final MouvementStockProduitRepository mouvementStockProduitRepository;
    private final FabricationRepository fabricationRepository;
    private final FabricationMpRepository fabricationMpRepository;
    private final TypeMouvementRepository typeMouvementRepository;
    private final UniteRepository uniteRepository;
    private final EmployeRepository employeRepository;
    private final SortieMpService sortieMpService; // ← réutilisation du module Tsiory

    public EntreeProduitService(ProduitRepository produitRepository,
                                 RecetteProduitRepository recetteProduitRepository,
                                 LotMpRepository lotMpRepository,
                                 LotProduitRepository lotProduitRepository,
                                 MouvementStockProduitRepository mouvementStockProduitRepository,
                                 FabricationRepository fabricationRepository,
                                 FabricationMpRepository fabricationMpRepository,
                                 TypeMouvementRepository typeMouvementRepository,
                                 UniteRepository uniteRepository,
                                 EmployeRepository employeRepository,
                                 SortieMpService sortieMpService) {
        this.produitRepository = produitRepository;
        this.recetteProduitRepository = recetteProduitRepository;
        this.lotMpRepository = lotMpRepository;
        this.lotProduitRepository = lotProduitRepository;
        this.mouvementStockProduitRepository = mouvementStockProduitRepository;
        this.fabricationRepository = fabricationRepository;
        this.fabricationMpRepository = fabricationMpRepository;
        this.typeMouvementRepository = typeMouvementRepository;
        this.uniteRepository = uniteRepository;
        this.employeRepository = employeRepository;
        this.sortieMpService = sortieMpService;
    }

    @Transactional
    public EntreeProduitResponseDTO produire(EntreeProduitRequestDTO dto) {
        if (dto.datePeremption() != null && dto.datePeremption().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La date de péremption ne peut pas être antérieure à la date de fabrication.");
        }

        // --- 0. Chargement des références ---
        Produit produit = produitRepository.findByIdAndActifTrue(dto.idProduit())
                .orElseThrow(() -> new ProduitNotFoundException(
                        "Produit introuvable ou inactif : id=" + dto.idProduit()));

        Employe employe = employeRepository.findById(dto.idEmploye())
                .orElseThrow(() -> new ProduitNotFoundException(
                        "Employé introuvable : id=" + dto.idEmploye()));

        Integer idCategorie = produit.getCategorie().getId().intValue();
        BigDecimal quantiteAProduire = dto.quantiteAProduire();

        // --- 1 & 2. Vérifier qu'une recette existe (le detail du calcul MP est délégué à SortieMpService) ---
        List<RecetteProduit> recette = recetteProduitRepository.findByIdCategorieAndIsActiveTrue(idCategorie);
        if (recette.isEmpty()) {
            throw new RecetteInexistanteException(
                    "Aucune recette active pour la catégorie du produit " + produit.getNom());
        }

        // --- 5. Créer le lot_produit ---
        LotProduit lotProduit = new LotProduit();
        lotProduit.setProduit(produit);
        lotProduit.setQuantiteInitiale(quantiteAProduire);
        lotProduit.setQuantiteRestante(quantiteAProduire);
        lotProduit.setDateFabrication(LocalDate.now());
        lotProduit.setDatePeremption(dto.datePeremption());
        lotProduit = lotProduitRepository.save(lotProduit);

        // --- Traçabilité fabrication ---
        Fabrication fabrication = new Fabrication();
        fabrication.setQuantiteProduite(quantiteAProduire);
        fabrication.setLotProduit(lotProduit);
        fabrication.setEmploye(employe);
        fabrication = fabricationRepository.save(fabrication);

        // --- 3 & 4. Déduction FIFO des MP — DÉLÉGUÉE à SortieMpService (déjà fait par Tsiory) ---
        SortieMpRequestDTO sortieRequete = new SortieMpRequestDTO();
        sortieRequete.setIdCategorie(idCategorie);
        sortieRequete.setIdEmploye(dto.idEmploye());
        sortieRequete.setQuantiteAProduire(quantiteAProduire); // ✅ nouveau champ ajouté
        sortieRequete.setReferenceDocument("FAB-" + fabrication.getId());

        List<MouvementStockMpDTO> mouvementsMp = sortieMpService.effectuerSortie(sortieRequete);

        // Construction de la traçabilité fabrication_mp + détails de réponse à partir des mouvements créés
        List<DetailConsommationMpDTO> details = new ArrayList<>();
        for (MouvementStockMpDTO mvt : mouvementsMp) {
            LotMp lot = lotMpRepository.findById(mvt.getIdLotMp())
                    .orElseThrow(() -> new RuntimeException("Lot MP introuvable : id=" + mvt.getIdLotMp()));

            FabricationMp fabMp = new FabricationMp();
            fabMp.setFabrication(fabrication);
            fabMp.setLotMp(lot);
            fabMp.setQuantite(mvt.getQuantite());
            fabMp.setIdUnite(mvt.getIdUnite());
            fabricationMpRepository.save(fabMp);

            details.add(new DetailConsommationMpDTO(mvt.getIdLotMp(), mvt.getNomMp(), mvt.getQuantite()));
        }

        // --- 6. mouvementStockPR (Entrée) ---
        TypeMouvement typeEntreeMouvement = typeMouvementRepository.findByLibelle("Entree")
                .orElseThrow(() -> new RuntimeException("Type de mouvement 'Entree' introuvable en base"));

        Unite uniteProduit = uniteRepository.findByLibelle("unité")
                .orElseThrow(() -> new RuntimeException(
                        "Unité par défaut 'unité' introuvable en base — exécuter : " +
                        "INSERT INTO unite (libelle) VALUES ('unité');"));

        MouvementStockProduit mvtProduit = new MouvementStockProduit();
        mvtProduit.setTypeMouvement(typeEntreeMouvement);
        mvtProduit.setLotProduit(lotProduit);
        mvtProduit.setQuantite(quantiteAProduire);
        mvtProduit.setUnite(uniteProduit);
        mvtProduit.setIdEmploye(dto.idEmploye());
        mvtProduit.setDateMouvement(LocalDate.now());
        mvtProduit.setObservation("Production - Fabrication #" + fabrication.getId());
        mouvementStockProduitRepository.save(mvtProduit);

        return new EntreeProduitResponseDTO(
                fabrication.getId(),
                lotProduit.getId(),
                produit.getNom(),
                quantiteAProduire,
                lotProduit.getDateFabrication(),
                details
        );
    }

    public BigDecimal getStockDisponible(Long idProduit) {
        return lotProduitRepository.sommeQuantiteRestante(idProduit);
    }
}
