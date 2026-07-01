package mg.vinaAkoho.vina_akoho.controller.ventes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientResumeDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.PanierFormDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.PanierItemDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteFormDTO;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.entity.ventes.ModePaiement;
import mg.vinaAkoho.vina_akoho.repository.clients.ClientRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.ModePaiementRepository;
import mg.vinaAkoho.vina_akoho.security.SessionFilter;
import mg.vinaAkoho.vina_akoho.service.ventes.VenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ventes")
@RequiredArgsConstructor
public class VenteController {

    private static final String SESSION_PANIER = "panier";

    private final VenteService venteService;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final ModePaiementRepository modePaiementRepository;

    @ModelAttribute("clientsDisponibles")
    public List<ClientResumeDTO> getClientsDisponibles() {
        return clientRepository.findByEstSupprimerFalse()
                .stream()
                .map(ClientResumeDTO::new)
                .collect(Collectors.toList());
    }

    @ModelAttribute("produitsDisponibles")
    public List<Produit> getProduitsDisponibles() {
        return produitRepository.findAllActifs();
    }

    @ModelAttribute("modesPaiementDisponibles")
    public List<ModePaiement> getModesPaiementDisponibles() {
        return modePaiementRepository.findAll();
    }

    @ModelAttribute("panierForm")
    public PanierFormDTO panierForm() {
        return new PanierFormDTO();
    }

    @ModelAttribute("venteForm")
    public VenteFormDTO venteForm() {
        return new VenteFormDTO();
    }

    @ModelAttribute("panier")
    public List<PanierItemDTO> panier(HttpSession session) {
        List<PanierItemDTO> panier = (List<PanierItemDTO>) session.getAttribute(SESSION_PANIER);
        if (panier == null) {
            panier = new ArrayList<>();
            session.setAttribute(SESSION_PANIER, panier);
        }
        return panier;
    }

    @ModelAttribute("panierTotal")
    public BigDecimal panierTotal(HttpSession session) {
        return panier(session).stream()
                .map(item -> item.getMontant())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @GetMapping
    public String listerTous(Model model) {
        model.addAttribute("ventes", venteService.listerToutes());
        return "ventes/responsable-commercial-ventes";
    }

    @GetMapping("/historique")
    public String historique(Model model) {
        List<VenteDTO> ventes = venteService.listerToutes();
        model.addAttribute("ventes", ventes);
        model.addAttribute("totalVentesKg", ventes.size() * 1); // placeholder
        model.addAttribute("chiffreAffaires", ventes.stream()
                .map(VenteDTO::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return "ventes/responsable-commercial-ventes-historique";
    }

    @GetMapping("/nouvelle")
    public String nouvelleVente() {
        return "ventes/responsable-commercial-ventes-nouvelles";
    }

    @PostMapping("/panier/ajouter")
    public String ajouterAuPanier(@Valid @ModelAttribute("panierForm") PanierFormDTO form,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Quantité ou produit invalide.");
            return "redirect:/ventes/nouvelle";
        }

        Produit produit = produitRepository.findById(form.getIdProduit())
                .orElse(null);
        if (produit == null) {
            redirectAttributes.addFlashAttribute("error", "Produit introuvable.");
            return "redirect:/ventes/nouvelle";
        }

        List<PanierItemDTO> panier = panier(session);
        BigDecimal montant = produit.getPrixVente().multiply(form.getQuantite());
        PanierItemDTO item = PanierItemDTO.builder()
                .idProduit(produit.getId())
                .nomProduit(produit.getNom())
                .quantite(form.getQuantite())
                .prixUnitaire(produit.getPrixVente())
                .montant(montant)
                .build();
        panier.add(item);

        redirectAttributes.addFlashAttribute("success", "Produit ajouté au panier.");
        return "redirect:/ventes/nouvelle";
    }

    @PostMapping("/panier/{idProduit}/supprimer")
    public String supprimerDuPanier(@PathVariable Long idProduit,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        List<PanierItemDTO> panier = panier(session);
        panier.removeIf(item -> item.getIdProduit().equals(idProduit));
        redirectAttributes.addFlashAttribute("success", "Produit retiré du panier.");
        return "redirect:/ventes/nouvelle";
    }

    @PostMapping("/valider")
    public String validerVente(@Valid @ModelAttribute("venteForm") VenteFormDTO venteForm,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Le client et le mode de paiement sont obligatoires.");
            return "redirect:/ventes/nouvelle";
        }

        List<PanierItemDTO> panier = panier(session);
        if (panier.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Le panier est vide.");
            return "redirect:/ventes/nouvelle";
        }

        try {
            Integer idEmploye = (Integer) session.getAttribute(SessionFilter.ATTRIBUT_ID_EMPLOYE);
            if (idEmploye == null) {
                idEmploye = 1;
            }
            VenteDTO vente = venteService.creer(venteForm, panier, idEmploye);
            session.removeAttribute(SESSION_PANIER);
            redirectAttributes.addFlashAttribute("success", "Vente créée avec succès (#" + vente.getId() + ").");
            return "redirect:/ventes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de créer la vente : " + e.getMessage());
            return "redirect:/ventes/nouvelle";
        }
    }

    @GetMapping("/{id}")
    public String detailVente(@PathVariable Long id, Model model) {
        VenteDTO vente = venteService.trouverParId(id);
        model.addAttribute("vente", vente);
        return "ventes/responsable-commercial-ventes-detail";
    }

    @GetMapping("/{id}/facture")
    public String facture(@PathVariable Long id, Model model) {
        VenteDTO vente = venteService.trouverParId(id);
        model.addAttribute("vente", vente);
        return "ventes/facture";
    }

    @GetMapping("/{id}/bon-livraison")
    public String bonLivraison(@PathVariable Long id, Model model) {
        VenteDTO vente = venteService.trouverParId(id);
        model.addAttribute("vente", vente);
        return "ventes/bon-livraison";
    }
}
