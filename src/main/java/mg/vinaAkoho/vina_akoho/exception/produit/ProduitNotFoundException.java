package mg.vinaAkoho.vina_akoho.exception.produit;

/**
 * Levée lorsqu'un produit demandé n'existe pas en base.
 */
public class ProduitNotFoundException extends RuntimeException {

    public ProduitNotFoundException(String message) {
        super(message);
    }

    public static ProduitNotFoundException parId(Long id) {
        return new ProduitNotFoundException("Aucun produit trouvé avec l'id : " + id);
    }
}
