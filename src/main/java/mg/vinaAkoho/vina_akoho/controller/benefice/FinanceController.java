package mg.vinaAkoho.vina_akoho.controller.benefice;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.vinaAkoho.vina_akoho.dto.benefice.RapportBeneficeDTO;
import mg.vinaAkoho.vina_akoho.service.benefice.BeneficeService;

import java.time.LocalDate;

@Controller
@RequestMapping("/api/benefices")
public class FinanceController {
    private final BeneficeService beneficeService;
    public FinanceController(BeneficeService beneficeService) {
        this.beneficeService = beneficeService;
    }
    @GetMapping
    public String afficherRapportBenefices(
        @RequestParam(value = "dateDebut", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
        @RequestParam(value = "dateFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
        @RequestParam(value = "categorieId", required = false) Integer categorieId,
        Model model) {

        if (dateDebut == null) dateDebut = LocalDate.now().withDayOfMonth(1);
        if (dateFin == null) dateFin = LocalDate.now();

        if (dateDebut.isAfter(dateFin)) {
        model.addAttribute("erreur", "La date de début ne peut pas être ultérieure à la date de fin.");
        
        RapportBeneficeDTO rapportErreur = new RapportBeneficeDTO(
            dateDebut, dateFin, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO
        );
        rapportErreur.setCategorieId(categorieId);
        try {
            rapportErreur.setCategories(beneficeService.calculerBeneficeParPeriodeEtCategorie(dateDebut, dateFin, categorieId).getCategories());
        } catch(Exception e) {
        }
        
        model.addAttribute("rapport", rapportErreur);
        return "benefice/benefice";
    }

    RapportBeneficeDTO rapport = beneficeService.calculerBeneficeParPeriodeEtCategorie(dateDebut, dateFin, categorieId);
    model.addAttribute("rapport", rapport);
    
    return "benefice/benefice";
}

    @GetMapping("/dashboard")
    public String afficherDashboardFinancier() {
        return "benefice/statistique_finance";
    }

    
}