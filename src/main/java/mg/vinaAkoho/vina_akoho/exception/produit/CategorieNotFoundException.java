package mg.vinaAkoho.vina_akoho.exception.produit;

/**
 * Levée lorsqu'une catégorie demandée n'existe pas en base.
 */
public class CategorieNotFoundException extends RuntimeException {

    public CategorieNotFoundException(String message) {
        super(message);
    }

    public static CategorieNotFoundException parId(Long id) {
        return new CategorieNotFoundException("Aucune catégorie trouvée avec l'id : " + id);
    }
}
