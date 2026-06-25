package mg.vinaAkoho.vina_akoho.service.produit;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.produit.CategorieDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.CategorieRequestDTO;
import mg.vinaAkoho.vina_akoho.entity.produit.Categorie;
import mg.vinaAkoho.vina_akoho.exception.produit.CategorieDejaExistanteException;
import mg.vinaAkoho.vina_akoho.exception.produit.CategorieEnUtilisationException;
import mg.vinaAkoho.vina_akoho.exception.produit.CategorieNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.produit.CategorieRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;

    public List<CategorieDTO> listerToutes() {
        return categorieRepository.findAllByActifTrue()
                .stream()
                .map(this::versDTO)
                .toList();
    }

    public Page<CategorieDTO> listerToutes(Pageable pageable) {
        return categorieRepository.findAllByActifTrue(pageable)
                .map(this::versDTO);
    }

    public CategorieDTO trouverParId(Long id) {
        Categorie categorie = categorieRepository.findByIdAndActifTrue(id)
                .orElseThrow(() -> CategorieNotFoundException.parId(id));
        return versDTO(categorie);
    }

    public CategorieDTO trouverParIdPourModification(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> CategorieNotFoundException.parId(id));
        return versDTO(categorie);
    }

    public CategorieDTO creer(CategorieRequestDTO requete) {
        if (categorieRepository.existsByLibelleIgnoreCaseAndActifTrue(requete.getLibelle())) {
            throw new CategorieDejaExistanteException(
                    "Une catégorie avec le libellé '" + requete.getLibelle() + "' existe déjà");
        }

        Categorie categorie = new Categorie();
        appliquerRequete(categorie, requete);

        if (requete.getActif() == null) {
            categorie.setActif(true);
        } else {
            categorie.setActif(requete.getActif());
        }

        Categorie sauvegardee = categorieRepository.save(categorie);
        return versDTO(sauvegardee);
    }

    public CategorieDTO modifier(Long id, CategorieRequestDTO requete) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> CategorieNotFoundException.parId(id));

        if (categorieRepository.existsByLibelleIgnoreCaseAndIdNotAndActifTrue(requete.getLibelle(), id)) {
            throw new CategorieDejaExistanteException(
                    "Une catégorie avec le libellé '" + requete.getLibelle() + "' existe déjà");
        }

        appliquerRequete(categorie, requete);

        if (requete.getActif() != null) {
            categorie.setActif(requete.getActif());
        }

        Categorie sauvegardee = categorieRepository.save(categorie);
        return versDTO(sauvegardee);
    }

    // Désactiver une catégorie (soft delete)
    public void supprimer(Long id) {
        Categorie categorie = categorieRepository.findByIdAndActifTrue(id)
                .orElseThrow(() -> CategorieNotFoundException.parId(id));

        long nombreProduitsLies = produitRepository.countByCategorieIdAndActifTrue(id);
        if (nombreProduitsLies > 0) {
            throw new CategorieEnUtilisationException(
                    "Impossible de désactiver la catégorie '" + categorie.getLibelle()
                            + "' : elle est encore utilisée par " + nombreProduitsLies + " produit(s) actif(s)");
        }

        // Soft delete : on désactive
        categorie.setActif(false);
        categorieRepository.save(categorie);
    }

    public void reactiver(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> CategorieNotFoundException.parId(id));

        if (categorie.getActif()) {
            throw new RuntimeException("La catégorie '" + categorie.getLibelle() + "' est déjà active");
        }

        if (categorieRepository.existsByLibelleIgnoreCaseAndActifTrue(categorie.getLibelle())) {
            throw new CategorieDejaExistanteException(
                    "Impossible de réactiver : une catégorie avec le libellé '"
                            + categorie.getLibelle() + "' existe déjà");
        }

        categorie.setActif(true);
        categorieRepository.save(categorie);
    }


    private void appliquerRequete(Categorie categorie, CategorieRequestDTO requete) {
        categorie.setLibelle(requete.getLibelle());
        categorie.setDescription(requete.getDescription());
        categorie.setPourcentageProteine(requete.getPourcentageProteine());
        categorie.setPourcentageMatiereGrasses(requete.getPourcentageMatiereGrasses());
        categorie.setPourcentageHumiditeMax(requete.getPourcentageHumiditeMax());
        categorie.setMargePourcentage(requete.getMargePourcentage());
    }

    private CategorieDTO versDTO(Categorie categorie) {
        long nombreProduits = produitRepository.countByCategorieIdAndActifTrue(categorie.getId());

        return CategorieDTO.builder()
                .id(categorie.getId())
                .libelle(categorie.getLibelle())
                .description(categorie.getDescription())
                .pourcentageProteine(categorie.getPourcentageProteine())
                .pourcentageMatiereGrasses(categorie.getPourcentageMatiereGrasses())
                .pourcentageHumiditeMax(categorie.getPourcentageHumiditeMax())
                .margePourcentage(categorie.getMargePourcentage())
                .actif(categorie.getActif())
                .nombreProduits(nombreProduits)
                .createdAt(categorie.getCreatedAt())
                .updatedAt(categorie.getUpdatedAt())
                .build();
    }
    // Dans CategorieService.java, ajouter cette méthode :

    // Rechercher des catégories par libellé
    public Page<CategorieDTO> rechercher(String search, Pageable pageable) {
        return categorieRepository.findByLibelleContainingIgnoreCaseAndActifTrue(search, pageable)
                .map(this::versDTO);
    }

}