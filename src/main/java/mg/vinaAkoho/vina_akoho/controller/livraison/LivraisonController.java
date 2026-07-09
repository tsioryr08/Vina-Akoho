package mg.vinaAkoho.vina_akoho.controller.livraison;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.livraison.LivraisonFormDTO;
import mg.vinaAkoho.vina_akoho.entity.livraison.livreur;
import mg.vinaAkoho.vina_akoho.entity.livraison.statutLivraison;
import mg.vinaAkoho.vina_akoho.entity.ventes.Vente;
import mg.vinaAkoho.vina_akoho.repository.livraison.LivreurRepository;
import mg.vinaAkoho.vina_akoho.repository.livraison.StatutLivraisonRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.VenteRepository;
import mg.vinaAkoho.vina_akoho.security.SessionFilter;
import mg.vinaAkoho.vina_akoho.service.livraison.LivraisonService;

@Controller
@RequestMapping("/api/livraisons")
@RequiredArgsConstructor
public class LivraisonController {

    private final LivraisonService livraisonService;
    private final VenteRepository venteRepository;
    private final LivreurRepository livreurRepository;
    private final StatutLivraisonRepository statutLivraisonRepository;

    @ModelAttribute("ventesDisponibles")
    public List<Vente> getVentesDisponibles() {
        return venteRepository.findAll();
    }

    @ModelAttribute("livreursDisponibles")
    public List<livreur> getLivreursDisponibles() {
        return livreurRepository.findAll();
    }

    @ModelAttribute("statutsDisponibles")
    public List<statutLivraison> getStatutsDisponibles() {
        return statutLivraisonRepository.findAll();
    }

    @ModelAttribute("livraisonForm")
    public LivraisonFormDTO livraisonForm() {
        return new LivraisonFormDTO();
    }

    @GetMapping
    public String listerLivraisons(Model model) {
        model.addAttribute("livraisons", livraisonService.listerToutes());
        return "livraison/livraisons";
    }

    @GetMapping("/nouvelle")
    public String nouvelleLivraison() {
        return "livraison/livraison-nouvelle";
    }

    @GetMapping("/zones")
    public String anciennesZonesLivraison() {
        return "redirect:/api/livraisons/statistiques";
    }

    @PostMapping("/creer")
    public String creerLivraison(@Valid @ModelAttribute("livraisonForm") LivraisonFormDTO form,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Données invalides.");
            return "redirect:/api/livraisons/nouvelle";
        }

        try {
            Integer idUtilisateur = (Integer) session.getAttribute(SessionFilter.ATTRIBUT_ID_EMPLOYE);
            if (idUtilisateur == null) {
                idUtilisateur = 1;
            }
            livraisonService.creer(form, idUtilisateur);
            redirectAttributes.addFlashAttribute("success", "Livraison créée avec succès.");
            return "redirect:/api/livraisons";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de créer la livraison : " + e.getMessage());
            return "redirect:/api/livraisons/nouvelle";
        }
    }

    @GetMapping("/{id}")
    public String detailLivraison(@PathVariable Long id, Model model) {
        model.addAttribute("livraison", livraisonService.trouverParId(id));
        model.addAttribute("historiquesLivraison", livraisonService.listerHistoriquePourLivraison(id));
        return "livraison/livraison-detail";
    }

    @PostMapping("/{id}/statut")
    public String modifierStatut(@PathVariable Long id,
                                @ModelAttribute("nouveauStatut") String nouveauStatut,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        if (nouveauStatut == null || nouveauStatut.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Statut invalide.");
            return "redirect:/api/livraisons/" + id;
        }

        try {
            Integer idUtilisateur = (Integer) session.getAttribute(SessionFilter.ATTRIBUT_ID_EMPLOYE);
            if (idUtilisateur == null) {
                idUtilisateur = 1;
            }
            livraisonService.modifierStatut(id, nouveauStatut, idUtilisateur);
            redirectAttributes.addFlashAttribute("success", "Statut mis à jour.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de modifier le statut : " + e.getMessage());
        }

        return "redirect:/api/livraisons/" + id;
    }

    @GetMapping("/historique")
    public String historique(Model model) {
        model.addAttribute("historiques", livraisonService.listerHistorique());
        return "livraison/historique";
    }

    @GetMapping("/statistiques")
    public String afficherPageStatistiques() {
        // Cela pointe vers src/main/resources/templates/livraison/statistiques.html
        return "livraison/statistique";
    }
}
