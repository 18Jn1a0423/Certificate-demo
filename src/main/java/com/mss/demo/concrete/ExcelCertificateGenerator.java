package com.mss.demo.concrete;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.mss.demo.entity.Certificate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ExcelCertificateGenerator implements CertificateGenerator {

	@Override
	public String getFileExtension() {
		return "xls";
	}

	@Override
	public byte[] generateCertificate(Certificate certificate) {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Certificate");
		Row row = sheet.createRow(0);

		Cell cell = row.createCell(0);
		cell.setCellValue("Certificate Name");
		cell = row.createCell(1);
		cell.setCellValue("Create Date");
		cell = row.createCell(2);
		cell.setCellValue("Expiry Date");

		row = sheet.createRow(1);
		row.createCell(0).setCellValue(certificate.getCertificateName());
		row.createCell(1).setCellValue(certificate.getCreateDate().toString());
		row.createCell(2).setCellValue(certificate.getExpiryDate().toString());

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return outputStream.toByteArray();
	}

}
