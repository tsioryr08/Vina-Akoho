package mg.vinaAkoho.vina_akoho.controller.prevision;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.prevision.PrevisionMatierePremiereDTO;
import mg.vinaAkoho.vina_akoho.dto.prevision.PrevisionProductionDTO;
import mg.vinaAkoho.vina_akoho.service.prevision.PrevisionProductionService;

@RestController
@RequestMapping("/api/previsions")
@RequiredArgsConstructor
public class PrevisionApiController {

    private final PrevisionProductionService previsionProductionService;

    @GetMapping("/production")
    public ApiResponse<List<PrevisionProductionDTO>> productions(
            @RequestParam(defaultValue = "30") int joursAnalyse,
            @RequestParam(defaultValue = "7") int joursCouverture) {
        return ApiResponse.success(
                "Prévisions de production calculées",
                previsionProductionService.calculerProductions(joursAnalyse, joursCouverture));
    }

    @GetMapping("/matieres-premieres")
    public ApiResponse<List<PrevisionMatierePremiereDTO>> matieresPremieres(
            @RequestParam(defaultValue = "30") int joursAnalyse,
            @RequestParam(defaultValue = "7") int joursCouverture) {
        List<PrevisionProductionDTO> productions =
                previsionProductionService.calculerProductions(joursAnalyse, joursCouverture);
        return ApiResponse.success(
                "Prévisions de matières premières calculées",
                previsionProductionService.calculerMatieresPremieres(productions));
    }
}
