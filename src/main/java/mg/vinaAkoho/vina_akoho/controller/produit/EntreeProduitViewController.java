package mg.vinaAkoho.vina_akoho.controller.produit;

import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/api/production")
public class EntreeProduitViewController {

    private final ProduitRepository produitRepository;

    public EntreeProduitViewController(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    @GetMapping("/entree-produit")
    public String page(Model model) {
        model.addAttribute("produits", produitRepository.findAllActifs());
        return "dashboard/production/entree-produit";
    }
}
