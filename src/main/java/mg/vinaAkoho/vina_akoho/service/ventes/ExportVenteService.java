package mg.vinaAkoho.vina_akoho.service.ventes;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportVenteService {

    public byte[] exporterVentesExcel(List<VenteDTO> ventes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ventes");
            
            // Créer le style pour l'en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Créer l'en-tête
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Client", "Date", "Mode Paiement", "Statut", "Montant Total"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Remplir les données
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            int rowNum = 1;
            for (VenteDTO vente : ventes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(vente.getId());
                row.createCell(1).setCellValue(vente.getClientNom() + " " + vente.getClientPrenom());
                row.createCell(2).setCellValue(vente.getDateVente().format(dateFormatter));
                row.createCell(3).setCellValue(vente.getModePaiement());
                row.createCell(4).setCellValue(vente.getStatutVente());
                row.createCell(5).setCellValue(vente.getMontantTotal().doubleValue());
            }
            
            // Auto-size les colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}
