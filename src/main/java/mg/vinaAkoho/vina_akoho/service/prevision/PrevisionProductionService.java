package mg.vinaAkoho.vina_akoho.service.prevision;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.prevision.PrevisionMatierePremiereDTO;
import mg.vinaAkoho.vina_akoho.dto.prevision.PrevisionProductionDTO;
import mg.vinaAkoho.vina_akoho.dto.prevision.VenteProduitProjection;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MatierePremiere;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.entity.recetteproduit.RecetteProduit;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.LotMpRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.LotProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.recetteproduit.RecetteProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.LigneVenteRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrevisionProductionService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final ProduitRepository produitRepository;
    private final LigneVenteRepository ligneVenteRepository;
    private final LotProduitRepository lotProduitRepository;
    private final RecetteProduitRepository recetteProduitRepository;
    private final LotMpRepository lotMpRepository;

    public List<PrevisionProductionDTO> calculerProductions(int joursAnalyse, int joursCouverture) {
        int analyse = borner(joursAnalyse, 1, 365);
        int couverture = borner(joursCouverture, 1, 365);
        LocalDate aujourdHui = LocalDate.now();

        Map<Long, BigDecimal> ventes = ligneVenteRepository
                .sommerQuantitesVenduesParProduit(
                        aujourdHui.minusDays(analyse - 1L).atStartOfDay(),
                        aujourdHui.plusDays(1).atStartOfDay())
                .stream()
                .collect(Collectors.toMap(
                        VenteProduitProjection::getProduitId,
                        v -> valeur(v.getQuantiteVendue())));

        return produitRepository.findAllActifs().stream()
                .map(produit -> calculerProduit(produit, ventes.getOrDefault(produit.getId(), ZERO),
                        analyse, couverture))
                .sorted((a, b) -> b.propositionProduction().compareTo(a.propositionProduction()))
                .toList();
    }

    public List<PrevisionMatierePremiereDTO> calculerMatieresPremieres(
            List<PrevisionProductionDTO> productions) {
        Map<Integer, BesoinMp> besoins = new HashMap<>();

        for (PrevisionProductionDTO production : productions) {
            if (production.propositionProduction().signum() <= 0) {
                continue;
            }
            List<RecetteProduit> recette = recetteProduitRepository
                    .findByIdCategorieAndIsActiveTrue(productionCategorieId(production.produitId()));
            for (RecetteProduit ligne : recette) {
                MatierePremiere mp = ligne.getMatierePremiere();
                BigDecimal besoin = production.propositionProduction()
                        .multiply(valeur(ligne.getQuantiteMp()));
                besoins.computeIfAbsent(mp.getId(), id -> new BesoinMp(mp))
                        .ajouter(besoin);
            }
        }

        List<PrevisionMatierePremiereDTO> resultat = new ArrayList<>();
        for (BesoinMp besoin : besoins.values()) {
            MatierePremiere mp = besoin.matierePremiere;
            BigDecimal stock = valeur(lotMpRepository.sommeQuantiteRestante(mp.getId()));
            BigDecimal securite = valeur(mp.getSeuilMinimum());
            BigDecimal commande = besoin.quantite.add(securite).subtract(stock).max(ZERO);
            resultat.add(new PrevisionMatierePremiereDTO(
                    mp.getId(),
                    mp.getCode(),
                    mp.getNom(),
                    mp.getFournisseur() != null ? mp.getFournisseur().getNom() : "-",
                    mp.getUnite() != null ? mp.getUnite().getLibelle() : "Unité",
                    arrondir(besoin.quantite),
                    arrondir(stock),
                    arrondir(securite),
                    arrondir(commande),
                    commande.signum() > 0 ? "À COMMANDER" : "STOCK SUFFISANT"));
        }
        resultat.sort((a, b) -> b.quantiteACommander().compareTo(a.quantiteACommander()));
        return resultat;
    }

    private PrevisionProductionDTO calculerProduit(
            Produit produit, BigDecimal quantiteVendue, int joursAnalyse, int joursCouverture) {
        BigDecimal moyenne = quantiteVendue.divide(
                BigDecimal.valueOf(joursAnalyse), 2, RoundingMode.HALF_UP);
        BigDecimal stock = valeur(lotProduitRepository.sommeQuantiteRestante(produit.getId()));
        BigDecimal proposition = moyenne.add(stock);
        boolean recetteDisponible = !recetteProduitRepository
                .findByIdCategorieAndIsActiveTrue(produit.getCategorie().getId().intValue()).isEmpty();

        String statut = proposition.signum() == 0
                ? "STOCK SUFFISANT"
                : recetteDisponible ? "À PRODUIRE" : "RECETTE MANQUANTE";

        return new PrevisionProductionDTO(
                produit.getId(),
                produit.getNom(),
                produit.getCategorie().getLibelle(),
                produit.getUnite() != null ? produit.getUnite().getLibelle() : "Unité",
                arrondir(quantiteVendue),
                arrondir(moyenne),
                arrondir(stock),
                arrondir(proposition),
                arrondir(proposition),
                statut,
                recetteDisponible);
    }

    private Integer productionCategorieId(Long produitId) {
        return produitRepository.findById(produitId)
                .orElseThrow()
                .getCategorie().getId().intValue();
    }

    private static int borner(int valeur, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, valeur));
    }

    private static BigDecimal valeur(BigDecimal valeur) {
        return valeur == null ? ZERO : valeur;
    }

    private static BigDecimal arrondir(BigDecimal valeur) {
        return valeur.setScale(2, RoundingMode.HALF_UP);
    }

    private static final class BesoinMp {
        private final MatierePremiere matierePremiere;
        private BigDecimal quantite = ZERO;

        private BesoinMp(MatierePremiere matierePremiere) {
            this.matierePremiere = matierePremiere;
        }

        private void ajouter(BigDecimal valeur) {
            quantite = quantite.add(valeur);
        }
    }
}
