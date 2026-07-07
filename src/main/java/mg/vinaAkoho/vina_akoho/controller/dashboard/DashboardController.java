package mg.vinaAkoho.vina_akoho.controller.dashboard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.service.matierespremieres.MatierePremiereService;
import mg.vinaAkoho.vina_akoho.service.produit.ProduitService;
import mg.vinaAkoho.vina_akoho.service.ventes.RecetteVenteService;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final RecetteVenteService recetteVenteService;
    private final ProduitService produitService;
    private final MatierePremiereService matierePremiereService;
    private final EmployeRepository employeRepository;

    @GetMapping("/admin")
    public String admin(Model model) {
        List<Employe> actifs = employeRepository.findByActif(true);
        model.addAttribute("employes", actifs);
        model.addAttribute("totalActifs", actifs.size());
        model.addAttribute("totalDesactives", employeRepository.findByActif(false).size());
        return "dashboard/admin/index";
    }

    @GetMapping("/achats")
    public String achats() {
        return "dashboard/achats/index";
    }

    @GetMapping("/production")
    public String production(Model model) {
        LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
        LocalDate aujourdHui = LocalDate.now();
        var recettes = recetteVenteService.listerParPeriode(debutMois, aujourdHui);
        model.addAttribute("recetteMensuelle", recetteVenteService.calculerTotal(recettes));
        return "dashboard/production/index";
    }

    @GetMapping("/stock")
    public String stock(Model model) {
        model.addAttribute("alertesMp", matierePremiereService.listerAlertes());
        model.addAttribute("produits", produitService.listerTous());
        return "dashboard/stock/index";
    }

    @GetMapping("/commercial")
    public String commercial() {
        return "dashboard/commercial/index";
    }

    @GetMapping("/comptabilite")
    public String comptabilite() {
        return "dashboard/comptabilite/index";
    }
}
