package mg.vinaAkoho.vina_akoho.service.recetteproduit;

import jakarta.transaction.Transactional;
import mg.vinaAkoho.vina_akoho.dto.recetteproduit.CreateRecetteProduitDTO;
import mg.vinaAkoho.vina_akoho.dto.recetteproduit.RecetteProduitDTO;
import mg.vinaAkoho.vina_akoho.entity.recetteproduit.RecetteProduit;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MatierePremiere;
import mg.vinaAkoho.vina_akoho.exception.recetteproduit.RecetteProduitNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.recetteproduit.RecetteProduitRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.MatierePremiereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecetteProduitService {

    private final RecetteProduitRepository recetteProduitRepository;
    private final MatierePremiereRepository matierePremiereRepository;

    @Autowired
    public RecetteProduitService(
            RecetteProduitRepository recetteProduitRepository,
            MatierePremiereRepository matierePremiereRepository) {
        this.recetteProduitRepository = recetteProduitRepository;
        this.matierePremiereRepository = matierePremiereRepository;
    }

    // Créer une recette pour une catégorie
    @Transactional
    public List<RecetteProduitDTO> creerRecette(CreateRecetteProduitDTO dto) {
        // Supprimer l'ancienne recette si elle existe pour éviter les conflits de version
        List<RecetteProduit> anciennesLignes = recetteProduitRepository
                .findByIdCategorieAndIsActiveTrue(dto.getIdCategorie());

        int nouvelleVersion = 1;
        if (!anciennesLignes.isEmpty()) {
            nouvelleVersion = anciennesLignes.get(0).getVersion() + 1;
            recetteProduitRepository.deleteAll(anciennesLignes);
            recetteProduitRepository.flush(); // Force l'exécution de la suppression avant l'insertion
        }

        final int version = nouvelleVersion;

        // Créer les nouvelles lignes
        List<RecetteProduit> nouvelles = dto.getLignes().stream().map(ligne -> {
            MatierePremiere mp = matierePremiereRepository.findById(ligne.getIdMp())
                    .orElseThrow(() -> new RecetteProduitNotFoundException(
                            "Matière première #" + ligne.getIdMp() + " introuvable"));

            RecetteProduit r = new RecetteProduit();
            r.setIdCategorie(dto.getIdCategorie());
            r.setVersion(version);
            r.setMatierePremiere(mp);
            r.setQuantiteMp(ligne.getQuantiteMp());
            r.setIdUnite(ligne.getIdUnite());
            r.setIsActive(true);
            r.setIdEmployeCreation(dto.getIdEmployeCreation());
            return r;
        }).collect(Collectors.toList());

        List<RecetteProduit> sauvegardees = recetteProduitRepository.saveAll(nouvelles);
        return sauvegardees.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Lire la recette active d'une catégorie
    public List<RecetteProduitDTO> getRecetteActive(Integer idCategorie) {
        List<RecetteProduit> lignes = recetteProduitRepository
                .findByIdCategorieAndIsActiveTrue(idCategorie);
        if (lignes.isEmpty()) {
            throw new RecetteProduitNotFoundException(
                    "Aucune recette active pour la catégorie #" + idCategorie);
        }
        return lignes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Mapper entité → DTO
    private RecetteProduitDTO toDTO(RecetteProduit r) {
        RecetteProduitDTO dto = new RecetteProduitDTO();
        dto.setId(r.getId());
        dto.setIdCategorie(r.getIdCategorie());
        dto.setVersion(r.getVersion());
        dto.setIdMp(r.getMatierePremiere().getId());
        dto.setNomMp(r.getMatierePremiere().getNom());
        dto.setQuantiteMp(r.getQuantiteMp());
        dto.setIdUnite(r.getIdUnite());
        dto.setIsActive(r.getIsActive());
        dto.setDateCreation(r.getDateCreation());
        return dto;
    }
}
