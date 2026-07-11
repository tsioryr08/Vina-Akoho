package mg.vinaAkoho.vina_akoho.controller.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.vinaAkoho.vina_akoho.dto.login.LoginRequestDTO;
import mg.vinaAkoho.vina_akoho.dto.login.LoginResponseDTO;
import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.exception.login.IdentifiantsInvalidesException;
import mg.vinaAkoho.vina_akoho.security.SessionFilter;
import mg.vinaAkoho.vina_akoho.service.login.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String loginForm(@ModelAttribute LoginRequestDTO requete,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        try {
            return authentifierEtRediriger(requete, request);
        } catch (IdentifiantsInvalidesException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginJson(@RequestBody LoginRequestDTO requete,
                                                                   HttpServletRequest request) {
        try {
            LoginResponseDTO reponse = authentifier(requete, request);
            return ResponseEntity.ok(ApiResponse.success("Connexion réussie", reponse));
        } catch (IdentifiantsInvalidesException e) {
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
    }

    private String authentifierEtRediriger(LoginRequestDTO requete, HttpServletRequest request) {
        LoginResponseDTO reponse = authentifier(requete, request);
        String role = reponse.getRole();
        if (role != null && role.equalsIgnoreCase("Administrateur")) {
            return "redirect:/api/admin";
        } else if (role != null && role.equalsIgnoreCase("Responsable achat")) {
            return "redirect:/api/achats";
        } else if (role != null && role.equalsIgnoreCase("Responsable de production")) {
            return "redirect:/api/production";
        } else if (role != null && role.equalsIgnoreCase("Gestionnaire de stock")) {
            return "redirect:/api/stock";
        } else if (role != null && role.equalsIgnoreCase("Responsable commercial")) {
            return "redirect:/api/ventes";
        } else if (role != null && role.equalsIgnoreCase("Comptable")) {
            return "redirect:/api/comptabilite";
        }
        return "redirect:/api/matieres-premieres";
    }

    private LoginResponseDTO authentifier(LoginRequestDTO requete, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        var reponse = loginService.login(requete, session);
        session.setAttribute(SessionFilter.ATTRIBUT_ID_EMPLOYE, reponse.getIdEmploye());
        session.setAttribute(SessionFilter.ATTRIBUT_ROLE, reponse.getRole());
        return reponse;
    }
}
