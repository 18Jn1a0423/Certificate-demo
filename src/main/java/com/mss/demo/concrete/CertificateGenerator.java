package com.mss.demo.concrete;

import com.mss.demo.entity.Certificate;

public interface CertificateGenerator {
	String getFileExtension(); // e.g., "pdf", "csv", "xlsx"

	byte[] generateCertificate(Certificate certificate);

}
