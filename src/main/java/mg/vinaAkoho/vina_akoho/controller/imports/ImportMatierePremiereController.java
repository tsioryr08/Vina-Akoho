package mg.vinaAkoho.vina_akoho.controller.imports;

import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.imports.ImportResultatDTO;
import mg.vinaAkoho.vina_akoho.service.imports.ImportMatierePremiereService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RestController
@RequestMapping("/api/matieres-premieres/import")
public class ImportMatierePremiereController {

    private final ImportMatierePremiereService importService;

    public ImportMatierePremiereController(ImportMatierePremiereService importService) {
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
            Sheet sheet = wb.createSheet("Matières premières");
            Row header = sheet.createRow(0);
            String[] colonnes = {"Nom", "Fournisseur", "Coût unitaire", "Unité", "Seuil minimum"};
            for (int i = 0; i < colonnes.length; i++) {
                header.createCell(i).setCellValue(colonnes[i]);
                sheet.setColumnWidth(i, 6000);
            }
            Row exemple = sheet.createRow(1);
            exemple.createCell(0).setCellValue("Maïs jaune");
            exemple.createCell(1).setCellValue("AGRIVET Madagascar");
            exemple.createCell(2).setCellValue(1200);
            exemple.createCell(3).setCellValue("kg");
            exemple.createCell(4).setCellValue(100);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=modele-matieres-premieres.xlsx");
            wb.write(response.getOutputStream());
        }
    }
}