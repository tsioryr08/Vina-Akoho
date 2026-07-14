package mg.vinaAkoho.vina_akoho.controller.produit;

import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.service.produit.ProductionHistoriqueService;
import mg.vinaAkoho.vina_akoho.dto.produit.ProductionHistoriqueItemDTO;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/api/production")
public class EntreeProduitViewController {

    private final ProduitRepository produitRepository;
    private final ProductionHistoriqueService productionHistoriqueService;

    public EntreeProduitViewController(ProduitRepository produitRepository,
                                       ProductionHistoriqueService productionHistoriqueService) {
        this.produitRepository = produitRepository;
        this.productionHistoriqueService = productionHistoriqueService;
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

    @GetMapping("/historique")
    public String historique(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) Long produitId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate dateDebut,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate dateFin,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer taille,
            @RequestParam(required = false) Integer highlight,
            @RequestParam(required = false) String success,
            Model model) {

        Page<ProductionHistoriqueItemDTO> historiques = productionHistoriqueService.lister(
                recherche,
                produitId,
                dateDebut,
                dateFin,
                page,
                taille);

        model.addAttribute("productions", historiques.getContent());
        model.addAttribute("currentPage", historiques.getNumber());
        model.addAttribute("totalPages", historiques.getTotalPages());
        model.addAttribute("pageSize", historiques.getSize());
        model.addAttribute("totalElements", historiques.getTotalElements());
        model.addAttribute("highlightProductionId", highlight);
        model.addAttribute("success", success);
        model.addAttribute("produits", produitRepository.findAllActifs());
        return "dashboard/production/historique";
    }

    @GetMapping("/historique/{idFabrication}")
    public String detailProduction(@PathVariable Integer idFabrication, Model model) {
        model.addAttribute("production", productionHistoriqueService.trouverParId(idFabrication));
        return "dashboard/production/detail-production";
    }
}
