package com.mss.demo.concrete;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;

import com.mss.demo.entity.Certificate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class DocxCertificateGenerator implements CertificateGenerator {

    @Override
    public String getFileExtension() {
        return "docx";
    }

    @Override
    public byte[] generateCertificate(Certificate certificate) {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Certificate Name: " + certificate.getCertificateName());
            run.addBreak();
            run.setText("Created By: " + certificate.getCreatedBy());
            run.addBreak();
            run.setText("Report Manager: " + certificate.getReportManager());
            run.addBreak();
            run.setText("Report Director: " + certificate.getReportDirector());
            run.addBreak();
            run.setText("Expiry Date: " + certificate.getExpiryDate().toString());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating .docx file", e);
        }
    }
}

