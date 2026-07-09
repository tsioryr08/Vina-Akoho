package mg.vinaAkoho.vina_akoho.controller.matierespremieres;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.EntreeStockDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.FicheDetailDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.MatierePremiereListDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.MatierePremiereRequestDTO;
import mg.vinaAkoho.vina_akoho.service.matierespremieres.MatierePremiereService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matieres-premieres")
public class MatierePremiereApiController {

    private final MatierePremiereService service;

    public MatierePremiereApiController(MatierePremiereService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MatierePremiereListDTO>>> lister() {
        return ResponseEntity.ok(ApiResponse.success("Matières premières récupérées", service.lister()));
    }

    @GetMapping("/alertes")
    public ResponseEntity<ApiResponse<List<MatierePremiereListDTO>>> listerAlertes() {
        return ResponseEntity.ok(ApiResponse.success("Alertes matières premières récupérées", service.listerAlertes()));
    }

    @GetMapping("/nouveau")
    public ResponseEntity<Void> redirigerAncienLienCreation() {
        return ResponseEntity.status(302).location(URI.create("/matieres-premieres/nouveau")).build();
    }

    @GetMapping("/entree-stock")
    public ResponseEntity<Void> redirigerAncienLienEntreeStock() {
        return ResponseEntity.status(302).location(URI.create("/matieres-premieres/entree-stock")).build();
    }

    @GetMapping("/fiche")
    public ResponseEntity<Void> redirigerAncienLienFiche() {
        return ResponseEntity.status(302).location(URI.create("/matieres-premieres/nouveau")).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FicheDetailDTO>> detail(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Fiche matière première récupérée", service.detail(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FicheDetailDTO>> creer(@Valid @RequestBody MatierePremiereRequestDTO requete) {
        return ResponseEntity.ok(ApiResponse.success("Matière première créée", service.creer(requete)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FicheDetailDTO>> modifier(
            @PathVariable Integer id,
            @Valid @RequestBody MatierePremiereRequestDTO requete) {
        return ResponseEntity.ok(ApiResponse.success("Matière première modifiée", service.modifier(id, requete)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(@PathVariable Integer id) {
        service.supprimer(id);
        return ResponseEntity.ok(ApiResponse.success("Matière première supprimée", null));
    }

    @PostMapping("/entree-stock")
    public ResponseEntity<ApiResponse<FicheDetailDTO>> entreeStock(@Valid @RequestBody EntreeStockDTO requete) {
        return ResponseEntity.ok(ApiResponse.success("Entrée en stock enregistrée", service.entreeStock(requete)));
    }
}
