package mg.vinaAkoho.vina_akoho.controller.layout;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LayoutViewController {

    @GetMapping("/layout/header.html")
    public String header() {
        return "layout/header";
    }

    @GetMapping("/layout/footer.html")
    public String footer() {
        return "layout/footer";
    }

    @GetMapping("/layout/{role}.html")
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
