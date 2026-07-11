package mg.vinaAkoho.vina_akoho.dto.imports;

import java.util.ArrayList;
import java.util.List;

public class ImportResultatDTO<T> {
    private List<T> lignesImportees = new ArrayList<>();
    private List<LigneErreurImportDTO> erreurs = new ArrayList<>();
    private List<LigneDoublonImportDTO> doublons = new ArrayList<>();
    private int totalLignesLues;

    public boolean isConfirmationRequise() { return !doublons.isEmpty(); }

    public List<T> getLignesImportees() { return lignesImportees; }
    public List<LigneErreurImportDTO> getErreurs() { return erreurs; }
    public List<LigneDoublonImportDTO> getDoublons() { return doublons; }
    public int getTotalLignesLues() { return totalLignesLues; }
    public void setTotalLignesLues(int totalLignesLues) { this.totalLignesLues = totalLignesLues; }
}