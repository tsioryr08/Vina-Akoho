package mg.vinaAkoho.vina_akoho.controller.finance;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.vinaAkoho.vina_akoho.service.finance.RapportFinancierService;

/**
 * Module Finance (Sprint 3 - Responsable Financier). Isolé dans son propre
 * package/dossier (controller.finance / service.finance / dto.finance /
 * templates/finance) pour ne pas toucher aux fichiers du module Bénéfices
 * existant (controller.benefice, propriété de Nekena).
 */
@Controller
@RequestMapping("/finances")
public class RapportFinancierController {

    private final RapportFinancierService rapportFinancierService;

    public RapportFinancierController(RapportFinancierService rapportFinancierService) {
        this.rapportFinancierService = rapportFinancierService;
    }

    @GetMapping("/rapport-mensuel")
    public String afficherRapportMensuel(
            @RequestParam(value = "nombreMois", required = false, defaultValue = "6") int nombreMois,
            Model model) {
        model.addAttribute("rapportMensuel", rapportFinancierService.calculerRapportFinancierMensuel(nombreMois));
        return "finance/rapport-mensuel";
    }
}
