package mg.vinaAkoho.vina_akoho.controller.imports;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.imports.ImportResultatDTO;
import mg.vinaAkoho.vina_akoho.service.imports.ImportFournisseurService;

@RestController
@RequestMapping("/fournisseurs/import")
public class ImportFournisseurController {

    private final ImportFournisseurService importService;

    public ImportFournisseurController(ImportFournisseurService importService) {
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
            Sheet sheet = wb.createSheet("Fournisseurs");
            Row header = sheet.createRow(0);
            String[] colonnes = {"Nom", "Email", "Téléphone"};
            for (int i = 0; i < colonnes.length; i++) {
                header.createCell(i).setCellValue(colonnes[i]);
                sheet.setColumnWidth(i, 6000);
            }
            Row exemple = sheet.createRow(1);
            exemple.createCell(0).setCellValue("AGRIVET Madagascar");
            exemple.createCell(1).setCellValue("contact@agrivet.mg");
            exemple.createCell(2).setCellValue("0341234567");

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=modele-fournisseurs.xlsx");
            wb.write(response.getOutputStream());
        }
    }
}