package mg.vinaAkoho.vina_akoho.controller.login;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mg.vinaAkoho.vina_akoho.dto.login.LoginRequestDTO;
import mg.vinaAkoho.vina_akoho.exception.login.IdentifiantsInvalidesException;
import mg.vinaAkoho.vina_akoho.service.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/login")
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public String login(@RequestParam String email,
                        @RequestParam String mdp,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        LoginRequestDTO requete = new LoginRequestDTO(email, mdp);
        try {
            var reponse = loginService.login(requete, session);
            String role = reponse.getRole();
            if (role != null && role.equalsIgnoreCase("Administrateur")) {
                return "redirect:/admin";
            } else if (role != null && role.equalsIgnoreCase("Responsable achat")) {
                return "redirect:/achats";
            } else if (role != null && role.equalsIgnoreCase("Responsable de production")) {
                return "redirect:/production";
            } else if (role != null && role.equalsIgnoreCase("Gestionnaire de stock")) {
                return "redirect:/stock";
            } else if (role != null && role.equalsIgnoreCase("Responsable commercial")) {
                return "redirect:/commercial";
            } else if (role != null && role.equalsIgnoreCase("Comptable")) {
                return "redirect:/comptabilite";
            } else {
                return "redirect:/matieres-premieres";
            }
        } catch (IdentifiantsInvalidesException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/";
        }
    }
}
