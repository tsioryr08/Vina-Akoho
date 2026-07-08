package mg.vinaAkoho.vina_akoho.controller.livraison;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.service.livraison.LivraisonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistiques/livraisons")
@RequiredArgsConstructor
public class LivraisonStatistiqueController {

    private final LivraisonService livraisonService;

    @GetMapping("/zones")
    public Map<String, Long> getLivraisonsParZone() {
        return livraisonService.getStatistiquesZones();
    }

    @GetMapping("/total")
    public long getTotalLivraisons() {
        return livraisonService.countTotalLivraisons();
    }
}
