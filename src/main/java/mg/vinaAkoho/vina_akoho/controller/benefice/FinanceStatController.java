package mg.vinaAkoho.vina_akoho.controller.benefice;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.repository.depense.DepenseRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.VenteRepository;
import mg.vinaAkoho.vina_akoho.service.benefice.BeneficeService; // Import de ton service
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/finances-statistique")
@RequiredArgsConstructor
public class FinanceStatController {

    private final DepenseRepository depenseRepository;
    private final VenteRepository venteRepository;
    private final BeneficeService beneficeService;

    @GetMapping("/depenses-categorie")
    public Map<String, BigDecimal> getDepensesParCategorie() {
        return depenseRepository.sumDepensesParCategorie().stream()
                .collect(Collectors.toMap(obj -> (String)obj[0], obj -> (BigDecimal)obj[1]));
    }

    @GetMapping("/depenses-phase")
    public Map<String, BigDecimal> getDepensesParPhase() {
        return depenseRepository.sumDepensesParPhase().stream()
                .collect(Collectors.toMap(obj -> (String)obj[0], obj -> (BigDecimal)obj[1]));
    }

    @GetMapping("/recettes-mensuelles")
    public Map<String, BigDecimal> getRecettesMensuelles() {
        return venteRepository.getRecettesParMois().stream()
                .collect(Collectors.toMap(obj -> (String)obj[0], obj -> (BigDecimal)obj[1]));
    }

    @GetMapping("/evolution-benefice")
    public Map<String, BigDecimal> getEvolutionBenefice() {
        return beneficeService.getEvolutionBeneficeParMois();
    }
}