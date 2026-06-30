package mg.vinaAkoho.vina_akoho.service.matierespremieres;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.EntreeStockDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.FicheDetailDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.FournisseurDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.LotDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.MatierePremiereListDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.MatierePremiereRequestDTO;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.UniteDTO;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Fournisseur;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.LotMp;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MatierePremiere;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MouvementStockMp;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.TypeMouvement;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Unite;
import mg.vinaAkoho.vina_akoho.exception.matierespremieres.FournisseurNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.matierespremieres.MatierePremiereNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.matierespremieres.TypeMouvementNotFoundException;
import mg.vinaAkoho.vina_akoho.exception.matierespremieres.UniteNotFoundException;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.FournisseurRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.LotMpRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.MatierePremiereRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.MouvementStockMpRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.TypeMouvementRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.UniteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatierePremiereService {

    private static final String STATUT_ALERTE = "SEUIL ATTEINT";
    private static final String STATUT_OK = "Stock Correct";

    private final MatierePremiereRepository matierePremiereRepository;
    private final FournisseurRepository fournisseurRepository;
    private final UniteRepository uniteRepository;
    private final LotMpRepository lotMpRepository;
    private final TypeMouvementRepository typeMouvementRepository;
    private final MouvementStockMpRepository mouvementStockMpRepository;

    public MatierePremiereService(MatierePremiereRepository matierePremiereRepository,
                                  FournisseurRepository fournisseurRepository,
                                  UniteRepository uniteRepository,
                                  LotMpRepository lotMpRepository,
                                  TypeMouvementRepository typeMouvementRepository,
                                  MouvementStockMpRepository mouvementStockMpRepository) {
        this.matierePremiereRepository = matierePremiereRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.uniteRepository = uniteRepository;
        this.lotMpRepository = lotMpRepository;
        this.typeMouvementRepository = typeMouvementRepository;
        this.mouvementStockMpRepository = mouvementStockMpRepository;
    }

    public List<MatierePremiereListDTO> lister() {
        return matierePremiereRepository.findAll().stream().map(this::versListDTO).toList();
    }

    public List<MatierePremiereListDTO> listerAlertes() {
        return lister().stream().filter(mp -> STATUT_ALERTE.equals(mp.statut())).toList();
    }

    public FicheDetailDTO detail(Integer id) {
        MatierePremiere mp = matierePremiereRepository.findById(id)
                .orElseThrow(() -> new MatierePremiereNotFoundException("Matière première introuvable : " + id));
        return versDetailDTO(mp);
    }

    @Transactional
    public FicheDetailDTO creer(MatierePremiereRequestDTO dto) {
        MatierePremiere mp = new MatierePremiere();
        mp.setCode(genererCode(dto.nom()));
        appliquerChamps(mp, dto);
        return versDetailDTO(matierePremiereRepository.save(mp));
    }

    @Transactional
    public FicheDetailDTO modifier(Integer id, MatierePremiereRequestDTO dto) {
        MatierePremiere mp = matierePremiereRepository.findById(id)
                .orElseThrow(() -> new MatierePremiereNotFoundException("Matière première introuvable : " + id));
        appliquerChamps(mp, dto);
        return versDetailDTO(matierePremiereRepository.save(mp));
    }

    @Transactional
    public void supprimer(Integer id) {
        if (!matierePremiereRepository.existsById(id)) {
            throw new MatierePremiereNotFoundException("Matière première introuvable : " + id);
        }
        matierePremiereRepository.deleteById(id);
    }

    @Transactional
    public FicheDetailDTO entreeStock(EntreeStockDTO dto) {
        MatierePremiere mp = matierePremiereRepository.findById(dto.idMatierePremiere())
                .orElseThrow(() -> new MatierePremiereNotFoundException(
                        "Matière première introuvable : " + dto.idMatierePremiere()));
        TypeMouvement entree = typeMouvementRepository.findByLibelle("ENTREE")
                .orElseThrow(() -> new TypeMouvementNotFoundException("Type de mouvement 'ENTREE' non configuré"));

        LotMp lot = new LotMp();
        lot.setMatierePremiere(mp);
        lot.setQuantiteInitiale(dto.quantite());
        lot.setQuantiteRestante(dto.quantite());
        lot.setDateAchat(dto.dateReception());
        // RG03 — enregistrer le coût unitaire et le fournisseur par lot d'achat
        lot.setCoutUnitaire(dto.coutUnitaire());
        lot.setFournisseur(mp.getFournisseur());
        lotMpRepository.save(lot);

        MouvementStockMp mouvement = new MouvementStockMp();
        mouvement.setTypeMouvement(entree);
        mouvement.setLotMp(lot);
        mouvement.setQuantite(dto.quantite());
        mouvement.setUnite(mp.getUnite());
        mouvement.setIdEmploye(dto.idEmploye());
        mouvement.setDateMouvement(dto.dateReception());
        mouvementStockMpRepository.save(mouvement);

        return versDetailDTO(mp);
    }

    public List<FournisseurDTO> listerFournisseurs() {
        return fournisseurRepository.findAll().stream()
                .map(f -> new FournisseurDTO(f.getId(), f.getNom())).toList();
    }

    public List<UniteDTO> listerUnites() {
        return uniteRepository.findAll().stream()
                .map(u -> new UniteDTO(u.getId(), u.getLibelle())).toList();
    }

    private void appliquerChamps(MatierePremiere mp, MatierePremiereRequestDTO dto) {
        Fournisseur fournisseur = fournisseurRepository.findById(dto.idFournisseur())
                .orElseThrow(() -> new FournisseurNotFoundException("Fournisseur introuvable : " + dto.idFournisseur()));
        Unite unite = uniteRepository.findById(dto.idUnite())
                .orElseThrow(() -> new UniteNotFoundException("Unité introuvable : " + dto.idUnite()));
        mp.setNom(dto.nom());
        mp.setFournisseur(fournisseur);
        mp.setCoutUnitaire(dto.coutUnitaire());
        mp.setUnite(unite);
        mp.setSeuilMinimum(dto.seuilMinimum());
    }

    private MatierePremiereListDTO versListDTO(MatierePremiere mp) {
        BigDecimal stock = lotMpRepository.sommeQuantiteRestante(mp.getId());
        return new MatierePremiereListDTO(mp.getId(), mp.getNom(), mp.getFournisseur().getNom(),
                mp.getUnite().getLibelle(), stock, mp.getSeuilMinimum(), mp.getCoutUnitaire(),
                statut(stock, mp.getSeuilMinimum()));
    }

    private FicheDetailDTO versDetailDTO(MatierePremiere mp) {
        List<LotMp> lots = lotMpRepository.findByMatierePremiereIdOrderByDateAchatAscIdAsc(mp.getId());

        BigDecimal stock = BigDecimal.ZERO;
        for (LotMp lot : lots) {
            stock = stock.add(lot.getQuantiteRestante());
        }

        // PAMP calculé à partir du coût réel de chaque lot (prix d'achat pondéré par la quantité initiale)
        BigDecimal pamp = calculerPamp(lots, mp.getCoutUnitaire());

        // Règle FIFO : le plus ancien lot non vide est en tête de pile
        boolean teteTrouvee = false;
        List<LotDTO> lotsDTO = new ArrayList<>();
        for (LotMp lot : lots) {
            String statutLot;
            if (lot.getQuantiteRestante().signum() == 0) {
                statutLot = "ÉPUISÉ";
            } else if (!teteTrouvee) {
                statutLot = "EN TÊTE DE PILE";
                teteTrouvee = true;
            } else {
                statutLot = "EN ATTENTE";
            }
            String fournisseurNom = lot.getFournisseur() != null
                    ? lot.getFournisseur().getNom()
                    : mp.getFournisseur().getNom();
            BigDecimal coutLot = lot.getCoutUnitaire() != null
                    ? lot.getCoutUnitaire()
                    : mp.getCoutUnitaire();
            lotsDTO.add(new LotDTO(lot.getId(), lot.getDateAchat(), lot.getQuantiteRestante(),
                    statutLot, fournisseurNom, coutLot));
        }

        BigDecimal suggestion = calculerSuggestion(mp.getId(), stock, mp.getSeuilMinimum());

        return new FicheDetailDTO(mp.getId(), mp.getCode(), mp.getNom(),
                mp.getFournisseur().getId(), mp.getFournisseur().getNom(),
                mp.getUnite().getId(), mp.getUnite().getLibelle(),
                mp.getCoutUnitaire(), mp.getSeuilMinimum(), stock,
                pamp, lotsDTO, suggestion);
    }

    private String statut(BigDecimal stock, BigDecimal seuil) {
        if (seuil != null && stock.compareTo(seuil) <= 0) {
            return STATUT_ALERTE;
        }
        return STATUT_OK;
    }

    // PAMP = Σ(coutLot × quantiteInitiale) / Σ(quantiteInitiale)
    private BigDecimal calculerPamp(List<LotMp> lots, BigDecimal coutDefaut) {
        if (lots.isEmpty()) {
            return coutDefaut;
        }
        BigDecimal totalValeur = BigDecimal.ZERO;
        BigDecimal totalQte = BigDecimal.ZERO;
        for (LotMp lot : lots) {
            BigDecimal cout = lot.getCoutUnitaire() != null ? lot.getCoutUnitaire() : coutDefaut;
            totalValeur = totalValeur.add(cout.multiply(lot.getQuantiteInitiale()));
            totalQte = totalQte.add(lot.getQuantiteInitiale());
        }
        if (totalQte.signum() == 0) {
            return coutDefaut;
        }
        return totalValeur.divide(totalQte, 2, RoundingMode.HALF_UP);
    }

    // RG03 — suggestion basée sur la consommation moyenne observée (sorties des 30 derniers jours extrapolées)
    private BigDecimal calculerSuggestion(Integer idMp, BigDecimal stock, BigDecimal seuilMinimum) {
        BigDecimal totalSorties = Objects.requireNonNullElse(
                mouvementStockMpRepository.sommeTotalSorties(idMp), BigDecimal.ZERO);
        LocalDate premiereSortie = mouvementStockMpRepository.premiereDateSortieAvecDate(idMp);

        if (premiereSortie == null || totalSorties.signum() == 0) {
            // Pas d'historique : suggérer de combler le déficit par rapport au seuil minimum
            if (seuilMinimum == null) return BigDecimal.ZERO;
            return seuilMinimum.subtract(stock).max(BigDecimal.ZERO).setScale(2, RoundingMode.CEILING);
        }

        long jours = ChronoUnit.DAYS.between(premiereSortie, LocalDate.now());
        if (jours == 0) jours = 1;

        BigDecimal consommation30Jours = totalSorties
                .divide(BigDecimal.valueOf(jours), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(30))
                .setScale(2, RoundingMode.CEILING);

        // Cible = stock cible pour 30 jours, au moins le seuil minimum
        BigDecimal cible = seuilMinimum != null
                ? consommation30Jours.max(seuilMinimum)
                : consommation30Jours;
        return cible.subtract(stock).max(BigDecimal.ZERO);
    }

    // Code auto-généré au format MP-<PREMIER_MOT>-NN (ex: MP-MAIS-01), incrémenté pour rester unique.
    private String genererCode(String nom) {
        String premierMot = nom.trim().split("\\s+")[0];
        String base = Normalizer.normalize(premierMot, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]", "")
                .toUpperCase();
        String prefixe = "MP-" + base + "-";
        long numero = matierePremiereRepository.countByCodeStartingWith(prefixe) + 1;
        return prefixe + String.format("%02d", numero);
    }
}