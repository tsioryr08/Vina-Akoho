package mg.vinaAkoho.vina_akoho.controller.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/admin")
    public String admin() {
        return "dashboard/admin/index";
    }

    @GetMapping("/achats")
    public String achats() {
        return "dashboard/achats/index";
    }

    @GetMapping("/production")
    public String production() {
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
