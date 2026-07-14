package mg.vinaAkoho.vina_akoho.controller.imports;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ImportViewController {

    @GetMapping("/matieres-premieres/import")
    public String pageImportMatierePremiere() {
        return "matieres-premieres/import";
    }

    @GetMapping("/fournisseurs/import")
    public String pageImportFournisseur() {
        return "matieres-premieres/import-fournisseurs";
    }

    @GetMapping("/api/produits/import")
    public String pageImportProduit() {
        return "produit/import";
    }

    @GetMapping("/clients/import")
    public String pageImportClient() {
        return "clients/import";
    }
}