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
import mg.vinaAkoho.vina_akoho.entity.produit.Fabrication;
import mg.vinaAkoho.vina_akoho.entity.produit.FabricationMp;
import mg.vinaAkoho.vina_akoho.entity.produit.LotProduit;
import mg.vinaAkoho.vina_akoho.entity.produit.MouvementStockProduit;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.LotMp;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MouvementStockMp;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.TypeMouvement;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Unite;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.entity.recetteproduit.RecetteProduit;
import mg.vinaAkoho.vina_akoho.exception.produit.ProduitNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.produit.RecetteInexistanteException;
import mg.vinaAkoho.vina_akoho.exception.stockmp.StockInsuffisantException;
import mg.vinaAkoho.vina_akoho.repository.produit.FabricationMpRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.FabricationRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.MouvementStockProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.LotMpRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.MouvementStockMpRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.TypeMouvementRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.UniteRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.recetteproduit.RecetteProduitRepository;

@Service
public class EntreeProduitService {

    private final ProduitRepository produitRepository;
    private final RecetteProduitRepository recetteProduitRepository;
    private final LotMpRepository lotMpRepository;
    private final MouvementStockMpRepository mouvementStockMpRepository;
    private final LotProduitRepository lotProduitRepository;
    private final MouvementStockProduitRepository mouvementStockProduitRepository;
    private final FabricationRepository fabricationRepository;
    private final FabricationMpRepository fabricationMpRepository;
    private final TypeMouvementRepository typeMouvementRepository;
    private final UniteRepository uniteRepository;
    private final EmployeRepository employeRepository;

    public EntreeProduitService(ProduitRepository produitRepository,
                                 RecetteProduitRepository recetteProduitRepository,
                                 LotMpRepository lotMpRepository,
                                 MouvementStockMpRepository mouvementStockMpRepository,
                                 LotProduitRepository lotProduitRepository,
                                 MouvementStockProduitRepository mouvementStockProduitRepository,
                                 FabricationRepository fabricationRepository,
                                 FabricationMpRepository fabricationMpRepository,
                                 TypeMouvementRepository typeMouvementRepository,
                                 UniteRepository uniteRepository,
                                 EmployeRepository employeRepository) {
        this.produitRepository = produitRepository;
        this.recetteProduitRepository = recetteProduitRepository;
        this.lotMpRepository = lotMpRepository;
        this.mouvementStockMpRepository = mouvementStockMpRepository;
        this.lotProduitRepository = lotProduitRepository;
        this.mouvementStockProduitRepository = mouvementStockProduitRepository;
        this.fabricationRepository = fabricationRepository;
        this.fabricationMpRepository = fabricationMpRepository;
        this.typeMouvementRepository = typeMouvementRepository;
        this.uniteRepository = uniteRepository;
        this.employeRepository = employeRepository;
    }

    @Transactional
    public EntreeProduitResponseDTO produire(EntreeProduitRequestDTO dto) {

        // --- 0. Chargement des références ---
        Produit produit = produitRepository.findByIdAndActifTrue(dto.idProduit())
                .orElseThrow(() -> new ProduitNotFoundException(
                        "Produit introuvable ou inactif : id=" + dto.idProduit()));

        Employe employe = employeRepository.findById(dto.idEmploye())
                .orElseThrow(() -> new ProduitNotFoundException(
                        "Employé introuvable : id=" + dto.idEmploye()));

        Integer idCategorie = produit.getCategorie().getId().intValue();
        BigDecimal quantiteAProduire = dto.quantiteAProduire();

        // --- 1 & 2. Lire la recette active de la catégorie ---
        List<RecetteProduit> recette = recetteProduitRepository.findByIdCategorieAndIsActiveTrue(idCategorie);
        if (recette.isEmpty()) {
            throw new RecetteInexistanteException(
                    "Aucune recette active pour la catégorie du produit " + produit.getNom());
        }

        // --- 1. Vérifier que les MP sont suffisantes AVANT de toucher au stock ---
        for (RecetteProduit ligne : recette) {
            BigDecimal besoin = ligne.getQuantiteMp().multiply(quantiteAProduire);
            BigDecimal disponible = lotMpRepository.sommeQuantiteRestante(ligne.getMatierePremiere().getId());
            if (disponible.compareTo(besoin) < 0) {
                throw new StockInsuffisantException(
                        "Stock insuffisant pour " + ligne.getMatierePremiere().getNom()
                                + " (besoin: " + besoin + ", disponible: " + disponible + ")");
            }
        }

        TypeMouvement typeSortie = typeMouvementRepository.findByLibelle("Sortie")
                .orElseThrow(() -> new RuntimeException("Type de mouvement 'Sortie' introuvable en base"));
        TypeMouvement typeEntreeMouvement = typeMouvementRepository.findByLibelle("Entree") // ✅ corrigé
                .orElseThrow(() -> new RuntimeException("Type de mouvement 'Entree' introuvable en base"));

        // --- 5. Créer le lot_produit (avant fabrication car FK obligatoire) ---
        LotProduit lotProduit = new LotProduit();
        lotProduit.setProduit(produit);
        lotProduit.setQuantiteInitiale(quantiteAProduire);
        lotProduit.setQuantiteRestante(quantiteAProduire);
        lotProduit.setDateFabrication(LocalDate.now());
        lotProduit.setDatePeremption(dto.datePeremption());
        lotProduit = lotProduitRepository.save(lotProduit);

        // --- Enregistrement fabrication (traçabilité) ---
        Fabrication fabrication = new Fabrication();
        fabrication.setQuantiteProduite(quantiteAProduire);
        fabrication.setLotProduit(lotProduit);
        fabrication.setEmploye(employe);
        fabrication = fabricationRepository.save(fabrication);

        // --- 3 & 4. Déduire les MP en FIFO + mouvementStockMP (Sortie) ---
        List<DetailConsommationMpDTO> details = new ArrayList<>();

        for (RecetteProduit ligne : recette) {
            BigDecimal aConsommer = ligne.getQuantiteMp().multiply(quantiteAProduire);
            Integer idMp = ligne.getMatierePremiere().getId();

            Unite unite = uniteRepository.findById(ligne.getIdUnite())
                    .orElseThrow(() -> new RuntimeException("Unité introuvable : id=" + ligne.getIdUnite()));

            List<LotMp> lots = lotMpRepository
                    .findByMatierePremiereIdAndQuantiteRestanteGreaterThanOrderByDateAchatAsc(idMp, BigDecimal.ZERO);

            for (LotMp lot : lots) {
                if (aConsommer.compareTo(BigDecimal.ZERO) <= 0) break;

                BigDecimal pris = lot.getQuantiteRestante().min(aConsommer);
                lot.setQuantiteRestante(lot.getQuantiteRestante().subtract(pris));
                lotMpRepository.save(lot);

                MouvementStockMp mvtMp = new MouvementStockMp();
                mvtMp.setTypeMouvement(typeSortie);
                mvtMp.setLotMp(lot);
                mvtMp.setQuantite(pris);
                mvtMp.setUnite(unite);
                mvtMp.setIdEmploye(dto.idEmploye());
                mvtMp.setDateMouvement(LocalDate.now());
                mvtMp.setObservation("Consommation production - Fabrication #" + fabrication.getId());
                mouvementStockMpRepository.save(mvtMp);

                FabricationMp fabMp = new FabricationMp();
                fabMp.setFabrication(fabrication);
                fabMp.setLotMp(lot);
                fabMp.setQuantite(pris);
                fabMp.setIdUnite(ligne.getIdUnite());
                fabricationMpRepository.save(fabMp);

                details.add(new DetailConsommationMpDTO(lot.getId(), ligne.getMatierePremiere().getNom(), pris));

                aConsommer = aConsommer.subtract(pris);
            }

            if (aConsommer.compareTo(BigDecimal.ZERO) > 0) {
                throw new StockInsuffisantException(
                        "Incohérence de stock détectée pour " + ligne.getMatierePremiere().getNom());
            }
        }

        // --- 6. mouvementStockPR (Entrée) ---
        Unite uniteProduit = uniteRepository.findById(recette.get(0).getIdUnite())
                .orElseThrow(() -> new RuntimeException("Unité introuvable"));

        MouvementStockProduit mvtProduit = new MouvementStockProduit();
        mvtProduit.setTypeMouvement(typeEntreeMouvement); // ✅ maintenant "Entree", pas "Sortie"
        mvtProduit.setLotProduit(lotProduit);
        mvtProduit.setQuantite(quantiteAProduire);
        mvtProduit.setUnite(uniteProduit);
        mvtProduit.setIdEmploye(dto.idEmploye());
        mvtProduit.setDateMouvement(LocalDate.now());
        mvtProduit.setObservation("Production - Fabrication #" + fabrication.getId());
        mouvementStockProduitRepository.save(mvtProduit);

        // --- 7. Stock produit = dérivé de SUM(lot_produit.quantite_restante), pas de champ dénormalisé ---

        return new EntreeProduitResponseDTO(
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
