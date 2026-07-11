package mg.vinaAkoho.vina_akoho.dto.imports;

public class LigneErreurImportDTO {
    private int numeroLigne;
    private String message;

    public LigneErreurImportDTO(int numeroLigne, String message) {
        this.numeroLigne = numeroLigne;
        this.message = message;
    }
    public int getNumeroLigne() { return numeroLigne; }
    public String getMessage() { return message; }
}