package mg.vinaAkoho.vina_akoho.service.imports;

import mg.vinaAkoho.vina_akoho.dto.imports.*;
import mg.vinaAkoho.vina_akoho.dto.matierespremieres.FicheDetailDTO;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Fournisseur;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.MatierePremiere;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Unite;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.FournisseurRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.MatierePremiereRepository;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.UniteRepository;
import mg.vinaAkoho.vina_akoho.service.matierespremieres.MatierePremiereService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ImportMatierePremiereService {

    private final ExcelReaderUtil excel;
    private final MatierePremiereRepository matierePremiereRepository;
    private final FournisseurRepository fournisseurRepository;
    private final UniteRepository uniteRepository;
    private final MatierePremiereService matierePremiereService; // réutilise genererCode() indirectement via creer()

    public ImportMatierePremiereService(ExcelReaderUtil excel,
                                         MatierePremiereRepository matierePremiereRepository,
                                         FournisseurRepository fournisseurRepository,
                                         UniteRepository uniteRepository,
                                         MatierePremiereService matierePremiereService) {
        this.excel = excel;
        this.matierePremiereRepository = matierePremiereRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.uniteRepository = uniteRepository;
        this.matierePremiereService = matierePremiereService;
    }

    /** Étape 1 — Analyse seule, aucune sauvegarde. */
    public ImportResultatDTO<String> apercu(MultipartFile fichier) throws Exception {
        return traiter(fichier, false, true);
    }

    /** Étape 2 — Import réel. forcerDoublons=true réimporte aussi les lignes en conflit. */
    @Transactional
    public ImportResultatDTO<String> importer(MultipartFile fichier, boolean forcerDoublons) throws Exception {
        return traiter(fichier, forcerDoublons, false);
    }

    private ImportResultatDTO<String> traiter(MultipartFile fichier, boolean forcerDoublons, boolean dryRun) throws Exception {
        ImportResultatDTO<String> resultat = new ImportResultatDTO<>();

        try (Workbook wb = excel.ouvrir(fichier.getInputStream())) {
            Sheet feuille = wb.getSheetAt(0);
            int dernierIndex = feuille.getLastRowNum();
            int lignesLues = 0;

            for (int i = 1; i <= dernierIndex; i++) { // ligne 0 = en-têtes
                Row row = feuille.getRow(i);
                if (excel.ligneVide(row, 5)) continue;
                lignesLues++;
                int numeroLigne = i + 1; // pour l'utilisateur (Excel commence à 1)

                String nom = excel.texte(row, 0);
                String nomFournisseur = excel.texte(row, 1);
                BigDecimal coutUnitaire = excel.decimal(row, 2);
                String libelleUnite = excel.texte(row, 3);
                BigDecimal seuilMinimum = excel.decimal(row, 4);

                // --- Validation ---
                if (nom == null) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "Le nom est obligatoire"));
                    continue;
                }
                if (nomFournisseur == null) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "Le fournisseur est obligatoire"));
                    continue;
                }
                if (coutUnitaire == null || coutUnitaire.signum() <= 0) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "Le coût unitaire doit être un nombre positif"));
                    continue;
                }
                if (libelleUnite == null) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "L'unité est obligatoire"));
                    continue;
                }

                Optional<Fournisseur> fournisseur = fournisseurRepository.findByNomIgnoreCase(nomFournisseur);
                if (fournisseur.isEmpty()) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne,
                            "Fournisseur inconnu : '" + nomFournisseur + "' (créez-le d'abord)"));
                    continue;
                }
                Optional<Unite> unite = uniteRepository.findByLibelle(libelleUnite);
                if (unite.isEmpty()) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne,
                            "Unité inconnue : '" + libelleUnite + "'"));
                    continue;
                }

                // --- Détection de doublon (par nom) ---
                boolean estDoublon = matierePremiereRepository.findByNomIgnoreCase(nom).isPresent();
                if (estDoublon && !forcerDoublons) {
                    resultat.getDoublons().add(new LigneDoublonImportDTO(numeroLigne, nom,
                            "Une matière première nommée '" + nom + "' existe déjà"));
                    continue;
                }

                // --- Sauvegarde (sauf en mode aperçu) ---
                if (!dryRun) {
                    MatierePremiere mp = new MatierePremiere();
                    mp.setNom(nom);
                    mp.setFournisseur(fournisseur.get());
                    mp.setCoutUnitaire(coutUnitaire);
                    mp.setUnite(unite.get());
                    mp.setSeuilMinimum(seuilMinimum);
                    mp.setCode(genererCodeSimple(nom)); // même logique que MatierePremiereService.genererCode
                    matierePremiereRepository.save(mp);
                }
                resultat.getLignesImportees().add(nom);
            }
            resultat.setTotalLignesLues(lignesLues);
        }
        return resultat;
    }

    // Duplique volontairement la logique privée de genererCode() de MatierePremiereService
    // (celle-ci est `private`, non réutilisable telle quelle) — même format MP-<MOT>-NN.
    private String genererCodeSimple(String nom) {
        String premierMot = nom.trim().split("\\s+")[0];
        String base = java.text.Normalizer.normalize(premierMot, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]", "")
                .toUpperCase();
        String prefixe = "MP-" + base + "-";
        long numero = matierePremiereRepository.countByCodeStartingWith(prefixe) + 1;
        return prefixe + String.format("%02d", numero);
    }
}