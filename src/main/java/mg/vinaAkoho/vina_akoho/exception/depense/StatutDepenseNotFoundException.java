package mg.vinaAkoho.vina_akoho.exception.depense;

import lombok.Getter;

@Getter
public class StatutDepenseNotFoundException extends RuntimeException {

    private final Integer statutDepenseId;

    private StatutDepenseNotFoundException(Integer statutDepenseId) {
        super("Statut de dépense introuvable pour l'identifiant : " + statutDepenseId);
        this.statutDepenseId = statutDepenseId;
    }

    public static StatutDepenseNotFoundException parId(Integer statutDepenseId) {
        return new StatutDepenseNotFoundException(statutDepenseId);
    }
}
