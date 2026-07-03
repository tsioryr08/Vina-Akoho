package mg.vinaAkoho.vina_akoho.service.produit;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.produit.ProduitDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.ProduitRequestDTO;
import mg.vinaAkoho.vina_akoho.entity.produit.Categorie;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.exception.produit.CategorieNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.produit.ProduitDejaExistantException;
import mg.vinaAkoho.vina_akoho.exception.produit.ProduitNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.produit.CategorieRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProduitService {

    private static final String STATUT_ALERTE = "SEUIL ATTEINT";
    private static final String STATUT_OK = "Stock Correct";

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;
    private final LotProduitRepository lotProduitRepository;

    public List<ProduitDTO> listerTous() {
        return produitRepository.findAllActifs()
                .stream()
                .map(this::versDTO)
                .toList();
    }

    // Rary — F2.2 : produits dont le stock actuel est descendu au seuil d'alerte
    public List<ProduitDTO> listerAlertes() {
        return listerTous().stream()
                .filter(p -> STATUT_ALERTE.equals(p.getStatut()))
                .toList();
    }

    public Page<ProduitDTO> rechercher(String recherche,
                                       Long idCategorie,
                                       BigDecimal prixMin,
                                       BigDecimal prixMax,
                                       Pageable pageable) {
        return produitRepository
                .findAll(ProduitSpecification.avecFiltres(recherche, idCategorie, prixMin, prixMax), pageable)
                .map(this::versDTO);
    }

    public ProduitDTO trouverParId(Long id) {
        Produit produit = produitRepository.findByIdAndActifTrue(id)
                .orElseThrow(() -> ProduitNotFoundException.parId(id));
        return versDTO(produit);
    }

    public ProduitDTO trouverParIdPourModification(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> ProduitNotFoundException.parId(id));
        return versDTO(produit);
    }

    public ProduitDTO creer(ProduitRequestDTO requete) {
        if (produitRepository.existsByRefIgnoreCaseAndActifTrue(requete.getRef())) {
            throw new ProduitDejaExistantException(
                    "Un produit avec la référence '" + requete.getRef() + "' existe déjà");
        }
        if (produitRepository.existsByNomIgnoreCaseAndActifTrue(requete.getNom())) {
            throw new ProduitDejaExistantException(
                    "Un produit avec le nom '" + requete.getNom() + "' existe déjà");
        }

        Categorie categorie = recupererCategorieOuLeverException(requete.getIdCategorie());

        Produit produit = new Produit();
        produit.setCategorie(categorie);
        appliquerRequete(produit, requete, categorie);

        if (requete.getActif() == null) {
            produit.setActif(true);
        } else {
            produit.setActif(requete.getActif());
        }

        Produit sauvegarde = produitRepository.save(produit);
        return versDTO(sauvegarde);
    }

    public ProduitDTO modifier(Long id, ProduitRequestDTO requete) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> ProduitNotFoundException.parId(id));

        if (produitRepository.existsByRefIgnoreCaseAndIdNotAndActifTrue(requete.getRef(), id)) {
            throw new ProduitDejaExistantException(
                    "Un produit avec la référence '" + requete.getRef() + "' existe déjà");
        }
        if (produitRepository.existsByNomIgnoreCaseAndIdNotAndActifTrue(requete.getNom(), id)) {
            throw new ProduitDejaExistantException(
                    "Un produit avec le nom '" + requete.getNom() + "' existe déjà");
        }

        Categorie categorie = recupererCategorieOuLeverException(requete.getIdCategorie());
        appliquerRequete(produit, requete, categorie);

        if (requete.getActif() != null) {
            produit.setActif(requete.getActif());
        }

        Produit sauvegarde = produitRepository.save(produit);
        return versDTO(sauvegarde);
    }

    public void supprimer(Long id) {
        Produit produit = produitRepository.findByIdAndActifTrue(id)
                .orElseThrow(() -> ProduitNotFoundException.parId(id));
        produit.setActif(false);
        produitRepository.save(produit);
    }

    public void reactiver(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> ProduitNotFoundException.parId(id));

        if (produit.getActif()) {
            throw new RuntimeException("Le produit '" + produit.getNom() + "' est déjà actif");
        }

        if (produitRepository.existsByRefIgnoreCaseAndActifTrue(produit.getRef())) {
            throw new ProduitDejaExistantException(
                    "Impossible de réactiver : un produit avec la référence '" + produit.getRef() + "' existe déjà");
        }
        if (produitRepository.existsByNomIgnoreCaseAndActifTrue(produit.getNom())) {
            throw new ProduitDejaExistantException(
                    "Impossible de réactiver : un produit avec le nom '" + produit.getNom() + "' existe déjà");
        }

        produit.setActif(true);
        produitRepository.save(produit);
    }


    private Produit recupererProduitOuLeverException(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> ProduitNotFoundException.parId(id));
    }

    private Categorie recupererCategorieOuLeverException(Long idCategorie) {
        return categorieRepository.findById(idCategorie)
                .orElseThrow(() -> CategorieNotFoundException.parId(idCategorie));
    }

    private void appliquerRequete(Produit produit, ProduitRequestDTO requete, Categorie categorie) {
        produit.setRef(requete.getRef());
        produit.setCategorie(categorie);
        produit.setNom(requete.getNom());
        produit.setPrixVente(requete.getPrixVente());
        produit.setSeuilAlerte(requete.getSeuilAlerte());
        produit.setDescription(requete.getDescription());
    }
    private ProduitDTO versDTO(Produit produit) {
        BigDecimal stock = lotProduitRepository.sommeQuantiteRestante(produit.getId());
        return ProduitDTO.builder()
                .id(produit.getId())
                .ref(produit.getRef())
                .idCategorie(produit.getCategorie().getId())
                .libelleCategorie(produit.getCategorie().getLibelle())
                .nom(produit.getNom())
                .prixVente(produit.getPrixVente())
                .seuilAlerte(produit.getSeuilAlerte())
                .description(produit.getDescription())
                .actif(produit.getActif())
                .margePourcentage(produit.getCategorie().getMargePourcentage())
                .pourcentageProteine(produit.getCategorie().getPourcentageProteine())
                .pourcentageMatiereGrasses(produit.getCategorie().getPourcentageMatiereGrasses())
                .pourcentageHumiditeMax(produit.getCategorie().getPourcentageHumiditeMax())
                .createdAt(produit.getCreatedAt())
                .updatedAt(produit.getUpdatedAt())
                .quantiteStock(stock)
                .statut(statut(stock, produit.getSeuilAlerte()))
                .build();
    }

    // Rary — F2.2, règle métier Sprint 2.2 : quantité actuelle <= seuil_alerte -> alerte
    private String statut(BigDecimal stock, Integer seuilAlerte) {
        if (seuilAlerte != null && stock.compareTo(BigDecimal.valueOf(seuilAlerte)) <= 0) {
            return STATUT_ALERTE;
        }
        return STATUT_OK;
    }

}