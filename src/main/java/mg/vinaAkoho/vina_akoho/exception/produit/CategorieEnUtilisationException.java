package mg.vinaAkoho.vina_akoho.exception.produit;

/**
 * Levée lorsqu'on tente de supprimer une catégorie encore référencée
 * par au moins un produit (cohérent avec la contrainte FK ON DELETE RESTRICT
 * de la table produit -> categorie).
 */
public class CategorieEnUtilisationException extends RuntimeException {

    public CategorieEnUtilisationException(String message) {
        super(message);
    }
}
