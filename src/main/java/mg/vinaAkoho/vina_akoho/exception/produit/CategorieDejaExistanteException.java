package mg.vinaAkoho.vina_akoho.exception.produit;

/**
 * Levée lorsqu'on tente de créer/modifier une catégorie avec un libellé
 * déjà utilisé par une autre catégorie.
 */
public class CategorieDejaExistanteException extends RuntimeException {

    public CategorieDejaExistanteException(String message) {
        super(message);
    }
}
