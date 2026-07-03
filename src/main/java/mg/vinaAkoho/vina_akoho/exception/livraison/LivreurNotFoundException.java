package mg.vinaAkoho.vina_akoho.exception.livraison;

public class LivreurNotFoundException extends RuntimeException {

    public LivreurNotFoundException(Long id) {
        super("Livreur introuvable avec l'identifiant : " + id);
    }

    public LivreurNotFoundException(String message) {
        super(message);
    }

    public static LivreurNotFoundException parId(Long id) {
        return new LivreurNotFoundException("Livreur introuvable avec l'identifiant : " + id);
    }
}
