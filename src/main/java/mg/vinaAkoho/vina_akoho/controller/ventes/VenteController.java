package mg.vinaAkoho.vina_akoho.controller.ventes;

import java.math.BigDecimal;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import mg.vinaAkoho.vina_akoho.dto.ventes.LigneVenteDTO;
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
import mg.vinaAkoho.vina_akoho.service.ventes.RecetteVenteService;
import mg.vinaAkoho.vina_akoho.service.ventes.ExportVenteService;
import mg.vinaAkoho.vina_akoho.service.ventes.VenteService;

@Controller
@RequestMapping("/ventes")
@RequiredArgsConstructor
public class VenteController {

    private static final String SESSION_PANIER = "panier";

    private final VenteService venteService;
    private final RecetteVenteService recetteVenteService;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final ModePaiementRepository modePaiementRepository;
    private final ExportVenteService exportVenteService;

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
    public String listerTous(
            @RequestParam(required = false) String client,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            Model model) {
        
        List<VenteDTO> toutesVentes = venteService.listerToutes();
        
        // Filtrer les ventes selon les critères
        List<VenteDTO> ventesFiltrees = toutesVentes.stream()
                .filter(v -> {
                    boolean match = true;
                    if (client != null && !client.isEmpty()) {
                        match = match && (v.getClientNom() != null && v.getClientNom().toLowerCase().contains(client.toLowerCase()));
                    }
                    if (statut != null && !statut.isEmpty()) {
                        match = match && statut.equals(v.getStatutVente());
                    }
                    if (dateDebut != null && !dateDebut.isEmpty() && v.getDateVente() != null) {
                        LocalDate debut = LocalDate.parse(dateDebut);
                        match = match && !v.getDateVente().toLocalDate().isBefore(debut);
                    }
                    if (dateFin != null && !dateFin.isEmpty() && v.getDateVente() != null) {
                        LocalDate fin = LocalDate.parse(dateFin);
                        match = match && !v.getDateVente().toLocalDate().isAfter(fin);
                    }
                    return match;
                })
                .collect(Collectors.toList());
        
        model.addAttribute("ventes", ventesFiltrees);
        
        // Calculer les statistiques sur les ventes filtrées
        LocalDate aujourdHui = LocalDate.now();
        LocalDate debutMois = aujourdHui.withDayOfMonth(1);
        
        BigDecimal ventesJour = ventesFiltrees.stream()
                .filter(v -> v.getDateVente() != null && 
                           v.getDateVente().toLocalDate().equals(aujourdHui))
                .map(VenteDTO::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal caMois = ventesFiltrees.stream()
                .filter(v -> v.getDateVente() != null && 
                           !v.getDateVente().toLocalDate().isBefore(debutMois))
                .map(VenteDTO::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long commandesEnAttente = ventesFiltrees.stream()
                .filter(v -> "En attente de paiement".equals(v.getStatutVente()))
                .count();
        
        long totalVentes = ventesFiltrees.size();
        double tauxConversion = totalVentes > 0 ? 
                (ventesFiltrees.stream().filter(v -> "Validée".equals(v.getStatutVente())).count() * 100.0 / totalVentes) : 0;
        
        model.addAttribute("ventesJour", ventesJour);
        model.addAttribute("caMois", caMois);
        model.addAttribute("commandesEnAttente", commandesEnAttente);
        model.addAttribute("tauxConversion", String.format("%.0f%%", tauxConversion));
        
        return "ventes/responsable-commercial-ventes";
    }

    @GetMapping("/historique")
    public String historique(
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) String zone,
            Model model) {
        
        List<VenteDTO> toutesVentes = venteService.listerToutes();
        
        // Filtrer les ventes - seulement celles payées (Validée)
        List<VenteDTO> ventesPayees = toutesVentes.stream()
                .filter(v -> "Validée".equals(v.getStatutVente()))
                .collect(Collectors.toList());
        
        // Filtrer par zone si spécifié
        if (zone != null && !zone.isEmpty() && !"Toutes les zones".equals(zone)) {
            ventesPayees = ventesPayees.stream()
                    .filter(v -> v.getClientZoneLivraison() != null && v.getClientZoneLivraison().equals(zone))
                    .collect(Collectors.toList());
        }
        
        // Filtrer par période si spécifié
        LocalDate aujourdHui = LocalDate.now();
        if (periode != null && !periode.isEmpty()) {
            LocalDate dateDebut;
            switch (periode) {
                case "Ce mois":
                    dateDebut = aujourdHui.withDayOfMonth(1);
                    break;
                case "Ce trimestre":
                    dateDebut = aujourdHui.withMonth(aujourdHui.getMonthValue() - (aujourdHui.getMonthValue() - 1) % 3).withDayOfMonth(1);
                    break;
                case "6 derniers mois":
                    dateDebut = aujourdHui.minusMonths(6);
                    break;
                default:
                    dateDebut = null;
            }
            if (dateDebut != null) {
                ventesPayees = ventesPayees.stream()
                        .filter(v -> v.getDateVente() != null && !v.getDateVente().toLocalDate().isBefore(dateDebut))
                        .collect(Collectors.toList());
            }
        }
        
        // Calculer les statistiques
        BigDecimal chiffreAffaires = ventesPayees.stream()
                .map(VenteDTO::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalVentesKg = ventesPayees.stream()
                .flatMap(v -> v.getLignes() != null ? v.getLignes().stream() : java.util.stream.Stream.empty())
                .mapToLong(l -> l.getQuantite() != null ? l.getQuantite().longValue() : 0)
                .sum();
        
        long nombreTransactions = ventesPayees.size();
        
        // Calculer les produits les plus vendus
        Map<String, BigDecimal> produitsVendus = ventesPayees.stream()
                .flatMap(v -> v.getLignes() != null ? v.getLignes().stream() : java.util.stream.Stream.empty())
                .collect(Collectors.groupingBy(
                        LigneVenteDTO::getNomProduit,
                        Collectors.reducing(BigDecimal.ZERO, LigneVenteDTO::getQuantite, BigDecimal::add)
                ));
        
        Map<String, BigDecimal> produitsCA = ventesPayees.stream()
                .flatMap(v -> v.getLignes() != null ? v.getLignes().stream() : java.util.stream.Stream.empty())
                .collect(Collectors.groupingBy(
                        LigneVenteDTO::getNomProduit,
                        Collectors.reducing(BigDecimal.ZERO, LigneVenteDTO::getMontant, BigDecimal::add)
                ));
        
        model.addAttribute("ventes", ventesPayees);
        model.addAttribute("totalVentesKg", totalVentesKg);
        model.addAttribute("chiffreAffaires", chiffreAffaires);
        model.addAttribute("nombreTransactions", nombreTransactions);
        model.addAttribute("produitsVendus", produitsVendus);
        model.addAttribute("produitsCA", produitsCA);
        model.addAttribute("periode", periode);
        model.addAttribute("zone", zone);
        
        return "ventes/responsable-commercial-ventes-historique";
    }

    @GetMapping("/recettes")
    public String recettes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            Model model) {
        LocalDate dateDebut = debut != null ? debut : LocalDate.now().withDayOfMonth(1);
        LocalDate dateFin = fin != null ? fin : LocalDate.now();

        var recettes = recetteVenteService.listerParPeriode(dateDebut, dateFin);
        model.addAttribute("debut", dateDebut);
        model.addAttribute("fin", dateFin);
        model.addAttribute("recettes", recettes);
        model.addAttribute("recetteTotale", recetteVenteService.calculerTotal(recettes));
        model.addAttribute("quantiteTotale", recetteVenteService.calculerQuantiteTotale(recettes));
        model.addAttribute("nombreLignes", recettes.size());
        return "ventes/responsable-commercial-recettes";
    }

    @GetMapping("/export/excel")
    public void exporterVentesExcel(
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) String zone,
            jakarta.servlet.http.HttpServletResponse response) throws IOException {
        
        List<VenteDTO> toutesVentes = venteService.listerToutes();
        
        // Filtrer les ventes - seulement celles payées (Validée)
        List<VenteDTO> ventesPayees = toutesVentes.stream()
                .filter(v -> "Validée".equals(v.getStatutVente()))
                .collect(Collectors.toList());
        
        // Filtrer par zone si spécifié
        if (zone != null && !zone.isEmpty() && !"Toutes les zones".equals(zone)) {
            ventesPayees = ventesPayees.stream()
                    .filter(v -> v.getClientAdresse() != null && v.getClientAdresse().contains(zone))
                    .collect(Collectors.toList());
        }
        
        // Filtrer par période si spécifié
        LocalDate aujourdHui = LocalDate.now();
        if (periode != null && !periode.isEmpty()) {
            LocalDate dateDebut;
            switch (periode) {
                case "Ce mois":
                    dateDebut = aujourdHui.withDayOfMonth(1);
                    break;
                case "Ce trimestre":
                    dateDebut = aujourdHui.withMonth(aujourdHui.getMonthValue() - (aujourdHui.getMonthValue() - 1) % 3).withDayOfMonth(1);
                    break;
                case "6 derniers mois":
                    dateDebut = aujourdHui.minusMonths(6);
                    break;
                default:
                    dateDebut = null;
            }
            if (dateDebut != null) {
                ventesPayees = ventesPayees.stream()
                        .filter(v -> v.getDateVente() != null && !v.getDateVente().toLocalDate().isBefore(dateDebut))
                        .collect(Collectors.toList());
            }
        }
        
        byte[] excelData = exportVenteService.exporterVentesExcel(ventesPayees);
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ventes_" + LocalDate.now() + ".xlsx");
        response.getOutputStream().write(excelData);
        response.getOutputStream().flush();
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
        PanierItemDTO item = panier.stream()
                .filter(i -> i.getIdProduit().equals(produit.getId()))
                .findFirst()
                .orElse(null);

        if (item != null) {
            item.setQuantite(item.getQuantite().add(form.getQuantite()));
            item.setMontant(item.getPrixUnitaire().multiply(item.getQuantite()));
        } else {
            BigDecimal montant = produit.getPrixVente().multiply(form.getQuantite());
            item = PanierItemDTO.builder()
                    .idProduit(produit.getId())
                    .nomProduit(produit.getNom())
                    .quantite(form.getQuantite())
                    .prixUnitaire(produit.getPrixVente())
                    .montant(montant)
                    .build();
            panier.add(item);
        }

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
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Le client et le mode de paiement sont obligatoires.");
            return "ventes/responsable-commercial-ventes-nouvelles";
        }

        List<PanierItemDTO> panier = panier(session);
        if (panier.isEmpty()) {
            model.addAttribute("error", "Le panier est vide.");
            return "ventes/responsable-commercial-ventes-nouvelles";
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
            model.addAttribute("error", "Impossible de créer la vente : " + e.getMessage());
            return "ventes/responsable-commercial-ventes-nouvelles";
        }
    }

    @GetMapping("/{id}")
    public String detailVente(@PathVariable Long id, Model model) {
        VenteDTO vente = venteService.trouverParId(id);
        model.addAttribute("vente", vente);
        return "ventes/responsable-commercial-ventes-detail";
    }

    @PostMapping("/{id}/valider-paiement")
    public String validerPaiement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            venteService.validerPaiement(id);
            redirectAttributes.addFlashAttribute("success", "Paiement validé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de valider le paiement : " + e.getMessage());
        }
        return "redirect:/ventes/" + id;
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
