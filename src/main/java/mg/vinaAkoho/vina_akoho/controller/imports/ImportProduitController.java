package mg.vinaAkoho.vina_akoho.controller.imports;

import mg.vinaAkoho.vina_akoho.dto.ApiResponse;
import mg.vinaAkoho.vina_akoho.dto.imports.ImportResultatDTO;
import mg.vinaAkoho.vina_akoho.service.imports.ImportProduitService;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RestController
@RequestMapping("/api/produits/import")
public class ImportProduitController {

    private final ImportProduitService importService;

    public ImportProduitController(ImportProduitService importService) {
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
            Sheet sheet = wb.createSheet("Produits");
            Row header = sheet.createRow(0);
            String[] colonnes = {"Référence (optionnel)", "Catégorie", "Nom", "Prix de vente", "Seuil d'alerte"};
            for (int i = 0; i < colonnes.length; i++) {
                header.createCell(i).setCellValue(colonnes[i]);
                sheet.setColumnWidth(i, 6500);
            }
            Row exemple = sheet.createRow(1);
            exemple.createCell(0).setCellValue("");
            exemple.createCell(1).setCellValue("Poussin");
            exemple.createCell(2).setCellValue("Aliment Poussin 10kg");
            exemple.createCell(3).setCellValue(25000);
            exemple.createCell(4).setCellValue(10);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=modele-produits.xlsx");
            wb.write(response.getOutputStream());
        }
    }
}