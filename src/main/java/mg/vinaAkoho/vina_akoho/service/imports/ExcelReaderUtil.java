package mg.vinaAkoho.vina_akoho.service.imports;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;

@Component
public class ExcelReaderUtil {

    public Workbook ouvrir(InputStream fichier) throws Exception {
        return WorkbookFactory.create(fichier);
    }

    public String texte(Row row, int colonne) {
        Cell cell = row.getCell(colonne);
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        String valeur = cell.getStringCellValue();
        return (valeur == null || valeur.isBlank()) ? null : valeur.trim();
    }

    public BigDecimal decimal(Row row, int colonne) {
        Cell cell = row.getCell(colonne);
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.STRING) {
                String v = cell.getStringCellValue().trim();
                return v.isEmpty() ? null : new BigDecimal(v.replace(",", "."));
            }
            return BigDecimal.valueOf(cell.getNumericCellValue());
        } catch (Exception e) {
            return null;
        }
    }

    public Integer entier(Row row, int colonne) {
        BigDecimal d = decimal(row, colonne);
        return d == null ? null : d.intValue();
    }

    public boolean ligneVide(Row row, int nbColonnes) {
        if (row == null) return true;
        for (int i = 0; i < nbColonnes; i++) {
            if (texte(row, i) != null) return false;
        }
        return true;
    }
}