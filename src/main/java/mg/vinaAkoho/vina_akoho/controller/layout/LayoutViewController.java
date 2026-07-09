package mg.vinaAkoho.vina_akoho.controller.layout;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/layout")
public class LayoutViewController {

    @GetMapping("/header.html")
    public String header() {
        return "layout/header";
    }

    @GetMapping("/footer.html")
    public String footer() {
        return "layout/footer";
    }

    @GetMapping("/{role}.html")
    public String sidebar(@PathVariable String role) {
        return switch (role) {
            case "admin" -> "layout/admin";
            case "responsableAchat" -> "layout/responsableAchat";
            case "responsableProduction" -> "layout/responsableProduction";
            case "gestionnaireStock" -> "layout/gestionnaireStock";
            case "responsableCommercial" -> "layout/responsableCommercial";
            case "comptable" -> "layout/comptable";
            case "livreur" -> "layout/livreur";
            default -> "layout/sidebar";
        };
    }
}
