package mg.vinaAkoho.vina_akoho.exception.clients;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(String message) {
        super(message);
    }

    public static ClientNotFoundException parId(Integer id) {
        return new ClientNotFoundException("Aucun client trouvé avec l'id : " + id);
    }
}
