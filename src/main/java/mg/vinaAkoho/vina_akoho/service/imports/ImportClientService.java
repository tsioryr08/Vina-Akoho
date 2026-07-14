package mg.vinaAkoho.vina_akoho.service.imports;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import mg.vinaAkoho.vina_akoho.dto.imports.ImportResultatDTO;
import mg.vinaAkoho.vina_akoho.dto.imports.LigneDoublonImportDTO;
import mg.vinaAkoho.vina_akoho.dto.imports.LigneErreurImportDTO;
import mg.vinaAkoho.vina_akoho.entity.clients.Client;
import mg.vinaAkoho.vina_akoho.entity.clients.ServiceClient;
import mg.vinaAkoho.vina_akoho.entity.clients.TypeClient;
import mg.vinaAkoho.vina_akoho.repository.clients.ClientRepository;
import mg.vinaAkoho.vina_akoho.repository.clients.ServiceClientRepository;
import mg.vinaAkoho.vina_akoho.repository.clients.TypeClientRepository;
import mg.vinaAkoho.vina_akoho.repository.livraison.ZoneLivraisonRepository;

@Service
public class ImportClientService {

    private final ExcelReaderUtil excel;
    private final ClientRepository clientRepository;
    private final ServiceClientRepository serviceClientRepository;
    private final TypeClientRepository typeClientRepository;
    private final ZoneLivraisonRepository zoneLivraisonRepository;

    public ImportClientService(ExcelReaderUtil excel, ClientRepository clientRepository,
                                ServiceClientRepository serviceClientRepository,
                                TypeClientRepository typeClientRepository,
                                ZoneLivraisonRepository zoneLivraisonRepository) {
        this.excel = excel;
        this.clientRepository = clientRepository;
        this.serviceClientRepository = serviceClientRepository;
        this.typeClientRepository = typeClientRepository;
        this.zoneLivraisonRepository = zoneLivraisonRepository;
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
                if (excel.ligneVide(row, 9)) continue;
                lignesLues++;
                int numeroLigne = i + 1;

                String nom = excel.texte(row, 0);
                String prenom = excel.texte(row, 1);
                String telephone = excel.texte(row, 2);
                String adresse = excel.texte(row, 3);
                String zoneLivraison = excel.texte(row, 4);
                String notes = excel.texte(row, 5);
                String libelleService = excel.texte(row, 6);
                String libelleTypeClient = excel.texte(row, 7);
                Integer tailleCheptel = excel.entier(row, 8);

                if (nom == null) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "Le nom est obligatoire"));
                    continue;
                }
                if (prenom == null) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "Le prénom est obligatoire"));
                    continue;
                }
                if (telephone == null) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "Le numéro de téléphone est obligatoire"));
                    continue;
                }
                if (libelleService == null) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "Le service est obligatoire"));
                    continue;
                }
                if (libelleTypeClient == null) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne, "Le type de client est obligatoire"));
                    continue;
                }

                Optional<ServiceClient> service = serviceClientRepository.findByLibelle(libelleService);
                if (service.isEmpty()) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne,
                            "Service inconnu : '" + libelleService + "'"));
                    continue;
                }
                Optional<TypeClient> typeClient = typeClientRepository.findByLibelle(libelleTypeClient);
                if (typeClient.isEmpty()) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne,
                            "Type de client inconnu : '" + libelleTypeClient + "'"));
                    continue;
                }
                if (zoneLivraison != null && !zoneLivraison.isBlank() && zoneLivraisonRepository.findById(zoneLivraison).isEmpty()) {
                    resultat.getErreurs().add(new LigneErreurImportDTO(numeroLigne,
                            "Zone de livraison inconnue : '" + zoneLivraison + "'"));
                    continue;
                }

                boolean estDoublon = clientRepository.existsByNumeroTelephoneAndEstSupprimerFalse(telephone);
                if (estDoublon && !forcerDoublons) {
                    resultat.getDoublons().add(new LigneDoublonImportDTO(numeroLigne, telephone,
                            "Un client avec le numéro '" + telephone + "' existe déjà"));
                    continue;
                }

                if (!dryRun) {
                    Client client = new Client();
                    client.setNom(nom);
                    client.setPrenom(prenom);
                    client.setNumeroTelephone(telephone);
                    client.setAdresse(adresse);
                    client.setIdZoneLivraison(zoneLivraison);
                    client.setNotes(notes);
                    client.setService(service.get());
                    client.setTypeClient(typeClient.get());
                    client.setTailleCheptel(tailleCheptel);
                    client.setActif(true);
                    client.setEstSupprimer(false);
                    clientRepository.save(client);
                }
                resultat.getLignesImportees().add(nom + " " + prenom);
            }
            resultat.setTotalLignesLues(lignesLues);
        }
        return resultat;
    }
}