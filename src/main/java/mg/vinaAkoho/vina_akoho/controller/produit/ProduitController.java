package mg.vinaAkoho.vina_akoho.controller.produit;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.produit.ProduitDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.ProduitRequestDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.CategorieDTO;
import mg.vinaAkoho.vina_akoho.service.produit.ProduitService;
import mg.vinaAkoho.vina_akoho.service.produit.CategorieService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("api/produits")
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
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {

        List<ProduitDTO> produitsList = produitService.listerTous();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), produitsList.size());

        Page<ProduitDTO> produits;
        if (start <= produitsList.size()) {
            produits = new PageImpl<>(
                    produitsList.subList(start, end),
                    pageable,
                    produitsList.size()
            );
        } else {
            produits = new PageImpl<>(List.of(), pageable, produitsList.size());
        }

        model.addAttribute("produits", produits);
        model.addAttribute("totalElements", produits.getTotalElements());
        model.addAttribute("totalPages", produits.getTotalPages());
        model.addAttribute("currentPage", produits.getNumber());

        return "produit/list";
    }

    @GetMapping("/recherche")
    public String rechercher(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long idCategorie,
            @RequestParam(required = false) BigDecimal prixMin,
            @RequestParam(required = false) BigDecimal prixMax,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {

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

        return "produit/list";
    }

    @GetMapping("/{id}")
    public String trouverParId(@PathVariable Long id, Model model) {
        model.addAttribute("produit", produitService.trouverParId(id));
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
            return "redirect:/produits";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + e.getMessage());
            return "redirect:/produits/nouveau";
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
            return "redirect:/produits";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            return "redirect:/produits/" + id + "/modifier";
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
        return "redirect:/produits";
    }

    @GetMapping("/{id}/reactiver")
    public String reactiver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitService.reactiver(id);
            redirectAttributes.addFlashAttribute("success", "Produit réactivé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la réactivation : " + e.getMessage());
        }
        return "redirect:/produits";
    }
}