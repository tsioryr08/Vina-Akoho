package mg.vinaAkoho.vina_akoho.controller.imports;

import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.imports.ImportResultatDTO;
import mg.vinaAkoho.vina_akoho.service.imports.ImportClientService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RestController
@RequestMapping("/api/clients/import")
public class ImportClientController {

    private final ImportClientService importService;

    public ImportClientController(ImportClientService importService) {
        this.importService = importService;
    }

    @PostMapping("/apercu")
    public ApiResponse<ImportResultatDTO<String>> apercu(@RequestParam("fichier") MultipartFile fichier) {
        try {
            return ApiResponse.success("Analyse terminée", importService.apercu(fichier));
        } catch (Exception e) {
            return ApiResponse.error("Erreur de lecture du fichier : " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<ImportResultatDTO<String>> importer(
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam(defaultValue = "false") boolean forcerDoublons) {
        try {
            return ApiResponse.success("Import terminé", importService.importer(fichier, forcerDoublons));
        } catch (Exception e) {
            return ApiResponse.error("Erreur lors de l'import : " + e.getMessage());
        }
    }
    @GetMapping("/modele")
    public void telechargerModele(HttpServletResponse response) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Clients");
            Row header = sheet.createRow(0);
            String[] colonnes = {"Nom", "Prénom", "Téléphone", "Adresse", "Zone livraison", "Notes", "Service", "Type client", "Taille cheptel"};
            for (int i = 0; i < colonnes.length; i++) {
                header.createCell(i).setCellValue(colonnes[i]);
                sheet.setColumnWidth(i, 5500);
            }
            Row exemple = sheet.createRow(1);
            exemple.createCell(0).setCellValue("Rakoto");
            exemple.createCell(1).setCellValue("Jean");
            exemple.createCell(2).setCellValue("0341234567");
            exemple.createCell(3).setCellValue("Mahitsy");
            exemple.createCell(4).setCellValue("MAHITSY");
            exemple.createCell(5).setCellValue("");
            exemple.createCell(6).setCellValue("Vente");
            exemple.createCell(7).setCellValue("Éleveur");
            exemple.createCell(8).setCellValue(100);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=modele-clients.xlsx");
            wb.write(response.getOutputStream());
        }
    }
}