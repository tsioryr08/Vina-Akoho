package mg.vinaAkoho.vina_akoho.service.stockmp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import mg.vinaAkoho.vina_akoho.dto.recetteproduit.RecetteProduitDTO;
import mg.vinaAkoho.vina_akoho.dto.stockmp.MouvementStockMpDTO;
import mg.vinaAkoho.vina_akoho.dto.stockmp.SortieMpRequestDTO;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.LotMp;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MouvementStockMp;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.TypeMouvement;
import mg.vinaAkoho.vina_akoho.exception.stockmp.StockInsuffisantException;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.LotMpRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.MouvementStockMpRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.TypeMouvementRepository;
import mg.vinaAkoho.vina_akoho.service.recetteproduit.RecetteProduitService;

@Service
public class SortieMpService {

    private static final String LIBELLE_TYPE_MOUVEMENT_SORTIE = "Sortie";

    private final RecetteProduitService recetteProduitService;
    private final LotMpRepository lotMpRepository;
    private final MouvementStockMpRepository mouvementStockMpRepository;
    private final EmployeRepository employeRepository;
    private final TypeMouvementRepository typeMouvementRepository;

    @Autowired
    public SortieMpService(
            RecetteProduitService recetteProduitService,
            LotMpRepository lotMpRepository,
            MouvementStockMpRepository mouvementStockMpRepository,
            EmployeRepository employeRepository,
            TypeMouvementRepository typeMouvementRepository) {
        this.recetteProduitService = recetteProduitService;
        this.lotMpRepository = lotMpRepository;
        this.mouvementStockMpRepository = mouvementStockMpRepository;
        this.employeRepository = employeRepository;
        this.typeMouvementRepository = typeMouvementRepository;
    }

    /**
     * Déduit automatiquement les matières premières nécessaires à la
     * fabrication d'un produit, selon sa recette, en suivant la méthode
     * FIFO (on consomme d'abord les lots les plus anciens).
     *
     * Étapes (RG Sprint 2 — Entrée produit / Sortie MP) :
     *  1. Lire la recette_produit de la catégorie.
     *  2. Pour chaque ligne de recette, vérifier que le stock total est suffisant.
     *  3. Déduire la quantité nécessaire en suivant le FIFO sur les lots.
     *  4. Mettre à jour quantite_restante de chaque lot touché.
     *  5. Enregistrer un mouvementStockMP de type Sortie pour chaque lot touché.
     */
    @Transactional
    public List<MouvementStockMpDTO> effectuerSortie(SortieMpRequestDTO requete) {

        Employe employe = employeRepository.findById(requete.getIdEmploye())
                .orElseThrow(() -> new StockInsuffisantException(
                        "Employé #" + requete.getIdEmploye() + " introuvable"));

        TypeMouvement typeSortie = typeMouvementRepository.findByLibelle(LIBELLE_TYPE_MOUVEMENT_SORTIE)
                .orElseGet(() -> creerTypeMouvement(LIBELLE_TYPE_MOUVEMENT_SORTIE));

        // 1. Lire la recette active de la catégorie
        List<RecetteProduitDTO> recette = recetteProduitService.getRecetteActive(requete.getIdCategorie());

        List<MouvementStockMp> mouvementsCrees = new ArrayList<>();

        // 2. Pour chaque ligne de la recette, déduire la matière première
        for (RecetteProduitDTO ligne : recette) {
            BigDecimal besoin = ligne.getQuantiteMp();

            List<LotMp> lots = lotMpRepository
                    .findByMatierePremiereIdAndQuantiteRestanteGreaterThanOrderByDateAchatAsc(
                            ligne.getIdMp(), BigDecimal.ZERO);

            // Vérifier que le stock total est suffisant avant de toucher quoi que ce soit
            BigDecimal stockDisponible = lots.stream()
                    .map(LotMp::getQuantiteRestante)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (stockDisponible.compareTo(besoin) < 0) {
                throw new StockInsuffisantException(
                        "Stock insuffisant pour " + ligne.getNomMp()
                                + " : disponible " + stockDisponible + ", nécessaire " + besoin);
            }

            // 3. Déduire en FIFO, lot par lot
            BigDecimal resteADeduire = besoin;
            for (LotMp lot : lots) {
                if (resteADeduire.compareTo(BigDecimal.ZERO) <= 0) break;

                BigDecimal quantitePriseSurCeLot = lot.getQuantiteRestante().min(resteADeduire);

                // 4. Mise à jour du lot
                lot.setQuantiteRestante(lot.getQuantiteRestante().subtract(quantitePriseSurCeLot));
                lotMpRepository.save(lot);

                // 5. Création du mouvement de sortie pour ce lot
                MouvementStockMp mouvement = new MouvementStockMp();
                mouvement.setTypeMouvement(typeSortie);
                mouvement.setLotMp(lot);
                mouvement.setQuantite(quantitePriseSurCeLot);
                mouvement.setUnite(lot.getMatierePremiere().getUnite());
                mouvement.setIdEmploye(employe.getId());
                mouvement.setReferenceDocument(requete.getReferenceDocument());
                mouvement.setObservation("Sortie automatique - production catégorie #" + requete.getIdCategorie());

                mouvementsCrees.add(mouvementStockMpRepository.save(mouvement));

                resteADeduire = resteADeduire.subtract(quantitePriseSurCeLot);
            }
        }

        return mouvementsCrees.stream().map(this::toDTO).toList();
    }

    private TypeMouvement creerTypeMouvement(String libelle) {
        TypeMouvement typeMouvement = new TypeMouvement();
        typeMouvement.setLibelle(libelle);
        return typeMouvementRepository.save(typeMouvement);
    }

    private MouvementStockMpDTO toDTO(MouvementStockMp m) {
        MouvementStockMpDTO dto = new MouvementStockMpDTO();
        dto.setId(m.getId());
        dto.setIdLotMp(m.getLotMp().getId());
        dto.setNomMp(m.getLotMp().getMatierePremiere().getNom());
        dto.setQuantite(m.getQuantite());
        dto.setIdUnite(m.getUnite().getId());
        dto.setTypeMouvement(m.getTypeMouvement().getLibelle());
        return dto;
    }
}