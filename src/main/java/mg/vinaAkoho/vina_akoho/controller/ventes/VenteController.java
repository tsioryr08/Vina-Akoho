package mg.vinaAkoho.vina_akoho.controller.ventes;

import java.math.BigDecimal;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.math.RoundingMode;

import org.springframework.data.domain.Page;
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
import mg.vinaAkoho.vina_akoho.dto.ventes.RechercheVenteDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientResumeDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.PanierFormDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.PanierItemDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteFormDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.ProduitVenduExportDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteListeExportDTO;
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
@RequestMapping("/api/ventes")
@RequiredArgsConstructor
public class VenteController {

    private static final String SESSION_PANIER = "panier";

    /**
     * Point 4 du markdown : une vente compte comme "réalisée" pour les
     * statistiques/CA tant qu'elle n'est ni en attente de paiement, ni annulée
     * (Validée, En préparation, En livraison, Livrée comptent toutes).
     */
    private static boolean estVenteRealisee(VenteDTO vente) {
        String statut = vente.getStatutVente();
        if (statut == null) {
            return false;
        }
        String s = statut.toLowerCase();
        return !s.equals("en attente de paiement") && !s.equals("annulée") && !s.equals("annulee");
    }

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
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) String modePaiement,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String avecLivraison,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) String triPar,
            @RequestParam(required = false) String ordreTri,
            Model model) {
        
        List<VenteDTO> toutesVentes = venteService.listerToutes();
        
        // Étape 1: Recherche textuelle (indépendante des filtres)
        List<VenteDTO> ventesRecherchees = toutesVentes.stream()
                .filter(v -> {
                    if (recherche == null || recherche.isEmpty()) {
                        return true;
                    }
                    String rechercheLower = recherche.toLowerCase();
                    boolean matchClient = v.getClientNom() != null && v.getClientNom().toLowerCase().contains(rechercheLower);
                    boolean matchProduit = v.getLignes() != null && v.getLignes().stream()
                            .anyMatch(l -> l.getNomProduit() != null && l.getNomProduit().toLowerCase().contains(rechercheLower));
                    boolean matchFacture = v.getFacture() != null && v.getFacture().getNumero() != null
                            && v.getFacture().getNumero().toLowerCase().contains(rechercheLower);
                    return matchClient || matchProduit || matchFacture;
                })
                .collect(Collectors.toList());

        // Étape 2: Filtres (indépendants de la recherche et du tri)
        List<VenteDTO> ventesFiltrees = ventesRecherchees.stream()
                .filter(v -> {
                    boolean match = true;
                    if (modePaiement != null && !modePaiement.isEmpty()) {
                        match = match && modePaiement.equals(v.getModePaiement());
                    }
                    if (statut != null && !statut.isEmpty()) {
                        match = match && statut.equals(v.getStatutVente());
                    }
                    if (avecLivraison != null && !avecLivraison.isEmpty()) {
                        boolean hasLivraison = v.getLivraison() != null;
                        if ("true".equals(avecLivraison)) {
                            match = match && hasLivraison;
                        } else if ("false".equals(avecLivraison)) {
                            match = match && !hasLivraison;
                        }
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

        // Étape 3: Tri (indépendant de la recherche et des filtres)
        if (triPar != null && !triPar.isEmpty()) {
            boolean desc = "desc".equalsIgnoreCase(ordreTri);
            switch (triPar) {
                case "dateVente":
                    ventesFiltrees.sort((v1, v2) -> {
                        if (v1.getDateVente() == null && v2.getDateVente() == null) return 0;
                        if (v1.getDateVente() == null) return desc ? 1 : -1;
                        if (v2.getDateVente() == null) return desc ? -1 : 1;
                        return desc ? v2.getDateVente().compareTo(v1.getDateVente()) : v1.getDateVente().compareTo(v2.getDateVente());
                    });
                    break;
                case "montantTotal":
                    ventesFiltrees.sort((v1, v2) -> {
                        if (v1.getMontantTotal() == null && v2.getMontantTotal() == null) return 0;
                        if (v1.getMontantTotal() == null) return desc ? 1 : -1;
                        if (v2.getMontantTotal() == null) return desc ? -1 : 1;
                        return desc ? v2.getMontantTotal().compareTo(v1.getMontantTotal()) : v1.getMontantTotal().compareTo(v2.getMontantTotal());
                    });
                    break;
                case "clientNom":
                    ventesFiltrees.sort((v1, v2) -> {
                        String nom1 = v1.getClientNom() != null ? v1.getClientNom() : "";
                        String nom2 = v2.getClientNom() != null ? v2.getClientNom() : "";
                        return desc ? nom2.compareToIgnoreCase(nom1) : nom1.compareToIgnoreCase(nom2);
                    });
                    break;
                default:
                    // Tri par défaut par date décroissante
                    ventesFiltrees.sort((v1, v2) -> {
                        if (v1.getDateVente() == null && v2.getDateVente() == null) return 0;
                        if (v1.getDateVente() == null) return 1;
                        if (v2.getDateVente() == null) return -1;
                        return v2.getDateVente().compareTo(v1.getDateVente());
                    });
            }
        } else {
            // Tri par défaut par date décroissante
            ventesFiltrees.sort((v1, v2) -> {
                if (v1.getDateVente() == null && v2.getDateVente() == null) return 0;
                if (v1.getDateVente() == null) return 1;
                if (v2.getDateVente() == null) return -1;
                return v2.getDateVente().compareTo(v1.getDateVente());
            });
        }

        model.addAttribute("ventes", ventesFiltrees);

        // Calculer les statistiques sur les ventes filtrées
        LocalDate aujourdHui = LocalDate.now();
        LocalDate debutMois = aujourdHui.withDayOfMonth(1);

        // Point 4 du markdown : le CA affiché ne doit compter que les ventes
        // réalisées (tout sauf "En attente de paiement" et "Annulée").
        BigDecimal ventesJour = ventesFiltrees.stream()
                .filter(v -> v.getDateVente() != null &&
                           v.getDateVente().toLocalDate().equals(aujourdHui))
                .filter(VenteController::estVenteRealisee)
                .map(VenteDTO::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal caMois = ventesFiltrees.stream()
                .filter(v -> v.getDateVente() != null &&
                           !v.getDateVente().toLocalDate().isBefore(debutMois))
                .filter(VenteController::estVenteRealisee)
                .map(VenteDTO::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long commandesEnAttente = ventesFiltrees.stream()
                .filter(v -> "En attente de paiement".equals(v.getStatutVente()))
                .count();

        long totalVentes = ventesFiltrees.size();
        double tauxConversion = totalVentes > 0 ?
                (ventesFiltrees.stream().filter(VenteController::estVenteRealisee).count() * 100.0 / totalVentes) : 0;

        model.addAttribute("ventesJour", ventesJour);
        model.addAttribute("caMois", caMois);
        model.addAttribute("commandesEnAttente", commandesEnAttente);
        model.addAttribute("tauxConversion", String.format("%.0f%%", tauxConversion));

        return "ventes/responsable-commercial-ventes";
    }

    @GetMapping("/liste/export/excel")
    public void exporterListeExcel(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) String modePaiement,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String avecLivraison,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) String triPar,
            @RequestParam(required = false) String ordreTri,
            jakarta.servlet.http.HttpServletResponse response) throws IOException {

        List<VenteDTO> toutesVentes = venteService.listerToutes();

        List<VenteDTO> ventesFiltrees = toutesVentes.stream()
                .filter(v -> {
                    if (recherche == null || recherche.isEmpty()) {
                        return true;
                    }
                    String rechercheLower = recherche.toLowerCase();
                    boolean matchClient = v.getClientNom() != null && v.getClientNom().toLowerCase().contains(rechercheLower);
                    boolean matchProduit = v.getLignes() != null && v.getLignes().stream()
                            .anyMatch(l -> l.getNomProduit() != null && l.getNomProduit().toLowerCase().contains(rechercheLower));
                    boolean matchFacture = v.getFacture() != null && v.getFacture().getNumero() != null
                            && v.getFacture().getNumero().toLowerCase().contains(rechercheLower);
                    return matchClient || matchProduit || matchFacture;
                })
                .filter(v -> {
                    boolean match = true;
                    if (modePaiement != null && !modePaiement.isEmpty()) {
                        match = match && modePaiement.equals(v.getModePaiement());
                    }
                    if (statut != null && !statut.isEmpty()) {
                        match = match && statut.equals(v.getStatutVente());
                    }
                    if (avecLivraison != null && !avecLivraison.isEmpty()) {
                        boolean hasLivraison = v.getLivraison() != null;
                        if ("true".equals(avecLivraison)) {
                            match = match && hasLivraison;
                        } else if ("false".equals(avecLivraison)) {
                            match = match && !hasLivraison;
                        }
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
                .sorted((v1, v2) -> {
                    if (triPar != null && !triPar.isEmpty()) {
                        boolean desc = "desc".equalsIgnoreCase(ordreTri);
                        switch (triPar) {
                            case "dateVente":
                                if (v1.getDateVente() == null && v2.getDateVente() == null) return 0;
                                if (v1.getDateVente() == null) return desc ? 1 : -1;
                                if (v2.getDateVente() == null) return desc ? -1 : 1;
                                return desc ? v2.getDateVente().compareTo(v1.getDateVente()) : v1.getDateVente().compareTo(v2.getDateVente());
                            case "montantTotal":
                                if (v1.getMontantTotal() == null && v2.getMontantTotal() == null) return 0;
                                if (v1.getMontantTotal() == null) return desc ? 1 : -1;
                                if (v2.getMontantTotal() == null) return desc ? -1 : 1;
                                return desc ? v2.getMontantTotal().compareTo(v1.getMontantTotal()) : v1.getMontantTotal().compareTo(v2.getMontantTotal());
                            case "clientNom":
                                String nom1 = v1.getClientNom() != null ? v1.getClientNom() : "";
                                String nom2 = v2.getClientNom() != null ? v2.getClientNom() : "";
                                return desc ? nom2.compareToIgnoreCase(nom1) : nom1.compareToIgnoreCase(nom2);
                        }
                    }
                    if (v1.getDateVente() == null && v2.getDateVente() == null) return 0;
                    if (v1.getDateVente() == null) return 1;
                    if (v2.getDateVente() == null) return -1;
                    return v2.getDateVente().compareTo(v1.getDateVente());
                })
                .toList();

        List<VenteListeExportDTO> export = ventesFiltrees.stream()
                .map(v -> VenteListeExportDTO.builder()
                        .id(v.getId())
                        .client((v.getClientNom() != null ? v.getClientNom() : "")
                                + (v.getClientPrenom() != null ? " " + v.getClientPrenom() : ""))
                        .date(v.getDateVente())
                        .produits(v.getLignes() != null ? v.getLignes().stream()
                                .map(l -> l.getNomProduit())
                                .filter(p -> p != null && !p.isBlank())
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("") : "")
                        .modePaiement(v.getModePaiement())
                        .total(v.getMontantTotal())
                        .statut(v.getStatutVente())
                        .build())
                .toList();

        byte[] excelData = exportVenteService.exporterVentesListeExcel(export);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ventes_liste_" + LocalDate.now() + ".xlsx");
        response.getOutputStream().write(excelData);
        response.getOutputStream().flush();
    }

    @GetMapping("/liste/export/pdf")
    public void exporterListePdf(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) String modePaiement,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String avecLivraison,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) String triPar,
            @RequestParam(required = false) String ordreTri,
            jakarta.servlet.http.HttpServletResponse response) throws IOException {

        List<VenteDTO> toutesVentes = venteService.listerToutes();

        List<VenteDTO> ventesFiltrees = toutesVentes.stream()
                .filter(v -> {
                    if (recherche == null || recherche.isEmpty()) {
                        return true;
                    }
                    String rechercheLower = recherche.toLowerCase();
                    boolean matchClient = v.getClientNom() != null && v.getClientNom().toLowerCase().contains(rechercheLower);
                    boolean matchProduit = v.getLignes() != null && v.getLignes().stream()
                            .anyMatch(l -> l.getNomProduit() != null && l.getNomProduit().toLowerCase().contains(rechercheLower));
                    boolean matchFacture = v.getFacture() != null && v.getFacture().getNumero() != null
                            && v.getFacture().getNumero().toLowerCase().contains(rechercheLower);
                    return matchClient || matchProduit || matchFacture;
                })
                .filter(v -> {
                    boolean match = true;
                    if (modePaiement != null && !modePaiement.isEmpty()) {
                        match = match && modePaiement.equals(v.getModePaiement());
                    }
                    if (statut != null && !statut.isEmpty()) {
                        match = match && statut.equals(v.getStatutVente());
                    }
                    if (avecLivraison != null && !avecLivraison.isEmpty()) {
                        boolean hasLivraison = v.getLivraison() != null;
                        if ("true".equals(avecLivraison)) {
                            match = match && hasLivraison;
                        } else if ("false".equals(avecLivraison)) {
                            match = match && !hasLivraison;
                        }
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
                .sorted((v1, v2) -> {
                    if (triPar != null && !triPar.isEmpty()) {
                        boolean desc = "desc".equalsIgnoreCase(ordreTri);
                        switch (triPar) {
                            case "dateVente":
                                if (v1.getDateVente() == null && v2.getDateVente() == null) return 0;
                                if (v1.getDateVente() == null) return desc ? 1 : -1;
                                if (v2.getDateVente() == null) return desc ? -1 : 1;
                                return desc ? v2.getDateVente().compareTo(v1.getDateVente()) : v1.getDateVente().compareTo(v2.getDateVente());
                            case "montantTotal":
                                if (v1.getMontantTotal() == null && v2.getMontantTotal() == null) return 0;
                                if (v1.getMontantTotal() == null) return desc ? 1 : -1;
                                if (v2.getMontantTotal() == null) return desc ? -1 : 1;
                                return desc ? v2.getMontantTotal().compareTo(v1.getMontantTotal()) : v1.getMontantTotal().compareTo(v2.getMontantTotal());
                            case "clientNom":
                                String nom1 = v1.getClientNom() != null ? v1.getClientNom() : "";
                                String nom2 = v2.getClientNom() != null ? v2.getClientNom() : "";
                                return desc ? nom2.compareToIgnoreCase(nom1) : nom1.compareToIgnoreCase(nom2);
                        }
                    }
                    if (v1.getDateVente() == null && v2.getDateVente() == null) return 0;
                    if (v1.getDateVente() == null) return 1;
                    if (v2.getDateVente() == null) return -1;
                    return v2.getDateVente().compareTo(v1.getDateVente());
                })
                .toList();

        List<VenteListeExportDTO> export = ventesFiltrees.stream()
                .map(v -> VenteListeExportDTO.builder()
                        .id(v.getId())
                        .client((v.getClientNom() != null ? v.getClientNom() : "")
                                + (v.getClientPrenom() != null ? " " + v.getClientPrenom() : ""))
                        .date(v.getDateVente())
                        .produits(v.getLignes() != null ? v.getLignes().stream()
                                .map(l -> l.getNomProduit())
                                .filter(p -> p != null && !p.isBlank())
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("") : "")
                        .modePaiement(v.getModePaiement())
                        .total(v.getMontantTotal())
                        .statut(v.getStatutVente())
                        .build())
                .toList();

        byte[] pdfData = exportVenteService.exporterVentesListePdf(export);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ventes_liste_" + LocalDate.now() + ".pdf");
        response.getOutputStream().write(pdfData);
        response.getOutputStream().flush();
    }

    @GetMapping("/historique")
    public String historique(
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) String zone,
            Model model) {

        List<VenteDTO> toutesVentes = venteService.listerToutes();

        // Point 4 du markdown : on ne garde que les ventes réalisées (exclut
        // "En attente de paiement" et "Annulée" ; inclut Validée, En
        // préparation, En livraison, Livrée).
        List<VenteDTO> ventesPayees = toutesVentes.stream()
                .filter(VenteController::estVenteRealisee)
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

    // Point 3 du markdown : chaque ligne du tableau correspond à un seul
    // produit (regroupé par nom) -> on peut donc afficher son unité réelle
    // au lieu du "kg" codé en dur dans le template.
    Map<String, String> produitsUnite = ventesPayees.stream()
        .flatMap(v -> v.getLignes() != null ? v.getLignes().stream() : java.util.stream.Stream.empty())
        .collect(Collectors.toMap(
            LigneVenteDTO::getNomProduit,
            l -> l.getUnite() != null && !l.getUnite().isBlank() ? l.getUnite() : "kg",
            (existant, nouveau) -> existant
        ));

    // IMPORTANT : on calcule ici le pourcentage de CA par produit, avec une
    // échelle et un arrondi explicites, au lieu de faire le .divide(...) dans
    // le template Thymeleaf. Sans scale/RoundingMode, BigDecimal.divide()
    // lève une ArithmeticException dès que le résultat n'est pas exact en
    // décimal (ex: 1/3), ce qui provoquait l'erreur 500.
    Map<String, BigDecimal> produitsPourcentage = new HashMap<>();
    if (chiffreAffaires.compareTo(BigDecimal.ZERO) > 0) {
        for (Map.Entry<String, BigDecimal> entry : produitsCA.entrySet()) {
        BigDecimal pourcentage = entry.getValue()
            .divide(chiffreAffaires, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
        produitsPourcentage.put(entry.getKey(), pourcentage);
        }
    } else {
        for (String nomProduit : produitsCA.keySet()) {
        produitsPourcentage.put(nomProduit, BigDecimal.ZERO);
        }
    }
        model.addAttribute("ventes", ventesPayees);
        model.addAttribute("totalVentesKg", totalVentesKg);
        model.addAttribute("chiffreAffaires", chiffreAffaires);
        model.addAttribute("nombreTransactions", nombreTransactions);
        model.addAttribute("produitsVendus", produitsVendus);
        model.addAttribute("produitsCA", produitsCA);
        model.addAttribute("produitsUnite", produitsUnite);
        model.addAttribute("produitsPourcentage", produitsPourcentage);
        model.addAttribute("periode", periode);
        model.addAttribute("zone", zone);

        return "ventes/responsable-commercial-ventes-historique";
    }

    @GetMapping("/historique/export/excel")
    public void exporterProduitsExcel(
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) String zone,
            jakarta.servlet.http.HttpServletResponse response) throws IOException {

        List<VenteDTO> toutesVentes = venteService.listerToutes();
        List<VenteDTO> ventesPayees = toutesVentes.stream()
                .filter(VenteController::estVenteRealisee)
                .collect(Collectors.toList());

        if (zone != null && !zone.isEmpty() && !"Toutes les zones".equals(zone)) {
            ventesPayees = ventesPayees.stream()
                    .filter(v -> v.getClientZoneLivraison() != null && v.getClientZoneLivraison().equals(zone))
                    .collect(Collectors.toList());
        }

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

        BigDecimal chiffreAffaires = ventesPayees.stream()
                .map(VenteDTO::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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

        Map<String, BigDecimal> produitsPourcentage = new HashMap<>();
        if (chiffreAffaires.compareTo(BigDecimal.ZERO) > 0) {
            for (Map.Entry<String, BigDecimal> entry : produitsCA.entrySet()) {
                BigDecimal pourcentage = entry.getValue()
                        .divide(chiffreAffaires, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                produitsPourcentage.put(entry.getKey(), pourcentage);
            }
        } else {
            for (String nomProduit : produitsCA.keySet()) {
                produitsPourcentage.put(nomProduit, BigDecimal.ZERO);
            }
        }

        List<ProduitVenduExportDTO> produits = produitsCA.keySet().stream()
                .map(nom -> new ProduitVenduExportDTO(
                        nom,
                        produitsVendus.getOrDefault(nom, BigDecimal.ZERO),
                        produitsCA.getOrDefault(nom, BigDecimal.ZERO),
                        produitsPourcentage.getOrDefault(nom, BigDecimal.ZERO)
                ))
                .sorted((a, b) -> b.chiffreAffaires().compareTo(a.chiffreAffaires()))
                .toList();

        byte[] excelData = exportVenteService.exporterProduitsExcel(produits);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=produits_vendus_" + LocalDate.now() + ".xlsx");
        response.getOutputStream().write(excelData);
        response.getOutputStream().flush();
    }

    @GetMapping("/historique/export/pdf")
    public void exporterProduitsPdf(
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) String zone,
            jakarta.servlet.http.HttpServletResponse response) throws IOException {

        List<VenteDTO> toutesVentes = venteService.listerToutes();
        List<VenteDTO> ventesPayees = toutesVentes.stream()
                .filter(VenteController::estVenteRealisee)
                .collect(Collectors.toList());

        if (zone != null && !zone.isEmpty() && !"Toutes les zones".equals(zone)) {
            ventesPayees = ventesPayees.stream()
                    .filter(v -> v.getClientZoneLivraison() != null && v.getClientZoneLivraison().equals(zone))
                    .collect(Collectors.toList());
        }

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

        BigDecimal chiffreAffaires = ventesPayees.stream()
                .map(VenteDTO::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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

        Map<String, BigDecimal> produitsPourcentage = new HashMap<>();
        if (chiffreAffaires.compareTo(BigDecimal.ZERO) > 0) {
            for (Map.Entry<String, BigDecimal> entry : produitsCA.entrySet()) {
                BigDecimal pourcentage = entry.getValue()
                        .divide(chiffreAffaires, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                produitsPourcentage.put(entry.getKey(), pourcentage);
            }
        } else {
            for (String nomProduit : produitsCA.keySet()) {
                produitsPourcentage.put(nomProduit, BigDecimal.ZERO);
            }
        }

        List<ProduitVenduExportDTO> produits = produitsCA.keySet().stream()
                .map(nom -> new ProduitVenduExportDTO(
                        nom,
                        produitsVendus.getOrDefault(nom, BigDecimal.ZERO),
                        produitsCA.getOrDefault(nom, BigDecimal.ZERO),
                        produitsPourcentage.getOrDefault(nom, BigDecimal.ZERO)
                ))
                .sorted((a, b) -> b.chiffreAffaires().compareTo(a.chiffreAffaires()))
                .toList();

        byte[] pdfData = exportVenteService.exporterProduitsPdf(produits);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=produits_vendus_" + LocalDate.now() + ".pdf");
        response.getOutputStream().write(pdfData);
        response.getOutputStream().flush();
    }

    @GetMapping("/historique-ventes")
    public String historiqueVentes(
            @RequestParam(required = false) String client,
            @RequestParam(required = false) String produit,
            @RequestParam(required = false) String numeroFacture,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String modePaiement,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) BigDecimal montantMin,
            @RequestParam(required = false) BigDecimal montantMax,
            @RequestParam(required = false) String triPar,
            @RequestParam(required = false) String ordreTri,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer taille,
            Model model) {

        RechercheVenteDTO recherche = RechercheVenteDTO.builder()
                .client(client)
                .produit(produit)
                .numeroFacture(numeroFacture)
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .modePaiement(modePaiement)
                .statut(statut)
                .montantMin(montantMin)
                .montantMax(montantMax)
                .triPar(triPar)
                .ordreTri(ordreTri)
                .page(page != null ? page - 1 : 0)
                .taille(taille != null ? taille : 10)
                .build();

        Page<VenteDTO> pageVentes = venteService.rechercherVentesAvecPagination(recherche);

        model.addAttribute("pageVentes", pageVentes);
        model.addAttribute("recherche", recherche);
        model.addAttribute("modesPaiement", modePaiementRepository.findAll());

        return "ventes/historique-ventes";
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

        // Point 4 du markdown : seules les ventes réalisées sont exportées.
        List<VenteDTO> ventesPayees = toutesVentes.stream()
                .filter(VenteController::estVenteRealisee)
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

    @GetMapping("/export/pdf")
    public void exporterVentesPdf(
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) String zone,
            jakarta.servlet.http.HttpServletResponse response) throws IOException {

        List<VenteDTO> toutesVentes = venteService.listerToutes();

        List<VenteDTO> ventesPayees = toutesVentes.stream()
            .filter(VenteController::estVenteRealisee)
            .collect(Collectors.toList());

        if (zone != null && !zone.isEmpty() && !"Toutes les zones".equals(zone)) {
            ventesPayees = ventesPayees.stream()
                    .filter(v -> v.getClientAdresse() != null && v.getClientAdresse().contains(zone))
                    .collect(Collectors.toList());
        }

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

        byte[] pdfData = exportVenteService.exporterVentesPdf(ventesPayees);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ventes_" + LocalDate.now() + ".pdf");
        response.getOutputStream().write(pdfData);
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
            return "redirect:/api/ventes/nouvelle";
        }

        Produit produit = produitRepository.findById(form.getIdProduit())
                .orElse(null);
        if (produit == null) {
            redirectAttributes.addFlashAttribute("error", "Produit introuvable.");
            return "redirect:/api/ventes/nouvelle";
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
            String unite = produit.getUnite() != null ? produit.getUnite().getLibelle() : "";
            item = PanierItemDTO.builder()
                    .idProduit(produit.getId())
                    .nomProduit(produit.getNom())
                    .quantite(form.getQuantite())
                    .prixUnitaire(produit.getPrixVente())
                    .montant(montant)
                    .unite(unite)
                    .build();
            panier.add(item);
        }

        redirectAttributes.addFlashAttribute("success", "Produit ajouté au panier.");
        return "redirect:/api/ventes/nouvelle";
    }

    @PostMapping("/panier/{idProduit}/supprimer")
    public String supprimerDuPanier(@PathVariable Long idProduit,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        List<PanierItemDTO> panier = panier(session);
        panier.removeIf(item -> item.getIdProduit().equals(idProduit));
        redirectAttributes.addFlashAttribute("success", "Produit retiré du panier.");
        return "redirect:/api/ventes/nouvelle";
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
            return "redirect:/api/ventes";
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
        return "redirect:/api/ventes/" + id;
    }

    @PostMapping("/{id}/annuler")
    public String annulerVente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            venteService.annulerVente(id);
            redirectAttributes.addFlashAttribute("success", "Commande annulée avec succès.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible d'annuler la commande : " + e.getMessage());
        }
        return "redirect:/ventes/" + id;
    }

    @GetMapping("/{id}/facture")
    public String facture(@PathVariable Long id, Model model) {
        VenteDTO vente = venteService.trouverParId(id);
        model.addAttribute("vente", vente);
        return "ventes/facture";
    }

    @GetMapping("/{id}/facture/pdf")
    public void facturePdf(@PathVariable Long id, jakarta.servlet.http.HttpServletResponse response) throws IOException {
        VenteDTO vente = venteService.trouverParId(id);
        byte[] pdfData = exportVenteService.exporterFactureVentePdf(vente);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=facture_vente_" + id + ".pdf");
        response.getOutputStream().write(pdfData);
        response.getOutputStream().flush();
    }

    @GetMapping("/{id}/bon-livraison")
    public String bonLivraison(@PathVariable Long id, Model model) {
        VenteDTO vente = venteService.trouverParId(id);
        model.addAttribute("vente", vente);
        return "ventes/bon-livraison";
    }
}
