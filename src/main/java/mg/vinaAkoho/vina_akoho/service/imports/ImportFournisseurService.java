package mg.vinaAkoho.vina_akoho.service.imports;

import mg.vinaAkoho.vina_akoho.dto.imports.*;
import mg.vinaAkoho.vina_akoho.entity.matierespremieres.Fournisseur;
import mg.vinaAkoho.vina_akoho.repository.matierespremieres.FournisseurRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportFournisseurService {

    private final ExcelReaderUtil excel;
    private final FournisseurRepository fournisseurRepository;

    public ImportFournisseurService(ExcelReaderUtil excel, FournisseurRepository fournisseurRepository) {
        this.excel = excel;
        this.fournisseurRepository = fournisseurRepository;
    }

    public ImportResultatDTO<String> apercu(MultipartFile fichier) throws Exception {
        return traiter(fichier, false, true);
    }

    @Transactional
    public ImportResultatDTO<String> importer(MultipartFile fichier, boolean forcerDoublons) throws Exception {
        return traiter(fichier, forcerDoublons, false);
    }

    private ImportResultatDTO<String> traiter(MultipartFile fichier, boolean forcerDoublons, boolean dryRun) throws Exception {
        ImportResultatDTO<String> resultat = new ImportResultatDTO<>();

        try (Workbook wb = excel.ouvrir(fichier.getInputStream())) {
            Sheet feuille = wb.getSheetAt(0);
            int lignesLues = 0;

            for (int i = 1; i <= feuille.getLastRowNum(); i++) {
                Row row = feuille.getRow(i);
                if (excel.ligneVide(row, 3)) continue;
                lignesLues++;
                int numeroLigne = i + 1;

                String nom = excel.texte(row, 0);
                String email = excel.texte(row, 1);
                String telephone = excel.texte(row, 2);

                if (nom == null) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "Le nom est obligatoire"));
                    continue;
                }

                boolean estDoublon = fournisseurRepository.findByNomIgnoreCase(nom).isPresent();
                if (estDoublon && !forcerDoublons) {
                    resultat.getDoublons().add(new LigneDoublonImportDTO(numeroLigne, nom,
                            "Un fournisseur nommé '" + nom + "' existe déjà"));
                    continue;
                }

                if (!dryRun) {
                    Fournisseur f = new Fournisseur();
                    f.setNom(nom);
                    f.setEmail(email);
                    f.setTelephone(telephone);
                    fournisseurRepository.save(f);
                }
                resultat.getLignesImportees().add(nom);
            }
            resultat.setTotalLignesLues(lignesLues);
        }
        return resultat;
    }
}