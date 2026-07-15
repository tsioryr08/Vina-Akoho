package mg.vinaAkoho.vina_akoho.controller.produit;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.produit.CategorieDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.CategorieRequestDTO;
import mg.vinaAkoho.vina_akoho.exception.produit.CategorieDejaExistanteException;
import mg.vinaAkoho.vina_akoho.exception.produit.CategorieEnUtilisationException;
import mg.vinaAkoho.vina_akoho.exception.produit.CategorieNotFoundException;
import mg.vinaAkoho.vina_akoho.service.produit.CategorieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    @GetMapping
    public String listerToutes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Integer taille,
            @RequestParam(required = false) String triPar,
            @RequestParam(required = false, defaultValue = "desc") String ordreTri,
            Model model) {

        Pageable pageable = buildPageable(page, taille, triPar, ordreTri);

        Page<CategorieDTO> categories;

        if (search != null && !search.trim().isEmpty()) {
            categories = categorieService.rechercher(search.trim(), pageable);
        } else {
            categories = categorieService.listerToutes(pageable);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("totalElements", categories.getTotalElements());
        model.addAttribute("totalPages", categories.getTotalPages());
        model.addAttribute("currentPage", categories.getNumber());
        model.addAttribute("search", search);
        model.addAttribute("taille", taille);
        model.addAttribute("triPar", triPar);
        model.addAttribute("ordreTri", ordreTri);

        return "categorie/list";
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

    @GetMapping("/{id}")
    public String trouverParId(@PathVariable Long id, Model model) {
        model.addAttribute("categorie", categorieService.trouverParId(id));
        return "categorie/detail";
    }

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        if (!model.containsAttribute("categorie")) {
            model.addAttribute("categorie", new CategorieRequestDTO());
        }
        return "categorie/formulaire";
    }

    @PostMapping
    public String creer(
            @Valid @ModelAttribute("categorie") CategorieRequestDTO requete,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "categorie/formulaire";
        }

        try {
            categorieService.creer(requete);
            redirectAttributes.addFlashAttribute("success", "Catégorie créée avec succès.");
            return "redirect:/api/categories";
        } catch (CategorieDejaExistanteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/categories/nouveau";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + e.getMessage());
            return "redirect:/api/categories/nouveau";
        }
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("categorie")) {
            CategorieDTO categorieDTO = categorieService.trouverParIdPourModification(id);

            CategorieRequestDTO requestDTO = CategorieRequestDTO.builder()
                    .libelle(categorieDTO.getLibelle())
                    .description(categorieDTO.getDescription())
                    .pourcentageProteine(categorieDTO.getPourcentageProteine())
                    .pourcentageMatiereGrasses(categorieDTO.getPourcentageMatiereGrasses())
                    .pourcentageHumiditeMax(categorieDTO.getPourcentageHumiditeMax())
                    .margePourcentage(categorieDTO.getMargePourcentage())
                    .actif(categorieDTO.getActif())
                    .build();

            model.addAttribute("categorie", requestDTO);
            model.addAttribute("categorieId", id);
        }

        return "categorie/formulaire";
    }

    @PostMapping("/{id}")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("categorie") CategorieRequestDTO requete,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "categorie/formulaire";
        }

        try {
            categorieService.modifier(id, requete);
            redirectAttributes.addFlashAttribute("success", "Catégorie modifiée avec succès.");
            return "redirect:/api/categories";
        } catch (CategorieDejaExistanteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/categories/" + id + "/modifier";
        } catch (CategorieNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Catégorie non trouvée.");
            return "redirect:/api/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            return "redirect:/api/categories/" + id + "/modifier";
        }
    }

    @GetMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categorieService.supprimer(id);
            redirectAttributes.addFlashAttribute("success", "Catégorie désactivée avec succès.");
        } catch (CategorieNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Catégorie non trouvée.");
        } catch (CategorieEnUtilisationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la désactivation : " + e.getMessage());
        }

        return "redirect:/categories";
    }

    @GetMapping("/{id}/reactiver")
    public String reactiver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categorieService.reactiver(id);
            redirectAttributes.addFlashAttribute("success", "Catégorie réactivée avec succès.");
        } catch (CategorieNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Catégorie non trouvée.");
        } catch (CategorieDejaExistanteException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la réactivation : " + e.getMessage());
        }

        return "redirect:/categories";
    }
}