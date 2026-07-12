package mg.vinaAkoho.vina_akoho.service.imports;

import mg.vinaAkoho.vina_akoho.dto.imports.*;
import mg.vinaAkoho.vina_akoho.entity.produit.Categorie;
import mg.vinaAkoho.vina_akoho.entity.produit.Produit;
import mg.vinaAkoho.vina_akoho.repository.produit.CategorieRepository;
import mg.vinaAkoho.vina_akoho.repository.produit.ProduitRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ImportProduitService {

    private final ExcelReaderUtil excel;
    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;

    public ImportProduitService(ExcelReaderUtil excel, ProduitRepository produitRepository,
                                 CategorieRepository categorieRepository) {
        this.excel = excel;
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
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
                if (excel.ligneVide(row, 5)) continue;
                lignesLues++;
                int numeroLigne = i + 1;

                String ref = excel.texte(row, 0); // peut être null -> auto-généré
                String libelleCategorie = excel.texte(row, 1);
                String nom = excel.texte(row, 2);
                BigDecimal prixVente = excel.decimal(row, 3);
                Integer seuilAlerte = excel.entier(row, 4);

                if (libelleCategorie == null) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "La catégorie est obligatoire"));
                    continue;
                }
                if (nom == null) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "Le nom est obligatoire"));
                    continue;
                }
                if (prixVente == null || prixVente.signum() < 0) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "Le prix de vente doit être un nombre positif ou nul"));
                    continue;
                }

                List<Categorie> categories = categorieRepository.findAll();
                Optional<Categorie> categorie = categories.stream()
                        .filter(c -> c.getLibelle().equalsIgnoreCase(libelleCategorie))
                        .findFirst();
                if (categorie.isEmpty()) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne,
                            "Catégorie inconnue : '" + libelleCategorie + "'"));
                    continue;
                }

                boolean estDoublon = produitRepository.existsByNomIgnoreCaseAndActifTrue(nom)
                        || (ref != null && produitRepository.existsByRefIgnoreCaseAndActifTrue(ref));
                if (estDoublon && !forcerDoublons) {
                    resultat.getDoublons().add(new LigneDoublonImportDTO(numeroLigne, nom,
                            "Un produit nommé '" + nom + "'"
                                    + (ref != null ? " ou de référence '" + ref + "'" : "") + " existe déjà"));
                    continue;
                }

                if (!dryRun) {
                    Produit produit = new Produit();
                    produit.setRef(ref != null ? ref : genererRefAuto());
                    produit.setCategorie(categorie.get());
                    produit.setNom(nom);
                    produit.setPrixVente(prixVente);
                    produit.setSeuilAlerte(seuilAlerte);
                    produit.setActif(true);
                    produitRepository.save(produit);
                }
                resultat.getLignesImportees().add(nom);
            }
            resultat.setTotalLignesLues(lignesLues);
        }
        return resultat;
    }

    // Réplique simplifiée de ProduitService.genererReferenceAutomatique() (privée, non réutilisable telle quelle)
    private String genererRefAuto() {
        String maxRef = produitRepository.findMaxRefLikePRD();
        if (maxRef == null || maxRef.isEmpty()) return "PRD-PF-001";
        try {
            String[] parts = maxRef.split("-");
            int number = Integer.parseInt(parts[parts.length - 1]) + 1;
            return String.format("PRD-PF-%03d", number);
        } catch (Exception e) {
            return "PRD-PF-001";
        }
    }
}