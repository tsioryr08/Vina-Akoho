package mg.vinaAkoho.vina_akoho.exception.produit;

/**
 * Levée lorsqu'on tente de créer/modifier un produit avec une référence
 * ou un nom déjà utilisé par un autre produit.
 */
public class ProduitDejaExistantException extends RuntimeException {

    public ProduitDejaExistantException(String message) {
        super(message);
    }
}
