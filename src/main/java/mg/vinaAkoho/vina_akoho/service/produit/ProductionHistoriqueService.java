package mg.vinaAkoho.vina_akoho.service.produit;

import mg.vinaAkoho.vina_akoho.dto.produit.ProductionConsommationDetailDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.ProductionDetailDTO;
import mg.vinaAkoho.vina_akoho.dto.produit.ProductionHistoriqueItemDTO;
import mg.vinaAkoho.vina_akoho.entity.login.Employe;
import mg.vinaAkoho.vina_akoho.entity.produit.Fabrication;
import mg.vinaAkoho.vina_akoho.entity.produit.FabricationMp;
import mg.vinaAkoho.vina_akoho.entity.produit.LotProduit;
import mg.vinaAkoho.vina_akoho.repository.produit.FabricationMpRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.FabricationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class ProductionHistoriqueService {

    private final FabricationRepository fabricationRepository;
    private final FabricationMpRepository fabricationMpRepository;

    public ProductionHistoriqueService(FabricationRepository fabricationRepository,
                                       FabricationMpRepository fabricationMpRepository) {
        this.fabricationRepository = fabricationRepository;
        this.fabricationMpRepository = fabricationMpRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductionHistoriqueItemDTO> lister(String recherche,
                                                    Long produitId,
                                                    LocalDate dateDebut,
                                                    LocalDate dateFin,
                                                    int page,
                                                    int taille) {
        String rechercheNormalisee = recherche != null && !recherche.isBlank()
                ? recherche.trim().toLowerCase(Locale.ROOT)
                : null;
        LocalDateTime debut = dateDebut != null ? dateDebut.atStartOfDay() : null;
        LocalDateTime finExclusive = dateFin != null ? dateFin.plusDays(1).atStartOfDay() : null;

        List<ProductionHistoriqueItemDTO> filtres = fabricationRepository.findAllByOrderByDateFabricationDescIdDesc()
                .stream()
                .filter(fabrication -> produitId == null
                        || fabrication.getLotProduit().getProduit().getId().equals(produitId))
                .filter(fabrication -> debut == null
                        || !fabrication.getDateFabrication().isBefore(debut))
                .filter(fabrication -> finExclusive == null
                        || fabrication.getDateFabrication().isBefore(finExclusive))
                .filter(fabrication -> correspondRecherche(fabrication, rechercheNormalisee))
                .map(this::toHistoriqueItem)
                .toList();

        int pageIndex = Math.max(page, 0);
        int pageSize = Math.max(taille, 1);
        int start = Math.min(pageIndex * pageSize, filtres.size());
        int end = Math.min(start + pageSize, filtres.size());

        return new PageImpl<>(
                filtres.subList(start, end),
                PageRequest.of(pageIndex, pageSize),
                filtres.size());
    }

    @Transactional(readOnly = true)
    public ProductionDetailDTO trouverParId(Integer idFabrication) {
        Fabrication fabrication = fabricationRepository.findById(idFabrication)
                .orElseThrow(() -> new IllegalArgumentException("Fabrication introuvable : #" + idFabrication));

        List<ProductionConsommationDetailDTO> consommations = fabricationMpRepository
                .findByFabricationIdOrderByIdAsc(idFabrication)
                .stream()
                .map(this::toConsommationDetail)
                .toList();

        return toDetail(fabrication, consommations);
    }

    private ProductionHistoriqueItemDTO toHistoriqueItem(Fabrication fabrication) {
        LotProduit lotProduit = fabrication.getLotProduit();
        return new ProductionHistoriqueItemDTO(
                fabrication.getId(),
                lotProduit.getId(),
                lotProduit.getProduit().getId(),
                lotProduit.getProduit().getNom(),
                fabrication.getQuantiteProduite(),
                lotProduit.getQuantiteRestante(),
                fabrication.getDateFabrication(),
                lotProduit.getDatePeremption(),
                formatEmploye(fabrication.getEmploye())
        );
    }

    private ProductionDetailDTO toDetail(Fabrication fabrication,
                                         List<ProductionConsommationDetailDTO> consommations) {
        LotProduit lotProduit = fabrication.getLotProduit();
        return new ProductionDetailDTO(
                fabrication.getId(),
                lotProduit.getId(),
                lotProduit.getProduit().getId(),
                lotProduit.getProduit().getNom(),
                fabrication.getQuantiteProduite(),
                lotProduit.getQuantiteRestante(),
                fabrication.getDateFabrication(),
                lotProduit.getDatePeremption(),
                formatEmploye(fabrication.getEmploye()),
                consommations
        );
    }

    private ProductionConsommationDetailDTO toConsommationDetail(FabricationMp fabricationMp) {
        var lotMp = fabricationMp.getLotMp();
        var matierePremiere = lotMp.getMatierePremiere();
        String unite = matierePremiere.getUnite() != null ? matierePremiere.getUnite().getLibelle() : "";
        return new ProductionConsommationDetailDTO(
                lotMp.getId(),
                matierePremiere.getNom(),
                fabricationMp.getQuantite(),
                lotMp.getQuantiteRestante(),
                unite
        );
    }

    private String formatEmploye(Employe employe) {
        String nom = employe.getNom() != null ? employe.getNom() : "";
        String prenom = employe.getPrenom() != null ? employe.getPrenom() : "";
        return (nom + " " + prenom).trim();
    }

    private boolean correspondRecherche(Fabrication fabrication, String recherche) {
        if (recherche == null) {
            return true;
        }

        String idFabrication = String.valueOf(fabrication.getId());
        String idLot = String.valueOf(fabrication.getLotProduit().getId());
        String nomProduit = fabrication.getLotProduit().getProduit().getNom();

        return idFabrication.contains(recherche)
                || idLot.contains(recherche)
                || (nomProduit != null && nomProduit.toLowerCase(Locale.ROOT).contains(recherche));
    }
}
