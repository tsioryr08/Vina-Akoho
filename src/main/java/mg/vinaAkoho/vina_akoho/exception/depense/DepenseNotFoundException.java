package mg.vinaAkoho.vina_akoho.exception.depense;

import lombok.Getter;

@Getter
public class DepenseNotFoundException extends RuntimeException {

    private final Integer depenseId;

    private DepenseNotFoundException(Integer depenseId) {
        super("Dépense introuvable pour l'identifiant : " + depenseId);
        this.depenseId = depenseId;
    }

    public static DepenseNotFoundException parId(Integer depenseId) {
        return new DepenseNotFoundException(depenseId);
    }
}
