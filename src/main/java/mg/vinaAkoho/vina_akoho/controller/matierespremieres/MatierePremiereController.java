package mg.vinaAkoho.vina_akoho.controller.matierespremieres;

import jakarta.validation.Valid;
import java.util.List;
import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.EntreeStockDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.FicheDetailDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.FournisseurDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.MatierePremiereListDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.MatierePremiereRequestDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.UniteDTO;
import mg.vinaAkoho.vina_akoho.service.matierespremieres.MatierePremiereService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matieres-premieres")
public class MatierePremiereController {

    private final MatierePremiereService service;

    public MatierePremiereController(MatierePremiereService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<MatierePremiereListDTO>> lister() {
        return ApiResponse.success("Liste des matières premières", service.lister());
    }

    @GetMapping("/alertes")
    public ApiResponse<List<MatierePremiereListDTO>> listerAlertes() {
        return ApiResponse.success("Matières premières en alerte", service.listerAlertes());
    }

    @GetMapping("/fournisseurs")
    public ApiResponse<List<FournisseurDTO>> listerFournisseurs() {
        return ApiResponse.success("Liste des fournisseurs", service.listerFournisseurs());
    }

    @GetMapping("/unites")
    public ApiResponse<List<UniteDTO>> listerUnites() {
        return ApiResponse.success("Liste des unités", service.listerUnites());
    }

    @GetMapping("/{id}")
    public ApiResponse<FicheDetailDTO> detail(@PathVariable Integer id) {
        return ApiResponse.success("Fiche matière première", service.detail(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FicheDetailDTO> creer(@Valid @RequestBody MatierePremiereRequestDTO dto) {
        return ApiResponse.success("Matière première créée", service.creer(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<FicheDetailDTO> modifier(@PathVariable Integer id,
                                                @Valid @RequestBody MatierePremiereRequestDTO dto) {
        return ApiResponse.success("Matière première modifiée", service.modifier(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> supprimer(@PathVariable Integer id) {
        service.supprimer(id);
        return ApiResponse.success("Matière première supprimée", null);
    }

    @PostMapping("/entree-stock")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FicheDetailDTO> entreeStock(@Valid @RequestBody EntreeStockDTO dto) {
        return ApiResponse.success("Entrée en stock enregistrée", service.entreeStock(dto));
    }
}
