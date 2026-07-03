package mg.vinaAkoho.vina_akoho.exception.livraison;

public class VenteNotFoundException extends RuntimeException {

    public VenteNotFoundException(Long id) {
        super("Vente introuvable avec l'identifiant : " + id);
    }

    public VenteNotFoundException(String message) {
        super(message);
    }

    public static VenteNotFoundException parId(Long id) {
        return new VenteNotFoundException("Vente introuvable avec l'identifiant : " + id);
    }
}
