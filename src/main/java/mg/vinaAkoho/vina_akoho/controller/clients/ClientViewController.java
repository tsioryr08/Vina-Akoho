package mg.vinaAkoho.vina_akoho.controller.clients;

import jakarta.validation.Valid;
import java.util.Map;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientConnexionDTO;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientGestionDTO;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientRequestDTO;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientResumeDTO;
import mg.vinaAkoho.vina_akoho.repository.clients.ServiceClientRepository;
import mg.vinaAkoho.vina_akoho.repository.clients.TypeClientRepository;
import mg.vinaAkoho.vina_akoho.service.clients.ClientService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final ServiceClientRepository serviceClientRepository;
    private final TypeClientRepository typeClientRepository;

    public ClientViewController(ClientService clientService,
                                ServiceClientRepository serviceClientRepository,
                                TypeClientRepository typeClientRepository) {
        this.clientService = clientService;
        this.serviceClientRepository = serviceClientRepository;
        this.typeClientRepository = typeClientRepository;
    }

    @GetMapping(value = "/connexion", produces = MediaType.TEXT_HTML_VALUE)
    public String connexion(Model model) {
        return "redirect:/clients/nouveau";
    }

    @GetMapping(value = {"", "/"}, produces = MediaType.TEXT_HTML_VALUE)
    public String index(Model model) {
        return espace(model);
    }

    @PostMapping("/connexion")
    public String connecter(@Valid @ModelAttribute("connexion") ClientConnexionDTO connexion,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("erreur", bindingResult.getFieldErrors().get(0).getDefaultMessage());
            model.addAttribute("inscription", creerClientExemple());
            ajouterReferences(model);
            return "clients/responsable-commercial-clients-nouveau";
        }

        return clientService.connecter(connexion)
                .map(client -> {
                    redirectAttributes.addFlashAttribute("client", new ClientResumeDTO(client));
                    return "redirect:/clients/espace";
                })
                .orElseGet(() -> {
                    model.addAttribute("erreur", "Identifiant client ou numero telephone incorrect");
                    model.addAttribute("inscription", creerClientExemple());
                    ajouterReferences(model);
                    return "clients/responsable-commercial-clients-nouveau";
                });
    }

    @GetMapping(value = "/espace", produces = MediaType.TEXT_HTML_VALUE)
    public String espace(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        ajouterReferences(model);
        return "clients/responsable-commercial-clients-liste";
    }

    @GetMapping(value = {"/liste", "/responsable-commercial-clients-liste.html"}, produces = MediaType.TEXT_HTML_VALUE)
    public String liste(Model model) {
        return espace(model);
    }

    @GetMapping(value = {"/nouveau", "/responsable-commercial-clients-nouveau.html"}, produces = MediaType.TEXT_HTML_VALUE)
    public String nouveau(Model model) {
        model.addAttribute("inscription", creerClientExemple());
        ajouterReferences(model);
        return "clients/responsable-commercial-clients-nouveau";
    }

    @PostMapping("/nouveau")
    public String inscrire(@Valid @ModelAttribute("inscription") ClientRequestDTO inscription,
                           BindingResult bindingResult,
                           Model model) {
        ajouterReferences(model);
        if (bindingResult.hasErrors()) {
            model.addAttribute("erreur", bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return "clients/responsable-commercial-clients-nouveau";
        }

        ClientGestionDTO client = clientService.createClient(inscription);
        model.addAttribute("client", client);
        model.addAttribute("succes", "Compte client enregistre. Votre identifiant est " + client.getId());
        return "clients/responsable-commercial-clients-nouveau";
    }

    @GetMapping(value = "/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("client", clientService.getClientById(id));
        return "clients/clients-detail";
    }

    @GetMapping(value = "/{id}/modifier", produces = MediaType.TEXT_HTML_VALUE)
    public String modifier(@PathVariable Integer id, Model model) {
        ClientGestionDTO client = clientService.getClientById(id);
        model.addAttribute("client", client);
        model.addAttribute("inscription", creerDepuisClient(client));
        ajouterReferences(model);
        return "clients/clients-detail-edit";
    }

    @PostMapping("/{id}/modifier")
    public String enregistrerModification(@PathVariable Integer id,
                                          @Valid @ModelAttribute("inscription") ClientRequestDTO inscription,
                                          BindingResult bindingResult,
                                          Model model) {
        ajouterReferences(model);
        if (bindingResult.hasErrors()) {
            model.addAttribute("client", clientService.getClientById(id));
            model.addAttribute("erreur", bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return "clients/clients-detail-edit";
        }

        ClientGestionDTO client = clientService.updateClient(id, inscription);
        model.addAttribute("client", client);
        model.addAttribute("succes", "Client modifie avec succes.");
        return "clients/clients-detail";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        clientService.deleteClient(id);
        redirectAttributes.addFlashAttribute("succes", "Client supprime de la liste.");
        return "redirect:/clients/espace";
    }

    @GetMapping("/recherche")
    @ResponseBody
    public Map<String, Object> rechercher(@RequestParam String numeroTelephone) {
        return clientService.rechercherParTelephone(numeroTelephone)
                .<Map<String, Object>>map(client -> Map.of(
                        "success", true,
                        "message", "Client trouve",
                        "data", new ClientResumeDTO(client)
                ))
                .orElseGet(() -> Map.of(
                        "success", false,
                        "message", "Aucun client avec ce numero"
                ));
    }

    private void ajouterReferences(Model model) {
        model.addAttribute("services", serviceClientRepository.findAll());
        model.addAttribute("typesClient", typeClientRepository.findAll());
    }

    private ClientRequestDTO creerClientExemple() {
        ClientRequestDTO inscription = new ClientRequestDTO();
        inscription.setNom("Rakoto");
        inscription.setPrenom("Jean Claude");
        inscription.setNumeroTelephone("0341111111");
        inscription.setAdresse("Antananarivo");
        inscription.setIdLocalite("LOC002");
        inscription.setIdZoneLivraison("ZONE002");
        inscription.setNotes("Client modifie");
        inscription.setIdService(1);
        inscription.setIdTypeClient(2);
        inscription.setTailleCheptel(40);
        inscription.setIsActif(true);
        return inscription;
    }

    private ClientRequestDTO creerDepuisClient(ClientGestionDTO client) {
        ClientRequestDTO inscription = new ClientRequestDTO();
        inscription.setNom(client.getNom());
        inscription.setPrenom(client.getPrenom());
        inscription.setNumeroTelephone(client.getNumeroTelephone());
        inscription.setAdresse(client.getAdresse());
        inscription.setIdLocalite(client.getIdLocalite());
        inscription.setIdZoneLivraison(client.getIdZoneLivraison());
        inscription.setNotes(client.getNotes());
        inscription.setIdService(client.getIdService());
        inscription.setIdTypeClient(client.getIdTypeClient());
        inscription.setTailleCheptel(client.getTailleCheptel());
        inscription.setIsActif(client.getIsActif());
        return inscription;
    }
}
