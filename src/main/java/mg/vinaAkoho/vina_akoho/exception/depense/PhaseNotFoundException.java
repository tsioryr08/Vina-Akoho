package mg.vinaAkoho.vina_akoho.exception.depense;

import lombok.Getter;

@Getter
public class PhaseNotFoundException extends RuntimeException {

    private final Integer phaseId;

    private PhaseNotFoundException(Integer phaseId) {
        super("Phase introuvable pour l'identifiant : " + phaseId);
        this.phaseId = phaseId;
    }

    public static PhaseNotFoundException parId(Integer phaseId) {
        return new PhaseNotFoundException(phaseId);
    }
}
