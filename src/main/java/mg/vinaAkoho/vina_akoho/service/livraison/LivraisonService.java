package mg.vinaAkoho.vina_akoho.service.livraison;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.livraison.HistoriqueChangementDTO;
import mg.vinaAkoho.vina_akoho.dto.livraison.LivraisonDTO;
import mg.vinaAkoho.vina_akoho.dto.livraison.LivraisonFormDTO;
import mg.vinaAkoho.vina_akoho.entity.livraison.historique_statut_livraison;
import mg.vinaAkoho.vina_akoho.entity.livraison.livreur;
import mg.vinaAkoho.vina_akoho.entity.livraison.livraison;
import mg.vinaAkoho.vina_akoho.entity.livraison.statutLivraison;
import mg.vinaAkoho.vina_akoho.entity.livraison.ZoneLivraison;
import mg.vinaAkoho.vina_akoho.entity.ventes.Vente;
import mg.vinaAkoho.vina_akoho.entity.ventes.StatutVente;
import mg.vinaAkoho.vina_akoho.exception.livraison.LivreurNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.livraison.LivraisonNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.livraison.VenteNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.livraison.HistoriqueChangementRepository;
import mg.vinaAkoho.vina_akoho.repository.livraison.LivraisonRepository;
import mg.vinaAkoho.vina_akoho.repository.livraison.LivreurRepository;
import mg.vinaAkoho.vina_akoho.repository.livraison.StatutLivraisonRepository;
import mg.vinaAkoho.vina_akoho.repository.livraison.ZoneLivraisonRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.StatutVenteRepository;
import mg.vinaAkoho.vina_akoho.repository.ventes.VenteRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final VenteRepository venteRepository;
    private final LivreurRepository livreurRepository;
    private final StatutLivraisonRepository statutLivraisonRepository;
    private final ZoneLivraisonRepository zoneLivraisonRepository;
    private final HistoriqueChangementRepository historiqueChangementRepository;
    private final StatutVenteRepository statutVenteRepository;

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

        ZoneLivraison zone = zoneLivraisonRepository.findById(form.getIdZoneLivraison())
            .orElseThrow(() -> new EntityNotFoundException(
                "Zone de livraison introuvable : " + form.getIdZoneLivraison()));

        statutLivraison statut = form.getIdStatutLivraison() != null
                ? statutLivraisonRepository.findById(form.getIdStatutLivraison())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Statut de livraison introuvable : " + form.getIdStatutLivraison()))
                : statutParDefautCreation();

        livraison livraison = new livraison();
        livraison.setVente(vente);
        if (form.getIdLivreur() != null) {
            livreur livreur = livreurRepository.findById(form.getIdLivreur())
                .orElseThrow(() -> LivreurNotFoundException.parId(Long.valueOf(form.getIdLivreur())));
            livraison.setLivreur(livreur);
        }
        livraison.setZoneLivraison(zone);
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
        historique.setDateChangement(LocalDateTime.now());
        historique.setCreatedAt(LocalDateTime.now());
        historique.setNouveauStatut(statut);
        historiqueChangementRepository.save(historique);

        return versDTO(livraison);
    }

    public LivraisonDTO modifierStatut(Long idLivraison, String nouveauStatutLibelle, Integer idUtilisateur) {
        livraison livraison = livraisonRepository.findById(idLivraison)
                .orElseThrow(() -> LivraisonNotFoundException.parId(idLivraison));

        statutLivraison ancienStatut = livraison.getStatutLivraison();

        if (ancienStatut != null && estStatutLivre(ancienStatut.getLibelle())) {
            throw new IllegalStateException("Le statut d'une livraison déjà livrée ne peut plus être modifié.");
        }

        statutLivraison statut = statutLivraisonRepository.findByLibelleIgnoreCase(nouveauStatutLibelle)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Statut de livraison introuvable : " + nouveauStatutLibelle));

        if (estStatutLivre(statut.getLibelle())
            && livraison.getLivreur() == null) {
            throw new IllegalStateException("Une livraison doit avoir un livreur assigné avant d'être validée.");
        }

        livraison.setStatutLivraison(statut);
        livraison = livraisonRepository.save(livraison);

        historique_statut_livraison historique = new historique_statut_livraison();
        historique.setIdLivraison(livraison.getId().intValue());
        historique.setDateChangement(LocalDateTime.now());
        historique.setCreatedAt(LocalDateTime.now());
        historique.setAncienStatut(ancienStatut);
        historique.setNouveauStatut(statut);
        historiqueChangementRepository.save(historique);

        // Point 5 du markdown (option A) : une fois la livraison effectuée,
        // la vente associée est considérée comme terminée -> on synchronise
        // son statut avec celui de la livraison.
        if (estStatutLivre(statut.getLibelle())) {
            Vente vente = livraison.getVente();
            if (vente != null) {
                StatutVente statutVenteLivree = statutVenteRepository.findByLibelleIgnoreCase("Livrée")
                        .orElseGet(() -> {
                            StatutVente nouveau = new StatutVente();
                            nouveau.setLibelle("Livrée");
                            return statutVenteRepository.save(nouveau);
                        });
                vente.setStatutVente(statutVenteLivree);
                venteRepository.save(vente);
            }
        }

        return versDTO(livraison);
    }

    public boolean estStatutLivre(String libelleStatut) {
        return "livrée".equalsIgnoreCase(libelleStatut) || "livree".equalsIgnoreCase(libelleStatut);
    }

    private statutLivraison statutParDefautCreation() {
        return statutLivraisonRepository.findByLibelleIgnoreCase("En cours de livraison")
                .or(() -> statutLivraisonRepository.findByLibelleIgnoreCase("En cours"))
                .or(() -> statutLivraisonRepository.findAll().stream()
                        .filter(s -> s.getLibelle() != null && s.getLibelle().toLowerCase().contains("en cours"))
                        .findFirst())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun statut de livraison par défaut 'En cours' n'a été trouvé."));
    }

    public List<HistoriqueChangementDTO> listerHistorique() {
        return historiqueChangementRepository.findAllByOrderByDateChangementDesc().stream()
                .map(this::versHistoriqueDTO)
                .collect(Collectors.toList());
    }

    public List<HistoriqueChangementDTO> listerHistoriquePourLivraison(Long idLivraison) {
        return historiqueChangementRepository.findByIdLivraisonOrderByDateChangementDesc(idLivraison.intValue()).stream()
                .map(this::versHistoriqueDTO)
                .collect(Collectors.toList());
    }

    public LivraisonDTO versDTO(livraison livraison) {
        Vente vente = livraison.getVente();
        String referenceVente = vente != null ? "V" + vente.getId() : null;
        String clientNom = vente != null && vente.getClient() != null
                ? vente.getClient().getNom()
                : null;
        String clientPrenom = vente != null && vente.getClient() != null
                ? vente.getClient().getPrenom()
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
                .zoneLivraison(livraison.getZoneLivraison() != null ? livraison.getZoneLivraison().getLibelle() : null)
                .statutLivraison(statut != null ? statut.getLibelle() : null)
                .createdAt(livraison.getCreatedAt())
                .build();
    }

        private HistoriqueChangementDTO versHistoriqueDTO(historique_statut_livraison historique) {
                return HistoriqueChangementDTO.builder()
                                .id(historique.getId())
                                .idLivraison(historique.getIdLivraison())
                                .ancienStatut(historique.getAncienStatut() != null ? historique.getAncienStatut().getLibelle() : null)
                                .nouveauStatut(historique.getNouveauStatut() != null ? historique.getNouveauStatut().getLibelle() : null)
                                .dateChangement(historique.getDateChangement() != null ? historique.getDateChangement() : historique.getCreatedAt())
                                .build();
        }

    public Map<String, Long> getStatistiquesZones() {
        return livraisonRepository.countLivraisonsByZone().stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> (Long) obj[1]
                ));
    }

    public long countTotalLivraisons() {
        return livraisonRepository.count();
    }
}
