package mg.vinaAkoho.vina_akoho.service.produit;

import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MatierePremiere;
import mg.vinaAkoho.vina_akoho.entity.produit.Categorie;
import mg.vinaAkoho.vina_akoho.entity.produit.LotProduit;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.entity.recetteproduit.RecetteProduit;
import mg.vinaAkoho.vina_akoho.repository.depense.DepenseLotRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.MatierePremiereRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.CategorieRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.recetteproduit.RecetteProduitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional(readOnly = true)
public class PrixVenteService {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;
    private final RecetteProduitRepository recetteProduitRepository;
    private final MatierePremiereRepository matierePremiereRepository;
    private final LotProduitRepository lotProduitRepository;
    private final DepenseLotRepository depenseLotRepository;

    public PrixVenteService(ProduitRepository produitRepository,
                            CategorieRepository categorieRepository,
                            RecetteProduitRepository recetteProduitRepository,
                            MatierePremiereRepository matierePremiereRepository,
                            LotProduitRepository lotProduitRepository,
                            DepenseLotRepository depenseLotRepository) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
        this.recetteProduitRepository = recetteProduitRepository;
        this.matierePremiereRepository = matierePremiereRepository;
        this.lotProduitRepository = lotProduitRepository;
        this.depenseLotRepository = depenseLotRepository;
    }

    public BigDecimal calculerPrixVente(Long produitId) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable : " + produitId));

        Categorie categorie = categorieRepository.findById(produit.getCategorie().getId())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));

        BigDecimal coutMatierePremiere = calculerCoutMatierePremiere(categorie.getId());
        BigDecimal coutDepensesFabrication = calculerCoutDepensesFabrication(produitId);

        BigDecimal coutTotal = coutMatierePremiere.add(coutDepensesFabrication);

        BigDecimal margePourcentage = categorie.getMargePourcentage();
        if (margePourcentage == null) {
            margePourcentage = BigDecimal.ZERO;
        }

        BigDecimal coefficientMarge = BigDecimal.ONE.add(
                margePourcentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );

        return coutTotal.multiply(coefficientMarge).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculerCoutMatierePremiere(Long idCategorie) {
        return recetteProduitRepository.findByIdCategorieAndIsActiveTrue(idCategorie.intValue())
                .stream()
                .map(recette -> {
                    MatierePremiere mp = matierePremiereRepository.findById(recette.getMatierePremiere().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Matière première introuvable"));
                    BigDecimal coutUnitaire = mp.getCoutUnitaire();
                    if (coutUnitaire == null) {
                        coutUnitaire = BigDecimal.ZERO;
                    }
                    return coutUnitaire.multiply(recette.getQuantiteMp());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculerCoutDepensesFabrication(Long produitId) {
        BigDecimal total = depenseLotRepository.sumDepensesByProduitId(produitId);
        return total != null ? total : BigDecimal.ZERO;
    }
}
