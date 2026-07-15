package mg.vinaAkoho.vina_akoho.controller.recetteproduit;

import mg.vinaAkoho.vina_akoho.dto.produit.CategorieDTO;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.UniteRepository;
import mg.vinaAkoho.vina_akoho.service.produit.CategorieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

// Affiche la page de gestion de recette (formulaire). La sauvegarde elle-meme
// passe par l'API JSON deja existante POST /api/recettes (RecetteProduitController),
// appelee en AJAX depuis categorie/recette.html.
@Controller
@RequestMapping("/api/categories/{idCategorie}/recette")
public class RecetteProduitViewController {

    private final CategorieService categorieService;
    private final UniteRepository uniteRepository;

    public RecetteProduitViewController(CategorieService categorieService, UniteRepository uniteRepository) {
        this.categorieService = categorieService;
        this.uniteRepository = uniteRepository;
    }

    @GetMapping
    public String afficherFormulaireRecette(@PathVariable Long idCategorie, Model model) {
        CategorieDTO categorie = categorieService.trouverParId(idCategorie);
        model.addAttribute("categorie", categorie);
        model.addAttribute("unites", uniteRepository.findAll());
        return "categorie/recette";
    }
}
