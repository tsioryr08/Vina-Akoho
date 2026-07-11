package mg.vinaAkoho.vina_akoho.dto.imports;

public class LigneDoublonImportDTO {
    private int numeroLigne;
    private String cleConflit;   // ex: le nom/téléphone en conflit
    private String message;

    public LigneDoublonImportDTO(int numeroLigne, String cleConflit, String message) {
        this.numeroLigne = numeroLigne;
        this.cleConflit = cleConflit;
        this.message = message;
    }
    public int getNumeroLigne() { return numeroLigne; }
    public String getCleConflit() { return cleConflit; }
    public String getMessage() { return message; }
}