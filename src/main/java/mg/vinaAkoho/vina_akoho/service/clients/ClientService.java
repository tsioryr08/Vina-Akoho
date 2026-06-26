package mg.vinaAkoho.vina_akoho.service.clients;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientConnexionDTO;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientGestionDTO;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientInscriptionDTO;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientRequestDTO;
import mg.vinaAkoho.vina_akoho.entity.clients.Client;
import mg.vinaAkoho.vina_akoho.entity.clients.ServiceClient;
import mg.vinaAkoho.vina_akoho.entity.clients.TypeClient;
import mg.vinaAkoho.vina_akoho.repository.clients.ClientRepository;
import mg.vinaAkoho.vina_akoho.repository.clients.ServiceClientRepository;
import mg.vinaAkoho.vina_akoho.repository.clients.TypeClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ServiceClientRepository serviceClientRepository;
    private final TypeClientRepository typeClientRepository;

    public ClientService(ClientRepository clientRepository,
                         ServiceClientRepository serviceClientRepository,
                         TypeClientRepository typeClientRepository) {
        this.clientRepository = clientRepository;
        this.serviceClientRepository = serviceClientRepository;
        this.typeClientRepository = typeClientRepository;
    }

    public Optional<Client> connecter(ClientConnexionDTO dto) {
        if (dto.getIdClient() == null) {
            return clientRepository.findByNumeroTelephoneAndActifTrueAndEstSupprimerFalse(dto.getNumeroTelephone());
        }

        return clientRepository.findByIdAndNumeroTelephoneAndActifTrueAndEstSupprimerFalse(
                dto.getIdClient(),
                dto.getNumeroTelephone()
        );
    }

    public Optional<Client> rechercherParTelephone(String numeroTelephone) {
        return clientRepository.findByNumeroTelephoneAndActifTrueAndEstSupprimerFalse(numeroTelephone);
    }

    @Transactional
    public Client inscrire(ClientInscriptionDTO dto) {
        Optional<Client> clientExistant = clientRepository.findByNumeroTelephoneAndActifTrueAndEstSupprimerFalse(dto.getNumeroTelephone());
        if (clientExistant.isPresent()) {
            return clientExistant.get();
        }

        ServiceClient service = serviceClientRepository.findByLibelle("Vente")
                .orElseGet(() -> serviceClientRepository.save(
                        new ServiceClient("Vente", "Service client par defaut")
                ));
        TypeClient typeClient = typeClientRepository.findByLibelle("Particulier")
                .orElseGet(() -> typeClientRepository.save(new TypeClient("Particulier")));

        Client client = new Client();
        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setNumeroTelephone(dto.getNumeroTelephone());
        client.setAdresse(dto.getAdresse());
        client.setService(service);
        client.setTypeClient(typeClient);
        client.setActif(true);
        client.setEstSupprimer(false);

        return clientRepository.save(client);
    }

    @Transactional
    public ClientGestionDTO createClient(ClientRequestDTO dto) {
        verifierClientDejaInscrit(dto.getNumeroTelephone(), null);

        ServiceClient service = findService(dto.getIdService());
        TypeClient typeClient = findTypeClient(dto.getIdTypeClient());

        Client client = new Client();
        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setDateInscription(dto.getDateInscription());
        client.setActif(dto.getIsActif() == null ? true : dto.getIsActif());
        client.setNumeroTelephone(dto.getNumeroTelephone());
        client.setAdresse(dto.getAdresse());
        client.setIdLocalite(dto.getIdLocalite());
        client.setIdZoneLivraison(dto.getIdZoneLivraison());
        client.setNotes(dto.getNotes());
        client.setService(service);
        client.setTypeClient(typeClient);
        client.setTailleCheptel(dto.getTailleCheptel());
        client.setEstSupprimer(false);

        return new ClientGestionDTO(clientRepository.save(client));
    }

    @Transactional(readOnly = true)
    public List<ClientGestionDTO> getAllClients() {
        return clientRepository.findByEstSupprimerFalse()
                .stream()
                .map(ClientGestionDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientGestionDTO getClientById(Integer id) {
        return new ClientGestionDTO(findActiveClient(id));
    }

    @Transactional
    public ClientGestionDTO updateClient(Integer id, ClientRequestDTO dto) {
        Client client = findActiveClient(id);
        verifierClientDejaInscrit(dto.getNumeroTelephone(), id);
        ServiceClient service = findService(dto.getIdService());
        TypeClient typeClient = findTypeClient(dto.getIdTypeClient());

        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setDateInscription(dto.getDateInscription() == null ? client.getDateInscription() : dto.getDateInscription());
        client.setActif(dto.getIsActif() == null ? client.getActif() : dto.getIsActif());
        client.setNumeroTelephone(dto.getNumeroTelephone());
        client.setAdresse(dto.getAdresse());
        client.setIdLocalite(dto.getIdLocalite());
        client.setIdZoneLivraison(dto.getIdZoneLivraison());
        client.setNotes(dto.getNotes());
        client.setService(service);
        client.setTypeClient(typeClient);
        client.setTailleCheptel(dto.getTailleCheptel());
        client.setUpdatedAt(LocalDateTime.now());

        return new ClientGestionDTO(clientRepository.save(client));
    }

    @Transactional
    public void deleteClient(Integer id) {
        Client client = findActiveClient(id);
        client.setEstSupprimer(true);
        client.setUpdatedAt(LocalDateTime.now());
        clientRepository.save(client);
    }

    private Client findActiveClient(Integer id) {
        return clientRepository.findByIdAndEstSupprimerFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable"));
    }

    private ServiceClient findService(Integer id) {
        return serviceClientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service introuvable"));
    }

    private TypeClient findTypeClient(Integer id) {
        return typeClientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type client introuvable"));
    }

    private void verifierClientDejaInscrit(String numeroTelephone, Integer idClientAutorise) {
        if (numeroTelephone == null || numeroTelephone.isBlank()) {
            return;
        }

        boolean existeDeja = idClientAutorise == null
                ? clientRepository.existsByNumeroTelephoneAndEstSupprimerFalse(numeroTelephone)
                : clientRepository.existsByNumeroTelephoneAndEstSupprimerFalseAndIdNot(numeroTelephone, idClientAutorise);

        if (existeDeja) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce client est deja inscrit avec ce numero telephone");
        }
    }

    private ServiceClient getOrCreateServiceClient() {
        return serviceClientRepository.findByLibelle("Vente")
                .orElseGet(() -> serviceClientRepository.save(
                        new ServiceClient("Vente", "Service client par defaut")
                ));
    }

    private TypeClient getOrCreateTypeClient(String libelle) {
        return typeClientRepository.findByLibelle(libelle)
                .orElseGet(() -> typeClientRepository.save(new TypeClient(libelle)));
    }
}
