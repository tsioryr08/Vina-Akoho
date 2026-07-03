package mg.vinaAkoho.vina_akoho.controller.dashboard;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.service.ventes.RecetteVenteService;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final RecetteVenteService recetteVenteService;

    @GetMapping("/admin")
    public String admin() {
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
    public String stock() {
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
