package mg.vinaAkoho.vina_akoho.exception.livraison;

public class LivraisonNotFoundException extends RuntimeException {

    public LivraisonNotFoundException(Long id) {
        super("Livraison introuvable avec l'identifiant : " + id);
    }

    public LivraisonNotFoundException(String message) {
        super(message);
    }

    public static LivraisonNotFoundException parId(Long id) {
        return new LivraisonNotFoundException("Livraison introuvable avec l'identifiant : " + id);
    }
}
