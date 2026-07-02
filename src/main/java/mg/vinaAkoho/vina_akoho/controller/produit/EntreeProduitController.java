package mg.vinaAkoho.vina_akoho.controller.produit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.produit.EntreeProduitRequestDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.EntreeProduitResponseDTO;
import mg.vinaAkoho.vina_akoho.service.produit.EntreeProduitService;

@RestController
@RequestMapping("/api/entrees-produit")
public class EntreeProduitController {

    private final EntreeProduitService entreeProduitService;

    public EntreeProduitController(EntreeProduitService entreeProduitService) {
        this.entreeProduitService = entreeProduitService;
    }

    @PostMapping
    public ApiResponse<EntreeProduitResponseDTO> produire(@Valid @RequestBody EntreeProduitRequestDTO dto) {
        EntreeProduitResponseDTO result = entreeProduitService.produire(dto);
        return ApiResponse.success("Production enregistrée avec succès", result);
    }

    @GetMapping("/stock/{idProduit}")
    public ApiResponse<Object> getStock(@PathVariable Long idProduit) {
        return ApiResponse.success("Stock récupéré", entreeProduitService.getStockDisponible(idProduit));
    }
}
