package mg.vinaAkoho.vina_akoho.controller.recetteproduit;

import jakarta.validation.Valid;
import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.recetteproduit.CreateRecetteProduitDTO;
import mg.vinaAkoho.vina_akoho.dto.recetteproduit.RecetteProduitDTO;
import mg.vinaAkoho.vina_akoho.service.recetteproduit.RecetteProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recettes")
public class RecetteProduitController {

    private final RecetteProduitService recetteProduitService;

    @Autowired
    public RecetteProduitController(RecetteProduitService recetteProduitService) {
        this.recetteProduitService = recetteProduitService;
    }

    // POST /api/recettes — créer ou mettre à jour une recette
    @PostMapping
    public ResponseEntity<ApiResponse<List<RecetteProduitDTO>>> creer(
            @Valid @RequestBody CreateRecetteProduitDTO dto) {
        List<RecetteProduitDTO> result = recetteProduitService.creerRecette(dto);
        return ResponseEntity.ok(ApiResponse.success("Recette créée avec succès", result));
    }

    // GET /api/recettes/categorie/{id} — lire la recette active d'une catégorie
    @GetMapping("/categorie/{idCategorie}")
    public ResponseEntity<ApiResponse<List<RecetteProduitDTO>>> getRecetteActive(
            @PathVariable Integer idCategorie) {
        List<RecetteProduitDTO> result = recetteProduitService.getRecetteActive(idCategorie);
        return ResponseEntity.ok(ApiResponse.success("Recette trouvée", result));
    }
}
