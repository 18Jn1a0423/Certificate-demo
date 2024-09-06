package com.mss.demo.concrete;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.mss.demo.entity.Certificate;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Component;

@Component
public class PdfCertificateGenerator implements CertificateGenerator {

	@Override
	public String getFileExtension() {
		return "pdf";
	}

	@Override
	public byte[] generateCertificate(Certificate certificate) {
		Document document = new Document();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			PdfWriter.getInstance(document, outputStream);
			document.open();
			document.add(new Paragraph("Certificate Name: " + certificate.getCertificateName()));
			document.add(new Paragraph("Create Date: " + certificate.getCreateDate()));
			document.add(new Paragraph("Expiry Date: " + certificate.getExpiryDate()));
			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return outputStream.toByteArray();
	}
}
//public class PdfCertificateGenerator implements CertificateGenerator {
//
//    @Override
//    public byte[] generateCertificate(Certificate certificate) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        try (PDDocument document = new PDDocument()) {
//            PDPage page = new PDPage();
//            document.addPage(page);
//            PDPageContentStream contentStream = new PDPageContentStream(document, page);
//            contentStream.beginText();
//            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//            contentStream.newLineAtOffset(25, 700);
//            contentStream.showText("Certificate Name: " + certificate.getName());
//            contentStream.newLineAtOffset(0, -15);
//            contentStream.showText("Create Date: " + certificate.getCreateDate());
//            contentStream.newLineAtOffset(0, -15);
//            contentStream.showText("Expiry Date: " + certificate.getExpiryDate());
//            contentStream.newLineAtOffset(0, -15);
//            contentStream.showText("Created By: " + certificate.getCreatedBy());
//            contentStream.newLineAtOffset(0, -15);
//            contentStream.showText("Report Manager: " + certificate.getReportManager());
//            contentStream.newLineAtOffset(0, -15);
//            contentStream.showText("Report Director: " + certificate.getReportDirector());
//            contentStream.endText();
//            contentStream.close();
//            document.save(outputStream);
//        } catch (IOException e) {
//            throw new RuntimeException("Error generating PDF", e);
//        }
//        return outputStream.toByteArray();
//    }
//}
