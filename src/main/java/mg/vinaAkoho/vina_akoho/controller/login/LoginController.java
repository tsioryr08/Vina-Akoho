package mg.vinaAkoho.vina_akoho.controller.login;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mg.vinaAkoho.vina_akoho.dto.login.LoginRequestDTO;
import mg.vinaAkoho.vina_akoho.service.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/login")
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping
    public String afficherForm() {
        return "redirect:/";
    }

    @PostMapping
    public String login(@Valid @ModelAttribute("login") LoginRequestDTO requete,
                        BindingResult bindingResult,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("erreur",
                    bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return "redirect:/";
        }

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

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Email ou mot de passe incorrect");
            return "redirect:/";
        }
    }
}

