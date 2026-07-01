package mg.vinaAkoho.vina_akoho.controller.stockmp;

import jakarta.validation.Valid;
import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.stockmp.MouvementStockMpDTO;
import mg.vinaAkoho.vina_akoho.dto.stockmp.SortieMpRequestDTO;
import mg.vinaAkoho.vina_akoho.service.stockmp.SortieMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-mp")
public class SortieMpController {

    private final SortieMpService sortieMpService;

    @Autowired
    public SortieMpController(SortieMpService sortieMpService) {
        this.sortieMpService = sortieMpService;
    }

    // POST /api/stock-mp/sortie — déduit automatiquement les MP selon la recette
    @PostMapping("/sortie")
    public ResponseEntity<ApiResponse<List<MouvementStockMpDTO>>> sortie(
            @Valid @RequestBody SortieMpRequestDTO requete) {
        List<MouvementStockMpDTO> result = sortieMpService.effectuerSortie(requete);
        return ResponseEntity.ok(ApiResponse.success("Sortie de matières premières effectuée", result));
    }
}