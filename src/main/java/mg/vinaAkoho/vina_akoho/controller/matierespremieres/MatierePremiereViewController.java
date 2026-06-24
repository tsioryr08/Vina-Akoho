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

@Controller
@RequestMapping("/matieres-premieres")
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
            @RequestParam(required = false) BigDecimal seuilMinimum) {
        service.creer(new MatierePremiereRequestDTO(nom, idFournisseur, coutUnitaire, idUnite, seuilMinimum));
        return "redirect:/matieres-premieres";
    }

    @GetMapping("/{id}")
    public String fiche(@PathVariable Integer id, Model model) {
        model.addAttribute("fiche", service.detail(id));
        return "matieres-premieres/fiche";
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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReception) {
        // idEmploye = 1 en attendant le module F0 Login
        service.entreeStock(new EntreeStockDTO(idMatierePremiere, quantite, dateReception, 1));
        return "redirect:/matieres-premieres/" + idMatierePremiere;
    }
}
