package mg.vinaAkoho.vina_akoho.controller.produit;

import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
@RequestMapping("/production")
public class EntreeProduitViewController {

    private final ProduitRepository produitRepository;

    public EntreeProduitViewController(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    @GetMapping("/entree-produit")
    public String page(
            @RequestParam(required = false) Long produitId,
            @RequestParam(required = false) java.math.BigDecimal quantite,
            Model model) {
        model.addAttribute("produits", produitRepository.findAllActifs());
        model.addAttribute("produitSelectionne", produitId);
        model.addAttribute("quantiteProposee", quantite);
        return "dashboard/production/entree-produit";
    }
}
