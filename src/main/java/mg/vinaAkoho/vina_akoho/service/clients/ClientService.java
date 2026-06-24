package mg.vinaAkoho.vina_akoho.service.clients;

import java.util.Optional;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientConnexionDTO;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientInscriptionDTO;
import mg.vinaAkoho.vina_akoho.entity.clients.Client;
import mg.vinaAkoho.vina_akoho.entity.clients.ServiceClient;
import mg.vinaAkoho.vina_akoho.entity.clients.TypeClient;
import mg.vinaAkoho.vina_akoho.repository.clients.ClientRepository;
import mg.vinaAkoho.vina_akoho.repository.clients.ServiceClientRepository;
import mg.vinaAkoho.vina_akoho.repository.clients.TypeClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            return clientRepository.findByNumeroTelephoneAndActifTrue(dto.getNumeroTelephone());
        }

        return clientRepository.findByIdAndNumeroTelephoneAndActifTrue(
                dto.getIdClient(),
                dto.getNumeroTelephone()
        );
    }

    public Optional<Client> rechercherParTelephone(String numeroTelephone) {
        return clientRepository.findByNumeroTelephoneAndActifTrue(numeroTelephone);
    }

    @Transactional
    public Client inscrire(ClientInscriptionDTO dto) {
        Optional<Client> clientExistant = clientRepository.findByNumeroTelephoneAndActifTrue(dto.getNumeroTelephone());
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
        client.setIdService(service.getId());
        client.setIdTypeClient(typeClient.getId());
        client.setActif(true);

        return clientRepository.save(client);
    }
}
