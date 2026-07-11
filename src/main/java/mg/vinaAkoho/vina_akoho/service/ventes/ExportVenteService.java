package mg.vinaAkoho.vina_akoho.service.ventes;

import lombok.RequiredArgsConstructor;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.ProduitVenduExportDTO;
import mg.vinaAkoho.vina_akoho.dto.ventes.VenteListeExportDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
@RequiredArgsConstructor
public class ExportVenteService {

    public byte[] exporterVentesExcel(List<VenteDTO> ventes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ventes");
            
            // Créer le style pour l'en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
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

    public byte[] exporterVentesPdf(List<VenteDTO> ventes) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("Export des ventes", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(18);
        document.add(title);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 3.5f, 3.2f, 2.8f, 2.2f, 2.2f});

        String[] columns = {"ID", "Client", "Date", "Mode Paiement", "Statut", "Montant Total"};

        for (String col : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
            cell.setBackgroundColor(new GrayColor(0.8f));
            table.addCell(cell);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (VenteDTO vente : ventes) {
            table.addCell(new Phrase(String.valueOf(vente.getId()), dataFont));
            table.addCell(new Phrase((vente.getClientNom() != null ? vente.getClientNom() : "")
                    + (vente.getClientPrenom() != null ? " " + vente.getClientPrenom() : ""), dataFont));
            table.addCell(new Phrase(vente.getDateVente() != null ? vente.getDateVente().format(dateFormatter) : "", dataFont));
            table.addCell(new Phrase(vente.getModePaiement() != null ? vente.getModePaiement() : "", dataFont));
            table.addCell(new Phrase(vente.getStatutVente() != null ? vente.getStatutVente() : "", dataFont));
            table.addCell(new Phrase(vente.getMontantTotal() != null ? vente.getMontantTotal().toString() : "0", dataFont));
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    public byte[] exporterProduitsExcel(List<ProduitVenduExportDTO> produits) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Produits les plus vendus");

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Produit", "Quantité", "Chiffre d'affaires (Ar)", "Part du CA"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (ProduitVenduExportDTO p : produits) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.produit());
                row.createCell(1).setCellValue(p.quantite() != null ? p.quantite().doubleValue() : 0);
                row.createCell(2).setCellValue(p.chiffreAffaires() != null ? p.chiffreAffaires().doubleValue() : 0);
                row.createCell(3).setCellValue(p.partDuCA() != null ? p.partDuCA().doubleValue() : 0);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exporterProduitsPdf(List<ProduitVenduExportDTO> produits) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("Produits les plus vendus", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(18);
        document.add(title);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 3f, 4f, 2.5f});

        String[] columns = {"Produit", "Quantité", "Chiffre d'affaires (Ar)", "Part du CA"};
        for (String col : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
            cell.setBackgroundColor(new GrayColor(0.8f));
            table.addCell(cell);
        }

        for (ProduitVenduExportDTO p : produits) {
            table.addCell(new Phrase(p.produit() != null ? p.produit() : "", dataFont));
            table.addCell(new Phrase(p.quantite() != null ? p.quantite().toString() : "0", dataFont));
            table.addCell(new Phrase(p.chiffreAffaires() != null ? p.chiffreAffaires().toString() : "0", dataFont));
            table.addCell(new Phrase(p.partDuCA() != null ? p.partDuCA() + "%" : "0%", dataFont));
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    public byte[] exporterVentesListeExcel(List<VenteListeExportDTO> ventes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Liste des ventes");

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Client", "Date", "Produit(s)", "Mode paiement", "Total (Ar)", "Statut"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            int rowNum = 1;
            for (VenteListeExportDTO v : ventes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getId() != null ? v.getId() : 0L);
                row.createCell(1).setCellValue(v.getClient() != null ? v.getClient() : "");
                row.createCell(2).setCellValue(v.getDate() != null ? v.getDate().format(dateFormatter) : "");
                row.createCell(3).setCellValue(v.getProduits() != null ? v.getProduits() : "");
                row.createCell(4).setCellValue(v.getModePaiement() != null ? v.getModePaiement() : "");
                row.createCell(5).setCellValue(v.getTotal() != null ? v.getTotal().doubleValue() : 0);
                row.createCell(6).setCellValue(v.getStatut() != null ? v.getStatut() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exporterVentesListePdf(List<VenteListeExportDTO> ventes) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("Liste des ventes", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(18);
        document.add(title);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 3.5f, 3.2f, 4.5f, 2.8f, 2.5f, 2.5f});

        String[] columns = {"ID", "Client", "Date", "Produit(s)", "Mode paiement", "Total (Ar)", "Statut"};
        for (String col : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
            cell.setBackgroundColor(new GrayColor(0.8f));
            table.addCell(cell);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (VenteListeExportDTO v : ventes) {
            table.addCell(new Phrase(v.getId() != null ? v.getId().toString() : "", dataFont));
            table.addCell(new Phrase(v.getClient() != null ? v.getClient() : "", dataFont));
            table.addCell(new Phrase(v.getDate() != null ? v.getDate().format(dateFormatter) : "", dataFont));
            table.addCell(new Phrase(v.getProduits() != null ? v.getProduits() : "", dataFont));
            table.addCell(new Phrase(v.getModePaiement() != null ? v.getModePaiement() : "", dataFont));
            table.addCell(new Phrase(v.getTotal() != null ? v.getTotal().toString() : "0", dataFont));
            table.addCell(new Phrase(v.getStatut() != null ? v.getStatut() : "", dataFont));
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    public byte[] exporterFactureVentePdf(VenteDTO vente) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("VINA AKOHO - Facture", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(18);
        document.add(title);

        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        document.add(new Paragraph("Informations vente", sectionFont));
        document.add(new Paragraph("ID Vente: " + (vente.getId() != null ? vente.getId() : ""), textFont));
        document.add(new Paragraph("Client: " + ((vente.getClientNom() != null ? vente.getClientNom() : "") + (vente.getClientPrenom() != null ? " " + vente.getClientPrenom() : "")), textFont));
        document.add(new Paragraph("Date: " + (vente.getDateVente() != null ? vente.getDateVente().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""), textFont));
        document.add(new Paragraph("Mode paiement: " + (vente.getModePaiement() != null ? vente.getModePaiement() : ""), textFont));
        document.add(new Paragraph("Statut: " + (vente.getStatutVente() != null ? vente.getStatutVente() : ""), textFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Coordonnées & vente", sectionFont));
        String factureNumero = vente.getFacture() != null && vente.getFacture().getNumero() != null ? vente.getFacture().getNumero() : "—";
        String factureDate = vente.getFacture() != null && vente.getFacture().getDateEmission() != null ? vente.getFacture().getDateEmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "—";
        String totalTtc = vente.getFacture() != null && vente.getFacture().getMontantTtc() != null ? vente.getFacture().getMontantTtc() + " Ar" : (vente.getMontantTotal() != null ? vente.getMontantTotal() + " Ar" : "0 Ar");
        
        document.add(new Paragraph("Facture: " + factureNumero, textFont));
        document.add(new Paragraph("Date émission: " + factureDate, textFont));
        document.add(new Paragraph("Total TTC: " + totalTtc, textFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Articles vendus", sectionFont));
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 2.5f, 3f, 3f});

        table.addCell(new Phrase("Produit", sectionFont));
        table.addCell(new Phrase("Quantité", sectionFont));
        table.addCell(new Phrase("Prix unitaire", sectionFont));
        table.addCell(new Phrase("Total", sectionFont));

        if (vente.getLignes() != null) {
            for (var ligne : vente.getLignes()) {
                table.addCell(new Phrase(ligne.getNomProduit() != null ? ligne.getNomProduit() : "", textFont));
                table.addCell(new Phrase(ligne.getQuantite() != null ? ligne.getQuantite().toString() : "0", textFont));
                table.addCell(new Phrase(ligne.getPrixUnitaire() != null ? ligne.getPrixUnitaire() + " Ar" : "0 Ar", textFont));
                table.addCell(new Phrase(ligne.getMontant() != null ? ligne.getMontant() + " Ar" : "0 Ar", textFont));
            }
        }

        document.add(table);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Total facture", sectionFont));
        document.add(new Paragraph(totalTtc, textFont));

        document.close();

        return outputStream.toByteArray();
    }

    public byte[] exporterBonLivraisonPdf(VenteDTO vente) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("VINA AKOHO - Bon de livraison", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(18);
        document.add(title);

        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        document.add(new Paragraph("Informations vente", sectionFont));
        document.add(new Paragraph("ID Vente: " + (vente.getId() != null ? vente.getId() : ""), textFont));
        document.add(new Paragraph("Date: " + (vente.getDateVente() != null ? vente.getDateVente().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""), textFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Livrer à", sectionFont));
        document.add(new Paragraph((vente.getClientNom() != null ? vente.getClientNom() : "") + (vente.getClientPrenom() != null ? " " + vente.getClientPrenom() : ""), textFont));
        document.add(new Paragraph(vente.getClientAdresse() != null ? vente.getClientAdresse() : "", textFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Articles", sectionFont));
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 2f, 2f});

        table.addCell(new Phrase("Produit", sectionFont));
        table.addCell(new Phrase("Quantité", sectionFont));
        table.addCell(new Phrase("Unité", sectionFont));

        if (vente.getLignes() != null) {
            for (var ligne : vente.getLignes()) {
                table.addCell(new Phrase(ligne.getNomProduit() != null ? ligne.getNomProduit() : "", textFont));
                table.addCell(new Phrase(ligne.getQuantite() != null ? ligne.getQuantite().toString() : "0", textFont));
                table.addCell(new Phrase(ligne.getUnite() != null ? ligne.getUnite() : "kg", textFont));
            }
        }

        document.add(table);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Signature réception", sectionFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("____________________________________", textFont));

        document.close();

        return outputStream.toByteArray();
    }
}
