package mg.vinaAkoho.vina_akoho.exception.depense;

import lombok.Getter;

@Getter
public class CategorieDepenseNotFoundException extends RuntimeException {

    private final Integer categorieDepenseId;

    private CategorieDepenseNotFoundException(Integer categorieDepenseId) {
        super("Catégorie de dépense introuvable pour l'identifiant : " + categorieDepenseId);
        this.categorieDepenseId = categorieDepenseId;
    }

    public static CategorieDepenseNotFoundException parId(Integer categorieDepenseId) {
        return new CategorieDepenseNotFoundException(categorieDepenseId);
    }
}
