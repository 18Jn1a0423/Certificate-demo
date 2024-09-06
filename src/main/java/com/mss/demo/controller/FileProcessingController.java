package com.mss.demo.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mss.demo.entity.Certificate;
import com.mss.demo.service.FileProcessingService;

@CrossOrigin(origins = "http://172.17.12.38:4200")
@RestController
@RequestMapping("/api/certificates")
public class FileProcessingController {

	@Autowired
	private FileProcessingService fileProcessingService;

	@PostMapping("/add")
	public ResponseEntity<String> addCertificate(@RequestBody Certificate certificateRequest) {
		try {
			// Determine isMonthly dynamically based on request data or other logic
			boolean isMonthly = determineIsMonthly(certificateRequest);

			fileProcessingService.saveCertificate(certificateRequest, isMonthly);
			return ResponseEntity.ok("Certificate created successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error creating certificate: " + e.getMessage());
		}
	}

	private boolean determineIsMonthly(Certificate certificateRequest) {
		LocalDate expiryDate = certificateRequest.getExpiryDate();
		if (expiryDate != null) {
			return expiryDate.isAfter(LocalDate.now().plusWeeks(1))
					&& expiryDate.isBefore(LocalDate.now().plusMonths(1));
		}
		return false;
	}

	@GetMapping("/all")
	public List<Certificate> getAllCertificates() {
		return fileProcessingService.getAllCertificates();
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<String> updateCertificate(@PathVariable Long id,
			@RequestBody Certificate certificateRequest) {
		try {
			fileProcessingService.updateCertificate(id, certificateRequest);
			return ResponseEntity.ok("Certificate updated successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating certificate: " + e.getMessage());
		}
	}

	@GetMapping("/fileType")
	public ResponseEntity<List<Certificate>> getCertificatesByFileType(@RequestParam String fileType) {
		try {
			List<Certificate> certificates = fileProcessingService.getCertificatesByFileType(fileType);
			return ResponseEntity.ok(certificates);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(Collections.emptyList());
		}
	}


//	@GetMapping("/expiring-soon")
//	public Map<String, List<Certificate>> getExpiringSoon() {
//	    List<Certificate> weeklyExpiringCertificates = fileProcessingService.getCertificatesExpiringSoon();
//	    List<Certificate> monthlyExpiringCertificates = fileProcessingService.getCertificatesExpiringInMonth();
//
//	    Map<String, List<Certificate>> response = new HashMap<>();
//	    response.put("weeklyExpiringCertificates", weeklyExpiringCertificates);
//	    response.put("monthlyExpiringCertificates", monthlyExpiringCertificates);
//
//	    return response;
//	}

	 @GetMapping("/check-expirations")
	    public ResponseEntity<String> checkExpirations() {
	        try {
	            // Trigger the expiration check
	            fileProcessingService.scheduleExpiringCertificatesCheck();

	            // Return a success message
	            return ResponseEntity.ok("Certificate expiration check completed successfully.");
	        } catch (Exception e) {
	            // Handle any exceptions that might occur
	            return ResponseEntity.status(500).body("An error occurred while checking certificate expirations: " + e.getMessage());
	        }
	    }
    
	/*****************************
	 * Download the certificate of a particular type
	 **********************************************************/
	@GetMapping("/download")
	public ResponseEntity<Resource> downloadCertificate(@RequestParam String fileType, @RequestParam Long Id) {
		Resource file = fileProcessingService.getMostRecentCertificateFile(fileType, Id);
		if (file == null) {
			return ResponseEntity.notFound().build();
		}

		String contentType = getContentType(fileType);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate." + fileType + "\"")
				.body(file);
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<Resource> downloadCertificate(@PathVariable Long id) {

		Optional<Certificate> certificateOptional = fileProcessingService.getCertificateById(id);
		if (!certificateOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Certificate certificate = certificateOptional.get();
		byte[] fileData = certificate.getFileData();
		String fileType = certificate.getFileType();

		String contentType = getContentType(fileType);
		if (contentType == null) {
			contentType = "application/octet-stream"; // Default content type
		}

		String filename = "certificate-" + id + "." + fileType;

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.body(new ByteArrayResource(fileData));
	}

	private String getContentType(String fileType) {
		// Use a mapping to determine the content type based on the file extension
		switch (fileType.toLowerCase()) {
		case "pdf":
			return "application/pdf";
		case "csv":
			return "text/csv";
		// Add more cases as needed
		default:
			return "application/octet-stream"; // Default content type for unknown types
		}
	}

	class FileTypeMapper {
		private static final Map<String, String> contentTypeMap = new HashMap<>();

		static {
			contentTypeMap.put("pdf", "application/pdf");
			contentTypeMap.put("csv", "text/csv");
		}

		public static String getContentType(String fileType) {
			return contentTypeMap.get(fileType.toLowerCase());
		}

	}

//	@RolesAllowed("realm_admin")
	@GetMapping("/renew/{certificateId}")
	public ResponseEntity<String> renewCertificate(@PathVariable Long certificateId) {
		try {
			fileProcessingService.renewCertificate(certificateId);
			return ResponseEntity.ok("Certificate renewed successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error renewing certificate: " + e.getMessage());
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteCertificatesByUserEmail(@RequestParam String userEmail) {
		try {
			fileProcessingService.deleteCertificatesByUserEmail(userEmail);
			return ResponseEntity.ok("Certificates deleted successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error deleting certificates: " + e.getMessage());
		}
	}

}
