package mg.vinaAkoho.vina_akoho.service.stockproduit;

import jakarta.transaction.Transactional;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.TypeMouvement;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Unite;
import mg.vinaAkoho.vina_akoho.entity.produit.LotProduit;
import mg.vinaAkoho.vina_akoho.entity.produit.MouvementStockProduit;
import mg.vinaAkoho.vina_akoho.exception.stockmp.StockInsuffisantException;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.TypeMouvementRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.UniteRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.MouvementStockProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SortieProduitService {

    private static final String LIBELLE_TYPE_MOUVEMENT_SORTIE = "Sortie";

    private final LotProduitRepository lotProduitRepository;
    private final MouvementStockProduitRepository mouvementStockProduitRepository;
    private final TypeMouvementRepository typeMouvementRepository;
    private final EmployeRepository employeRepository;
    private final UniteRepository uniteRepository;

    @Autowired
    public SortieProduitService(
            LotProduitRepository lotProduitRepository,
            MouvementStockProduitRepository mouvementStockProduitRepository,
            TypeMouvementRepository typeMouvementRepository,
            EmployeRepository employeRepository,
            UniteRepository uniteRepository
    ) {
        this.lotProduitRepository = lotProduitRepository;
        this.mouvementStockProduitRepository = mouvementStockProduitRepository;
        this.typeMouvementRepository = typeMouvementRepository;
        this.employeRepository = employeRepository;
        this.uniteRepository = uniteRepository;
    }

    @Transactional
    public List<Allocation> allouerLots(Long produitId,
                                       BigDecimal quantiteDemande,
                                       Integer idEmploye,
                                       String referenceDocument) {
        BigDecimal stockDisponible = lotProduitRepository.sommeQuantiteRestante(produitId);
        if (stockDisponible.compareTo(quantiteDemande) < 0) {
            throw new StockInsuffisantException(
                    "Stock insuffisant pour le produit #" + produitId + " : disponible "
                            + stockDisponible + ", requis " + quantiteDemande);
        }

        Employe employe = employeRepository.findById(idEmploye)
                .orElseThrow(() -> new StockInsuffisantException(
                        "Employé #" + idEmploye + " introuvable pour l'enregistrement du mouvement"));

        TypeMouvement typeSortie = typeMouvementRepository.findByLibelle(LIBELLE_TYPE_MOUVEMENT_SORTIE)
                .orElseThrow(() -> new StockInsuffisantException(
                        "Type de mouvement '" + LIBELLE_TYPE_MOUVEMENT_SORTIE + "' introuvable"));

        Unite unite = uniteRepository.findById(1)
                .orElseGet(() -> uniteRepository.findAll()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new StockInsuffisantException("Aucune unité disponible pour le mouvement")));

        List<LotProduit> lots = lotProduitRepository.findByProduitIdAndQuantiteRestanteGreaterThanOrderByDateFabricationAsc(
                produitId,
                BigDecimal.ZERO
        );

        BigDecimal reste = quantiteDemande;
        List<Allocation> allocations = new ArrayList<>();

        for (LotProduit lot : lots) {
            if (reste.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal quantitePrelevee = lot.getQuantiteRestante().min(reste);
            lot.setQuantiteRestante(lot.getQuantiteRestante().subtract(quantitePrelevee));
            lotProduitRepository.save(lot);

            MouvementStockProduit mouvement = new MouvementStockProduit();
            mouvement.setTypeMouvement(typeSortie);
            mouvement.setLotProduit(lot);
            mouvement.setQuantite(quantitePrelevee);
            mouvement.setUnite(unite);
            mouvement.setIdEmploye(employe.getId());
            mouvement.setReferenceDocument(referenceDocument);
            mouvement.setObservation("Sortie automatique - vente produit #" + produitId);
            mouvementStockProduitRepository.save(mouvement);

            allocations.add(new Allocation(lot, quantitePrelevee));
            reste = reste.subtract(quantitePrelevee);
        }

        if (reste.compareTo(BigDecimal.ZERO) > 0) {
            throw new StockInsuffisantException(
                    "Stock insuffisant après allocation FIFO pour le produit #" + produitId);
        }

        return allocations;
    }

    public static class Allocation {

        private final LotProduit lotProduit;
        private final BigDecimal quantite;

        public Allocation(LotProduit lotProduit, BigDecimal quantite) {
            this.lotProduit = lotProduit;
            this.quantite = quantite;
        }

        public LotProduit getLotProduit() {
            return lotProduit;
        }

        public BigDecimal getQuantite() {
            return quantite;
        }
    }
}
