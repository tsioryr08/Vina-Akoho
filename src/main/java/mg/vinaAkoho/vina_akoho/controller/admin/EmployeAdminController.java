package mg.vinaAkoho.vina_akoho.controller.admin;

import jakarta.validation.Valid;
import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.admin.CreerEmployeDTO;
import mg.vinaAkoho.vina_akoho.dto.admin.EmployeAdminDTO;
import mg.vinaAkoho.vina_akoho.dto.admin.ModifierEmployeDTO;
import mg.vinaAkoho.vina_akoho.dto.admin.ReinitialisationMdpDTO;
import mg.vinaAkoho.vina_akoho.service.admin.EmployeAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employes")
public class EmployeAdminController {

    private final EmployeAdminService employeAdminService;

    @Autowired
    public EmployeAdminController(EmployeAdminService employeAdminService) {
        this.employeAdminService = employeAdminService;
    }

    // GET /api/admin/employes/actifs
    @GetMapping("/actifs")
    public ResponseEntity<ApiResponse<List<EmployeAdminDTO>>> listerActifs() {
        return ResponseEntity.ok(ApiResponse.success(
                "Liste des employés actifs", employeAdminService.listerActifs()));
    }

    // GET /api/admin/employes/desactives
    @GetMapping("/desactives")
    public ResponseEntity<ApiResponse<List<EmployeAdminDTO>>> listerDesactives() {
        return ResponseEntity.ok(ApiResponse.success(
                "Liste des employés désactivés", employeAdminService.listerDesactives()));
    }

    // GET /api/admin/employes/recherche?terme=Jean
    @GetMapping("/recherche")
    public ResponseEntity<ApiResponse<List<EmployeAdminDTO>>> rechercher(
            @RequestParam String terme,
            @RequestParam(required = false) Boolean actif) {
        List<EmployeAdminDTO> resultat = (actif != null)
                ? employeAdminService.rechercherParStatut(terme, actif)
                : employeAdminService.rechercher(terme);
        return ResponseEntity.ok(ApiResponse.success("Résultats de recherche", resultat));
    }

    // GET /api/admin/employes/par-role?idRole=2
    @GetMapping("/par-role")
    public ResponseEntity<ApiResponse<List<EmployeAdminDTO>>> listerParRole(
            @RequestParam Integer idRole,
            @RequestParam(required = false) Boolean actif) {
        List<EmployeAdminDTO> resultat = (actif != null)
                ? employeAdminService.listerParRoleEtStatut(idRole, actif)
                : employeAdminService.listerParRole(idRole);
        return ResponseEntity.ok(ApiResponse.success("Employés filtrés par rôle", resultat));
    }

    // POST /api/admin/employes
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeAdminDTO>> creer(
            @Valid @RequestBody CreerEmployeDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(
                "Compte employé créé avec succès", employeAdminService.creer(dto)));
    }

    // PUT /api/admin/employes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeAdminDTO>> modifier(
            @PathVariable Integer id,
            @Valid @RequestBody ModifierEmployeDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(
                "Compte employé modifié avec succès", employeAdminService.modifier(id, dto)));
    }

    // PATCH /api/admin/employes/{id}/desactiver
    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<ApiResponse<EmployeAdminDTO>> desactiver(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Employé désactivé", employeAdminService.desactiver(id)));
    }

    // PATCH /api/admin/employes/{id}/reactiver
    @PatchMapping("/{id}/reactiver")
    public ResponseEntity<ApiResponse<EmployeAdminDTO>> reactiver(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Employé réactivé", employeAdminService.reactiver(id)));
    }

    // PATCH /api/admin/employes/{id}/reinitialiser-mdp
    @PatchMapping("/{id}/reinitialiser-mdp")
    public ResponseEntity<ApiResponse<Void>> reinitialiserMdp(
            @PathVariable Integer id,
            @Valid @RequestBody ReinitialisationMdpDTO dto) {
        employeAdminService.reinitialiserMdp(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Mot de passe réinitialisé avec succès", null));
    }
}