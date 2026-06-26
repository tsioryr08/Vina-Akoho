package mg.vinaAkoho.vina_akoho.controller.clients;

import jakarta.validation.Valid;
import java.util.List;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientGestionDTO;
import mg.vinaAkoho.vina_akoho.dto.clients.ClientRequestDTO;
import mg.vinaAkoho.vina_akoho.service.clients.ClientService;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = {"/clients", "/api/clients"},
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ClientGestionDTO createClient(@Valid @RequestBody ClientRequestDTO request) {
        return clientService.createClient(request);
    }

    @GetMapping
    public List<ClientGestionDTO> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{id}")
    public ClientGestionDTO getClientById(@PathVariable Integer id) {
        return clientService.getClientById(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientGestionDTO updateClient(@PathVariable Integer id,
                                         @Valid @RequestBody ClientRequestDTO request) {
        return clientService.updateClient(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Integer id) {
        clientService.deleteClient(id);
    }
}
