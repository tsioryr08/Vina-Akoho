package mg.vinaAkoho.vina_akoho.service.matierespremieres;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
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
        lotMpRepository.save(lot);

        MouvementStockMp mouvement = new MouvementStockMp();
        mouvement.setTypeMouvement(entree);
        mouvement.setLotMp(lot);
        mouvement.setQuantite(dto.quantite());
        mouvement.setUnite(mp.getUnite());
        mouvement.setIdEmploye(dto.idEmploye());
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
        // PAMP = coût unitaire : le prix d'achat est verrouillé à la création, tous les lots ont donc le même coût.
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
        // FIFO : le plus ancien lot non vide est en tête de pile (à épuiser en premier).
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
            lotsDTO.add(new LotDTO(lot.getId(), lot.getDateAchat(), lot.getQuantiteRestante(), statutLot));
        }
        return new FicheDetailDTO(mp.getId(), mp.getCode(), mp.getNom(), mp.getFournisseur().getNom(),
                mp.getUnite().getLibelle(), mp.getCoutUnitaire(), mp.getSeuilMinimum(), stock,
                mp.getCoutUnitaire(), lotsDTO);
    }

    private String statut(BigDecimal stock, BigDecimal seuil) {
        if (seuil != null && stock.compareTo(seuil) <= 0) {
            return STATUT_ALERTE;
        }
        return STATUT_OK;
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
