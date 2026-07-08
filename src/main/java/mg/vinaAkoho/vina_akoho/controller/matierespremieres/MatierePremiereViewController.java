package mg.vinaAkoho.vina_akoho.controller.matierespremieres;

import java.math.BigDecimal;
import java.time.LocalDate;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.EntreeStockDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.MatierePremiereListDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.MatierePremiereRequestDTO;
import mg.vinaAkoho.vina_akoho.service.matierespremieres.MatierePremiereService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/matieres-premieres")
public class MatierePremiereViewController {

    private final MatierePremiereService service;

    public MatierePremiereViewController(MatierePremiereService service) {
        this.service = service;
    }

    @GetMapping
    public String liste(Model model) {
        var mps = service.lister();
        model.addAttribute("mps", mps);
        BigDecimal totalStock = mps.stream()
                .map(MatierePremiereListDTO::quantiteStock)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalStock", totalStock);
        return "matieres-premieres/liste";
    }

    @GetMapping("/alertes")
    public String alertes(Model model) {
        model.addAttribute("alertes", service.listerAlertes());
        return "matieres-premieres/alertes";
    }

    @GetMapping("/nouveau")
    public String formulaire(Model model) {
        model.addAttribute("fournisseurs", service.listerFournisseurs());
        model.addAttribute("unites", service.listerUnites());
        return "matieres-premieres/formulaire";
    }

    @PostMapping("/nouveau")
    public String creer(
            @RequestParam String nom,
            @RequestParam Integer idFournisseur,
            @RequestParam BigDecimal coutUnitaire,
            @RequestParam Integer idUnite,
            @RequestParam(required = false) BigDecimal seuilMinimum,
            RedirectAttributes redirectAttributes) {
        try {
            var fiche = service
                    .creer(new MatierePremiereRequestDTO(nom, idFournisseur, coutUnitaire, idUnite, seuilMinimum));
            redirectAttributes.addFlashAttribute("successMessage", "Matière première créée avec succès");
            return "redirect:/api/matieres-premieres/" + fiche.id();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/api/matieres-premieres/nouveau";
        }
    }

    @GetMapping("/{id}")
    public String fiche(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("fiche", service.detail(id));
            return "matieres-premieres/fiche";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/api/matieres-premieres";
        }
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("fiche", service.detail(id));
            model.addAttribute("fournisseurs", service.listerFournisseurs());
            model.addAttribute("unites", service.listerUnites());
            return "matieres-premieres/formulaire-modification";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/api/matieres-premieres";
        }
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Integer id,
            @RequestParam String nom,
            @RequestParam Integer idFournisseur,
            @RequestParam BigDecimal coutUnitaire,
            @RequestParam Integer idUnite,
            @RequestParam(required = false) BigDecimal seuilMinimum,
            RedirectAttributes redirectAttributes) {
        try {
            service.modifier(id,
                    new MatierePremiereRequestDTO(nom, idFournisseur, coutUnitaire, idUnite, seuilMinimum));
            redirectAttributes.addFlashAttribute("successMessage", "Matière première modifiée avec succès");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/api/matieres-premieres/" + id;
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.supprimer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Matière première supprimée avec succès");
            return "redirect:/api/matieres-premieres";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/api/matieres-premieres/" + id;
        }
    }

    @GetMapping("/entree-stock")
    public String entreeStockForm(Model model) {
        model.addAttribute("mps", service.lister());
        return "matieres-premieres/entree-stock";
    }

    @PostMapping("/entree-stock")
    public String entreeStock(
            @RequestParam Integer idMatierePremiere,
            @RequestParam BigDecimal quantite,
            @RequestParam BigDecimal coutUnitaire,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReception,
            RedirectAttributes redirectAttributes) {
        try {
            service.entreeStock(new EntreeStockDTO(idMatierePremiere, quantite, dateReception, 1, coutUnitaire));
            redirectAttributes.addFlashAttribute("successMessage", "Entrée en stock enregistrée avec succès");
            return "redirect:/api/matieres-premieres/" + idMatierePremiere;
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/api/matieres-premieres/entree-stock";
        }
    }
}
