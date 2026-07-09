package mg.vinaAkoho.vina_akoho.controller.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mg.vinaAkoho.vina_akoho.dto.login.LoginRequestDTO;
import mg.vinaAkoho.vina_akoho.exception.login.IdentifiantsInvalidesException;
import mg.vinaAkoho.vina_akoho.security.SessionFilter;
import mg.vinaAkoho.vina_akoho.service.login.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public String login(@RequestParam String email,
                        @RequestParam String mdp,
                        HttpServletRequest request,
                        RedirectAttributes redirectAttributes) {
        LoginRequestDTO requete = new LoginRequestDTO(email, mdp);
        try {
            HttpSession session = request.getSession(true);
            var reponse = loginService.login(requete, session);
            session.setAttribute(SessionFilter.ATTRIBUT_ID_EMPLOYE, reponse.getIdEmploye());
            session.setAttribute(SessionFilter.ATTRIBUT_ROLE, reponse.getRole());
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
            } else {
                return "redirect:/api/matieres-premieres";
            }
        } catch (IdentifiantsInvalidesException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/";
        }
    }
}
