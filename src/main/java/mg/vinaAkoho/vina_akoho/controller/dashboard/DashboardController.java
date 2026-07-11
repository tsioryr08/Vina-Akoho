package mg.vinaAkoho.vina_akoho.controller.dashboard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.ventes.EvolutionVenteStatDTO;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.repository.login.EmployeRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.LotMpRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.service.matierespremieres.MatierePremiereService;
import mg.vinaAkoho.vina_akoho.service.produit.ProduitService;
import mg.vinaAkoho.vina_akoho.service.ventes.RecetteVenteService;
import mg.vinaAkoho.vina_akoho.service.ventes.StatistiqueVenteService;
import mg.vinaAkoho.vina_akoho.service.prevision.PrevisionProductionService;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final RecetteVenteService recetteVenteService;
    private final ProduitService produitService;
    private final MatierePremiereService matierePremiereService;
    private final LotProduitRepository lotProduitRepository;
    private final LotMpRepository lotMpRepository;
    private final EmployeRepository employeRepository;
    private final PrevisionProductionService previsionProductionService;
    private final StatistiqueVenteService statistiqueVenteService;


    @GetMapping("/admin")
    public String admin(Model model) {
        List<Employe> actifs = employeRepository.findByActif(true);
        model.addAttribute("employes", actifs);
        model.addAttribute("totalActifs", actifs.size());
        model.addAttribute("totalDesactives", employeRepository.findByActif(false).size());

        // Évolution des ventes sur les 30 derniers jours (granularité jour),
        // identique au défaut de la page statistiques pour un rendu cohérent.
        LocalDate debut = LocalDate.now().minusDays(30);
        LocalDate fin = LocalDate.now();
        List<EvolutionVenteStatDTO> evolutionVentes = statistiqueVenteService
                .obtenirStatistiques(debut, fin, null, "montant", "jour")
                .getEvolution();
        model.addAttribute("evolutionVentes", evolutionVentes);

        return "dashboard/admin/index";
    }

    @GetMapping("/achats")
    public String achats() {
        return "dashboard/achats/index";
    }

    @GetMapping("/production")
    public String production(
            @RequestParam(defaultValue = "30") int joursAnalyse,
            @RequestParam(defaultValue = "7") int joursCouverture,
            Model model) {
        LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
        LocalDate aujourdHui = LocalDate.now();
        var recettes = recetteVenteService.listerParPeriode(debutMois, aujourdHui);
        model.addAttribute("recetteMensuelle", recetteVenteService.calculerTotal(recettes));

        // KPIs Responsable Production (stock & lots)
        var produits = produitService.listerTous();
        var mpAlertes = matierePremiereService.listerAlertes();

        // 1) Quantité actuelle produits finis (somme des quantités stock)
        // 5) Nombre produits sous seuil d'alerte
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

        // 2) Quantité actuelle matières premières (somme stocks MP actifs)
        var mps = matierePremiereService.lister();
        java.math.BigDecimal quantiteMatièresPremières = mps.stream()
                .map(mp -> mp.quantiteStock() != null ? mp.quantiteStock() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        String uniteMps = mps.stream()
                .map(mg.vinaAkoho.vina_akoho.dto.matierespremieres.MatierePremiereListDTO::uniteLibelle)
                .filter(u -> u != null && !u.isBlank())
                .findFirst()
                .orElse("Unité");

        // 5) Nombre matières premières sous seuil minimum
        model.addAttribute("mpSousSeuilMin", mpAlertes.size());

        // 3 & 4 & 6 via repositories par lots (ajout dans LotMpRepository + LotProduitRepository)
        model.addAttribute("quantiteProduitsFinis", quantiteProduitsFinis != null ? quantiteProduitsFinis : java.math.BigDecimal.ZERO);
        model.addAttribute("quantiteMatièresPremières", quantiteMatièresPremières != null ? quantiteMatièresPremières : java.math.BigDecimal.ZERO);
        model.addAttribute("produitsSousSeuil", produitsSousSeuil);
        model.addAttribute("lotsProduits", lotProduitRepository.compterLotsProduitsActifs());

        // 4) Lots expirant bientôt (30 derniers jours => fenêtre glissante)
        LocalDate debut = LocalDate.now();
        LocalDate fin = debut.plusDays(30);
        model.addAttribute("lotsExpirantBientot", lotMpRepository.compterLotsExpirantBientot(debut, fin));

        model.addAttribute("uniteProduits", uniteProduits);
        model.addAttribute("uniteMps", uniteMps);

        int analyse = Math.max(1, Math.min(365, joursAnalyse));
        int couverture = Math.max(1, Math.min(365, joursCouverture));
        var previsionsProduction = previsionProductionService.calculerProductions(analyse, couverture);
        model.addAttribute("previsionsProduction", previsionsProduction);
        model.addAttribute("previsionsMp",
                previsionProductionService.calculerMatieresPremieres(previsionsProduction));
        model.addAttribute("joursAnalyse", analyse);
        model.addAttribute("joursCouverture", couverture);
        model.addAttribute("dateDebutPrevision", aujourdHui.minusDays(analyse - 1L));
        model.addAttribute("dateFinPrevision", aujourdHui);

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
