package mg.vinaAkoho.vina_akoho.controller.produit;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.produit.HistoriquePrixProduitDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.ProduitDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.ProduitRequestDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.CategorieDTO;
import mg.vinaAkoho.vina_akoho.service.produit.ProduitService;
import mg.vinaAkoho.vina_akoho.service.produit.CategorieService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;
    private final CategorieService categorieService;

    @ModelAttribute("categories")
    public List<CategorieDTO> getAllCategories() {
        return categorieService.listerToutes();
    }

    @GetMapping
    public String listerTous(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long idCategorie,
            @RequestParam(required = false) BigDecimal prixMin,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Integer taille,
            @RequestParam(required = false) String triPar,
            @RequestParam(required = false, defaultValue = "desc") String ordreTri,
            Model model) {
        // La liste initiale et la recherche partagent la même pagination/base de données
        // pour que les liens de pagination restent cohérents.
        return rechercher(q, idCategorie, prixMin, prixMax, page, taille, triPar, ordreTri, model);
    }

    @GetMapping("/recherche")
    public String rechercher(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long idCategorie,
            @RequestParam(required = false) BigDecimal prixMin,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Integer taille,
            @RequestParam(required = false) String triPar,
            @RequestParam(required = false, defaultValue = "desc") String ordreTri,
            Model model) {

        Pageable pageable = buildPageable(page, taille, triPar, ordreTri);
        Page<ProduitDTO> resultats = produitService.rechercher(
                q, idCategorie, prixMin, prixMax, pageable);

        model.addAttribute("produits", resultats);
        model.addAttribute("totalElements", resultats.getTotalElements());
        model.addAttribute("totalPages", resultats.getTotalPages());
        model.addAttribute("currentPage", resultats.getNumber());
        model.addAttribute("q", q);
        model.addAttribute("idCategorie", idCategorie);
        model.addAttribute("prixMin", prixMin);
        model.addAttribute("prixMax", prixMax);
        model.addAttribute("taille", taille);
        model.addAttribute("triPar", triPar);
        model.addAttribute("ordreTri", ordreTri);

        return "produit/list";
    }

    private Pageable buildPageable(int page, Integer taille, String triPar, String ordreTri) {
        int size = (taille != null && taille > 0 && taille <= 200) ? taille : 20;
        Sort sort = Sort.unsorted();
        if (triPar != null && !triPar.isBlank()) {
            Sort.Direction direction = "asc".equalsIgnoreCase(ordreTri) ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(direction, triPar);
        }
        return PageRequest.of(Math.max(0, page), size, sort);
    }

    private List<ProduitDTO> trierProduits(List<ProduitDTO> liste, String triPar, String ordreTri) {
        Comparator<ProduitDTO> comparator;
        switch (triPar) {
            case "prixVente" -> comparator = Comparator.comparing(
                    ProduitDTO::getPrixVente, Comparator.nullsLast(BigDecimal::compareTo));
            case "ref" -> comparator = Comparator.comparing(
                    ProduitDTO::getRef, Comparator.nullsLast(String::compareTo));
            case "margePourcentage" -> comparator = Comparator.comparing(
                    p -> p.getMargePourcentage() != null ? p.getMargePourcentage() : BigDecimal.ZERO);
            default -> comparator = Comparator.comparing(
                    ProduitDTO::getNom, Comparator.nullsLast(String::compareTo));
        }
        if (!"asc".equalsIgnoreCase(ordreTri)) {
            comparator = comparator.reversed();
        }
        liste.sort(comparator);
        return liste;
    }

    @GetMapping("/{id}")
    public String trouverParId(@PathVariable Long id, Model model) {
        model.addAttribute("produit", produitService.trouverParId(id));
        List<HistoriquePrixProduitDTO> historiquePrix = produitService.listerHistoriquePrix(id);
        model.addAttribute("historiquePrix", historiquePrix);
        return "produit/detail";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        if (!model.containsAttribute("produit")) {
            model.addAttribute("produit", new ProduitRequestDTO());
        }
        return "produit/formulaire";
    }

    @PostMapping
    public String creer(
            @Valid @ModelAttribute("produit") ProduitRequestDTO requete,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "produit/formulaire";
        }

        try {
            produitService.creer(requete);
            redirectAttributes.addFlashAttribute("success", "Produit créé avec succès !");
            return "redirect:/api/produits";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + e.getMessage());
            return "redirect:/api/produits/nouveau";
        }
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("produit")) {
            ProduitDTO produitDTO = produitService.trouverParIdPourModification(id);

            ProduitRequestDTO requestDTO = ProduitRequestDTO.builder()
                    .ref(produitDTO.getRef())
                    .idCategorie(produitDTO.getIdCategorie())
                    .nom(produitDTO.getNom())
                    .prixVente(produitDTO.getPrixVente())
                    .seuilAlerte(produitDTO.getSeuilAlerte())
                    .description(produitDTO.getDescription())
                    .actif(produitDTO.getActif())
                    .build();

            model.addAttribute("produit", requestDTO);
            model.addAttribute("produitId", id);
        }
        return "produit/formulaire";
    }

    @PostMapping("/{id}")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("produit") ProduitRequestDTO requete,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "produit/formulaire";
        }

        try {
            produitService.modifier(id, requete);
            redirectAttributes.addFlashAttribute("success", "Produit modifié avec succès !");
            return "redirect:/api/produits";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            return "redirect:/api/produits/" + id + "/modifier";
        }
    }

    @GetMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitService.supprimer(id);
            redirectAttributes.addFlashAttribute("success", "Produit désactivé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la désactivation : " + e.getMessage());
        }
        return "redirect:/api/produits";
    }

    @GetMapping("/{id}/reactiver")
    public String reactiver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitService.reactiver(id);
            redirectAttributes.addFlashAttribute("success", "Produit réactivé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la réactivation : " + e.getMessage());
        }
        return "redirect:/api/produits";
    }
}