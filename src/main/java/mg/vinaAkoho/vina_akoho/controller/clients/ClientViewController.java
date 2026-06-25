package mg.vinaAkoho.vina_akoho.controller.clients;

import jakarta.validation.Valid;
import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientConnexionDTO;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientInscriptionDTO;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientResumeDTO;
import mg.vinaAkoho.vina_akoho.service.clients.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clients")
public class ClientViewController {

    private final ClientService clientService;

    public ClientViewController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/connexion")
    public String connexion(Model model) {
        model.addAttribute("connexion", new ClientConnexionDTO());
        return "clients/connexion";
    }

    @PostMapping("/connexion")
    public String connecter(@Valid @ModelAttribute("connexion") ClientConnexionDTO connexion,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("erreur", bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return "clients/connexion";
        }

        return clientService.connecter(connexion)
                .map(client -> {
                    redirectAttributes.addFlashAttribute("client", new ClientResumeDTO(client));
                    return "redirect:/clients/espace";
                })
                .orElseGet(() -> {
                    model.addAttribute("erreur", "Identifiant client ou numero telephone incorrect");
                    return "clients/connexion";
                });
    }

    @GetMapping("/espace")
    public String espace(Model model) {
        if (!model.containsAttribute("client")) {
            return "redirect:/clients/connexion";
        }
        return "clients/espace";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("inscription", new ClientInscriptionDTO());
        return "clients/nouveau";
    }

    @PostMapping("/nouveau")
    public String inscrire(@Valid @ModelAttribute("inscription") ClientInscriptionDTO inscription,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("erreur", bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return "clients/nouveau";
        }

        var client = clientService.inscrire(inscription);
        model.addAttribute("client", new ClientResumeDTO(client));
        model.addAttribute("succes", "Compte client enregistre. Votre identifiant est " + client.getId());
        return "clients/nouveau";
    }

    @GetMapping("/recherche")
    @ResponseBody
    public ApiResponse<ClientResumeDTO> rechercher(@RequestParam String numeroTelephone) {
        return clientService.rechercherParTelephone(numeroTelephone)
                .map(client -> ApiResponse.success("Client trouve", new ClientResumeDTO(client)))
                .orElseGet(() -> ApiResponse.error("Aucun client avec ce numero"));
    }
}
