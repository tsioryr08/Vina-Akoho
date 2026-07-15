package mg.vinaAkoho.vina_akoho.controller.produit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.produit.HistoriqueDataDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.PaginationDTO;
import mg.vinaAkoho.vina_akoho.dto.prevision.PrevisionMatierePremiereDTO;
import mg.vinaAkoho.vina_akoho.dto.prevision.PrevisionProductionDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.ProductionDashboardDataDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.ProductionHistoriqueItemDTO;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.LotMpRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.service.matierespremieres.MatierePremiereService;
import mg.vinaAkoho.vina_akoho.service.produit.ProduitService;
import mg.vinaAkoho.vina_akoho.service.produit.ProductionHistoriqueService;
import mg.vinaAkoho.vina_akoho.service.ventes.RecetteVenteService;
import mg.vinaAkoho.vina_akoho.service.prevision.PrevisionProductionService;

@Controller
@RequestMapping("/api/production")
@RequiredArgsConstructor
public class EntreeProduitViewController {

    private final ProduitRepository produitRepository;
    private final ProductionHistoriqueService productionHistoriqueService;
    private final ProduitService produitService;
    private final MatierePremiereService matierePremiereService;
    private final RecetteVenteService recetteVenteService;
    private final PrevisionProductionService previsionProductionService;
    private final LotProduitRepository lotProduitRepository;
    private final LotMpRepository lotMpRepository;

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

    private record ProductionKpis(
            java.math.BigDecimal quantiteProduitsFinis,
            java.math.BigDecimal quantiteMatièresPremières,
            long produitsSousSeuil,
            String uniteProduits,
            String uniteMps,
            long mpSousSeuilMin
    ) {}

    private ProductionKpis calculerKpisProduction() {
        var produits = produitService.listerTous();
        var mpAlertes = matierePremiereService.listerAlertes();

        long produitsSousSeuil = produits.stream()
                .filter(p -> p.getStatut() != null && p.getStatut().equals("SEUIL ATTEINT"))
                .count();

        java.math.BigDecimal quantiteProduitsFinis = produits.stream()
                .map(p -> p.getQuantiteStock() != null ? p.getQuantiteStock() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        String uniteProduits = produits.stream()
                .map(mg.vinaAkoho.vina_akoho.dto.produit.ProduitDTO::getLibelleUnite)
                .filter(u -> u != null && !u.isBlank())
                .findFirst()
                .orElse("Unité");

        var mps = matierePremiereService.lister();
        java.math.BigDecimal quantiteMatièresPremières = mps.stream()
                .map(mp -> mp.quantiteStock() != null ? mp.quantiteStock() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        String uniteMps = mps.stream()
                .map(mg.vinaAkoho.vina_akoho.dto.matierespremieres.MatierePremiereListDTO::uniteLibelle)
                .filter(u -> u != null && !u.isBlank())
                .findFirst()
                .orElse("Unité");

        return new ProductionKpis(
                quantiteProduitsFinis != null ? quantiteProduitsFinis : java.math.BigDecimal.ZERO,
                quantiteMatièresPremières != null ? quantiteMatièresPremières : java.math.BigDecimal.ZERO,
                produitsSousSeuil,
                uniteProduits,
                uniteMps,
                mpAlertes.size()
        );
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

    @GetMapping(value = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProductionDashboardDataDTO productionData(
            @RequestParam(defaultValue = "30") int joursAnalyse,
            @RequestParam(defaultValue = "7") int joursCouverture) {
        LocalDate aujourdHui = LocalDate.now();
        int analyse = Math.max(1, Math.min(365, joursAnalyse));
        int couverture = Math.max(1, Math.min(365, joursCouverture));
        var previsionsProduction = previsionProductionService.calculerProductions(analyse, couverture);

        ProductionKpis kpis = calculerKpisProduction();

        return new ProductionDashboardDataDTO(
                kpis.quantiteProduitsFinis(),
                kpis.quantiteMatièresPremières(),
                lotProduitRepository.compterLotsProduitsActifs(),
                lotMpRepository.compterLotsExpirantBientot(LocalDate.now(), LocalDate.now().plusDays(30)),
                kpis.produitsSousSeuil(),
                kpis.mpSousSeuilMin(),
                kpis.uniteProduits(),
                kpis.uniteMps(),
                previsionsProduction,
                previsionProductionService.calculerMatieresPremieres(previsionsProduction),
                analyse,
                couverture,
                aujourdHui.minusDays(analyse - 1L),
                aujourdHui
        );
    }

    @GetMapping(value = "/historique/data", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HistoriqueDataDTO historiqueData(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) Long produitId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate dateDebut,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate dateFin,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer taille,
            @RequestParam(required = false) Integer highlight,
            @RequestParam(required = false) String success) {

        Page<ProductionHistoriqueItemDTO> historiques = productionHistoriqueService.lister(
                recherche, produitId, dateDebut, dateFin, page, taille);

        return new HistoriqueDataDTO(
                historiques.getContent(),
                new PaginationDTO(
                        historiques.getNumber(),
                        historiques.getTotalPages(),
                        historiques.getTotalElements(),
                        historiques.getSize()
                ),
                highlight,
                success,
                produitService.listerTous()
        );
    }

    @GetMapping("/historique/{idFabrication}")
    public String detailProduction(@PathVariable Integer idFabrication, Model model) {
        model.addAttribute("production", productionHistoriqueService.trouverParId(idFabrication));
        return "dashboard/production/detail-production";
    }
}
