package com.trimblecars.leasemanagement.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.trimblecars.leasemanagement.model.customer.LeaseHistory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] generateLeaseHistoryPdf(List<LeaseHistory> leaseHistories) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            
            Paragraph title = new Paragraph("Lease History Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18);
            document.add(title);

            
            document.add(new Paragraph("\n"));

            
            Table table = new Table(new float[]{1, 2, 2, 2}); // Adjust column widths
            table.addCell("Lease ID");
            table.addCell("Customer ID");
            table.addCell("Vehicle ID");
            table.addCell("Vehicle Name");
            table.addCell("Lease Date");
            table.addCell("End Date");

        
            for (LeaseHistory lease : leaseHistories) {
                table.addCell(lease.getId());
                table.addCell(lease.getCustomer().getId());
                table.addCell(lease.getVehicle().getId());
                table.addCell(lease.getVehicle().getVehicleName());
                table.addCell(lease.getLeaseStartDate().toString());
                table.addCell(lease.getLeaseEndDate().toString());
            }

            // Add table to the document
            document.add(table);

            // Close the document
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }
}