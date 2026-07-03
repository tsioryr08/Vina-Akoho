package mg.vinaAkoho.vina_akoho.service.livraison;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.livraison.LivraisonDTO;
import mg.vinaAkoho.vina_akoho.dto.livraison.LivraisonFormDTO;
import mg.vinaAkoho.vina_akoho.entity.livraison.historique_statut_livraison;
import mg.vinaAkoho.vina_akoho.entity.livraison.livreur;
import mg.vinaAkoho.vina_akoho.entity.livraison.livraison;
import mg.vinaAkoho.vina_akoho.entity.livraison.statutLivraison;
import mg.vinaAkoho.vina_akoho.entity.ventes.Vente;
import mg.vinaAkoho.vina_akoho.exception.livraison.LivreurNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.livraison.LivraisonNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.livraison.VenteNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.livraison.HistoriqueChangementRepository;
import mg.vinaAkoho.vina_akoho.repository.livraison.LivraisonRepository;
import mg.vinaAkoho.vina_akoho.repository.livraison.LivreurRepository;
import mg.vinaAkoho.vina_akoho.repository.livraison.StatutLivraisonRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.VenteRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final VenteRepository venteRepository;
    private final LivreurRepository livreurRepository;
    private final StatutLivraisonRepository statutLivraisonRepository;
    private final HistoriqueChangementRepository historiqueChangementRepository;

    public List<LivraisonDTO> listerToutes() {
        return livraisonRepository.findAll()
                .stream()
                .map(this::versDTO)
                .collect(Collectors.toList());
    }

    public LivraisonDTO trouverParId(Long id) {
        livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> LivraisonNotFoundException.parId(id));
        return versDTO(livraison);
    }

    public LivraisonDTO creer(LivraisonFormDTO form, Integer idUtilisateur) {
        Vente vente = venteRepository.findById(form.getIdVente())
                .orElseThrow(() -> VenteNotFoundException.parId(form.getIdVente()));

        statutLivraison statut = statutLivraisonRepository.findById(form.getIdStatutLivraison())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Statut de livraison introuvable : " + form.getIdStatutLivraison()));

        livraison livraison = new livraison();
        livraison.setVente(vente);
        if (form.getIdLivreur() != null) {
            livreur livreur = livreurRepository.findById(form.getIdLivreur())
                    .orElseThrow(() -> LivreurNotFoundException.parId(Long.valueOf(form.getIdLivreur())));
            livraison.setLivreur(livreur);
        }
        livraison.setLieuExact(form.getLieuExact());
        livraison.setContact(form.getContact());
        if (form.getDateLivraison() != null && !form.getDateLivraison().isBlank()) {
            livraison.setDateLivraison(LocalDate.parse(form.getDateLivraison()));
        }
        livraison.setCommentaire(form.getCommentaire());
        livraison.setStatutLivraison(statut);

        livraison = livraisonRepository.save(livraison);

        historique_statut_livraison historique = new historique_statut_livraison();
        historique.setIdLivraison(livraison.getId().intValue());
        historique.setNouveauStatut(statut);
        historiqueChangementRepository.save(historique);

        return versDTO(livraison);
    }

    public LivraisonDTO modifierStatut(Long idLivraison, String nouveauStatutLibelle, Integer idUtilisateur) {
        livraison livraison = livraisonRepository.findById(idLivraison)
                .orElseThrow(() -> LivraisonNotFoundException.parId(idLivraison));

        String ancienStatut = livraison.getStatutLivraison() != null
                ? livraison.getStatutLivraison().getLibelle()
                : null;

        statutLivraison statut = statutLivraisonRepository.findByLibelleIgnoreCase(nouveauStatutLibelle)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Statut de livraison introuvable : " + nouveauStatutLibelle));

        livraison.setStatutLivraison(statut);
        livraison = livraisonRepository.save(livraison);

        historique_statut_livraison historique = new historique_statut_livraison();
        historique.setIdLivraison(livraison.getId().intValue());
        historique.setAncienStatut(null);
        historique.setNouveauStatut(statut);
        historiqueChangementRepository.save(historique);

        return versDTO(livraison);
    }

    private LivraisonDTO versDTO(livraison livraison) {
        Vente vente = livraison.getVente();
        String referenceVente = vente != null ? "V" + vente.getId() : null;
        String clientNom = vente != null && vente.getCommande() != null
                ? vente.getCommande().getClient().getNom()
                : null;
        String clientPrenom = vente != null && vente.getCommande() != null
                ? vente.getCommande().getClient().getPrenom()
                : null;

        livreur livreur = livraison.getLivreur();
        statutLivraison statut = livraison.getStatutLivraison();

        return LivraisonDTO.builder()
                .id(livraison.getId())
                .idVente(vente != null ? vente.getId() : null)
                .referenceVente(referenceVente)
                .clientNom(clientNom)
                .clientPrenom(clientPrenom)
                .livreurNom(livreur != null ? livreur.getNom() : null)
                .livreurPrenom(livreur != null ? livreur.getPrenom() : null)
                .lieuExact(livraison.getLieuExact())
                .contact(livraison.getContact())
                .dateLivraison(livraison.getDateLivraison())
                .commentaire(livraison.getCommentaire())
                .statutLivraison(statut != null ? statut.getLibelle() : null)
                .createdAt(livraison.getCreatedAt())
                .build();
    }
}
