package mg.vinaAkoho.vina_akoho.exception.ventes;

public class VenteNotFoundException extends RuntimeException {

    public VenteNotFoundException(String message) {
        super(message);
    }

    public static VenteNotFoundException parId(Long id) {
        return new VenteNotFoundException("Aucune vente trouvée avec l'id : " + id);
    }
}
