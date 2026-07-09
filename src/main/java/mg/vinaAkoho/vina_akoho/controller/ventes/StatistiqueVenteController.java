package mg.vinaAkoho.vina_akoho.controller.ventes;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.ventes.StatistiqueVenteReponseDTO;
import mg.vinaAkoho.vina_akoho.entity.produit.Categorie;
import mg.vinaAkoho.vina_akoho.repository.produit.CategorieRepository;
import mg.vinaAkoho.vina_akoho.service.ventes.StatistiqueVenteService;

@Controller
@RequestMapping("/api/ventes/statistiques")
@RequiredArgsConstructor
public class StatistiqueVenteController {

    private final StatistiqueVenteService statistiqueVenteService;
    private final CategorieRepository categorieRepository;

    @GetMapping
    public String page(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long idCategorie,
            @RequestParam(required = false) String triProduits,
            @RequestParam(required = false) String granularite,
            Model model) {

        LocalDate debut = dateDebut != null ? dateDebut : LocalDate.now().minusDays(30);
        LocalDate fin = dateFin != null ? dateFin : LocalDate.now();
        String tri = triProduits != null ? triProduits : "quantite";
        String granulariteChoisie = granularite != null ? granularite : "jour";

        StatistiqueVenteReponseDTO stats = statistiqueVenteService.obtenirStatistiques(
                debut, fin, idCategorie, tri, granulariteChoisie);

        List<Categorie> categories = categorieRepository.findAll().stream()
                .sorted(Comparator.comparing(Categorie::getLibelle))
                .toList();

        model.addAttribute("stats", stats);
        model.addAttribute("categories", categories);
        model.addAttribute("dateDebut", debut);
        model.addAttribute("dateFin", fin);
        model.addAttribute("idCategorie", idCategorie);
        model.addAttribute("triProduits", tri);
        model.addAttribute("granularite", granulariteChoisie);

        return "ventes/statistiques-ventes";
    }

    @GetMapping(value = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public StatistiqueVenteReponseDTO data(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long idCategorie,
            @RequestParam(defaultValue = "quantite") String triProduits,
            @RequestParam(defaultValue = "jour") String granularite) {
        return statistiqueVenteService.obtenirStatistiques(dateDebut, dateFin, idCategorie, triProduits, granularite);
    }
}