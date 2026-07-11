package mg.vinaAkoho.vina_akoho.controller.depense;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.depense.DepenseDTO;
import mg.vinaAkoho.vina_akoho.dto.depense.DepenseRequestDTO;
import mg.vinaAkoho.vina_akoho.service.depense.CategorieDepenseService;
import mg.vinaAkoho.vina_akoho.service.depense.DepenseService;
import mg.vinaAkoho.vina_akoho.service.depense.PhaseService;
import mg.vinaAkoho.vina_akoho.service.depense.StatutDepenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/api/depenses")
@RequiredArgsConstructor
public class DepenseController {

    private final DepenseService depenseService;
    private final CategorieDepenseService categorieDepenseService;
    private final PhaseService phaseService;
    private final StatutDepenseService statutDepenseService;

    @GetMapping("/comptable-depenses")
    public String afficherDepensesComptable(Model model) {
        model.addAttribute("depenses", depenseService.listerToutes());
        model.addAttribute("categoriesDepense", categorieDepenseService.listerToutes());
        model.addAttribute("categorieDepense1", categorieDepenseService.trouverParIdSansException(1));
        model.addAttribute("categorieDepense2", categorieDepenseService.trouverParIdSansException(2));
        model.addAttribute("categorieDepense3", categorieDepenseService.trouverParIdSansException(3));
        model.addAttribute("dateDuJour", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("montantTotalDepenses", depenseService.calculerMontantTotal());
        model.addAttribute("nombreOperationsDepenses", depenseService.compterToutes());
        model.addAttribute("montantCategorieDepense1", depenseService.calculerMontantTotalParCategorie(1));
        model.addAttribute("montantCategorieDepense2", depenseService.calculerMontantTotalParCategorie(2));
        model.addAttribute("montantCategorieDepense3", depenseService.calculerMontantTotalParCategorie(3));
        return "depense/comptable-depenses";
    }

    @GetMapping("/recherche")
    public String rechercherDepenses(
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) Integer idCategorie,
            @RequestParam(required = false) LocalDate dateDu,
            Model model) {

        model.addAttribute("depenses", depenseService.rechercher(motCle, idCategorie, dateDu));
        model.addAttribute("motCle", motCle);
        model.addAttribute("idCategorie", idCategorie);
        model.addAttribute("dateDu", dateDu);
        chargerReferentiels(model);
        model.addAttribute("categorieDepense1", categorieDepenseService.trouverParIdSansException(1));
        model.addAttribute("categorieDepense2", categorieDepenseService.trouverParIdSansException(2));
        model.addAttribute("categorieDepense3", categorieDepenseService.trouverParIdSansException(3));
        model.addAttribute("dateDuJour", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("montantTotalDepenses", depenseService.calculerMontantTotal());
        model.addAttribute("nombreOperationsDepenses", depenseService.compterToutes());
        model.addAttribute("montantCategorieDepense1", depenseService.calculerMontantTotalParCategorie(1));
        model.addAttribute("montantCategorieDepense2", depenseService.calculerMontantTotalParCategorie(2));
        model.addAttribute("montantCategorieDepense3", depenseService.calculerMontantTotalParCategorie(3));
        return "depense/comptable-depenses";
    }

    @GetMapping("/comptable-depenses-nouveau")
    public String afficherNouvelleDepenseComptable(
            @RequestParam(required = false) Integer depenseId,
            Model model) {
        if (depenseId != null) {
            DepenseDTO existant = depenseService.trouverParId(depenseId);
            DepenseRequestDTO dto = new DepenseRequestDTO();
            dto.setId(existant.getId());
            dto.setDate(existant.getDate());
            dto.setDesignation(existant.getDesignation());
            dto.setIdCategorieDepense(existant.getIdCategorieDepense());
            dto.setIdPhase(existant.getIdPhase());
            dto.setMontant(existant.getMontant());
            dto.setIdStatutDepense(existant.getIdStatutDepense());
            model.addAttribute("depense", dto);
            model.addAttribute("modeEdition", true);
        } else if (!model.containsAttribute("depense")) {
            model.addAttribute("depense", new DepenseRequestDTO());
        }
        chargerReferentiels(model);
        return "depense/comptable-depenses-nouveau";
    }

    @PostMapping
    public String creerDepenseComptable(
            @Valid @ModelAttribute("depense") DepenseRequestDTO requete,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            if (requete.getId() != null) {
                model.addAttribute("modeEdition", true);
            }
            chargerReferentiels(model);
            return "depense/comptable-depenses-nouveau";
        }

        if (requete.getId() != null) {
            depenseService.modifier(requete.getId(), requete);
            redirectAttributes.addFlashAttribute("successMessage", "La dépense a été modifiée avec succès.");
        } else {
            depenseService.creer(requete);
            redirectAttributes.addFlashAttribute("successMessage", "La dépense a été enregistrée avec succès.");
        }
        return "redirect:/api/depenses/comptable-depenses";
    }

    private void chargerReferentiels(Model model) {
        model.addAttribute("categoriesDepense", categorieDepenseService.listerToutes());
        model.addAttribute("phases", phaseService.listerToutes());
        model.addAttribute("statutsDepense", statutDepenseService.listerTous());
    }
}
