package com.mss.demo.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mss.demo.concrete.CertificateGenerator;
import com.mss.demo.entity.Certificate;
import com.mss.demo.repository.CertificateRepository;

@Service
public class FileProcessingService {

	@Autowired
	private CertificateRepository certificateRepository;

	@Autowired
	private List<CertificateGenerator> certificateGenerators;

	@Autowired
	private EmailService emailService;

	public void saveCertificate(Certificate request, boolean isMonthly) {
		if (!isValidEmail(request.getUserEmail())) {
			throw new IllegalArgumentException("Invalid email address: " + request.getUserEmail());
		}

		// Set the create date to the current date
		LocalDate createDate = LocalDate.now();

		// Calculate the expiry date based on the isMonthly parameter
		LocalDate expiryDate = isMonthly ? createDate.plusMonths(1) : createDate.plusWeeks(1);

		CertificateGenerator generator = certificateGenerators.stream()
				.filter(g -> g.getFileExtension().equalsIgnoreCase(request.getFileType())).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unsupported format: " + request.getFileType()));

		byte[] fileData = generator.generateCertificate(new Certificate(request.getCertificateName(), createDate,
				expiryDate, null, request.getFileType(), request.getUserEmail(), request.getCreatedBy(),
				request.getReportManager(), request.getReportDirector()));

		Certificate entity = new Certificate(request.getCertificateName(), createDate, expiryDate, fileData,
				request.getFileType(), request.getUserEmail(), request.getCreatedBy(), request.getReportManager(),
				request.getReportDirector());

		certificateRepository.save(entity);
	}

	private boolean isValidEmail(String email) {
		// Simple regex for basic email validation
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email != null && email.matches(emailRegex);
	}

///*****************GET ALL CERTIFICATE LIST*************************************************************///

	public List<Certificate> getAllCertificates() {
		return certificateRepository.findAll();
	}

	/**************
	 * * Get specific type of file
	 ************************************************************/

	public List<Certificate> getCertificatesByFileType(String fileType) {
		return certificateRepository.findByFileType(fileType.toLowerCase());
	}

	/*******************************
	 * handleExpiringCertificates
	 *********************************************************************************/

//	@Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
//	@Scheduled(cron = "0 0 10 * * MON") // Runs weekly on Monday at 10:00 AM
//	@Scheduled(cron = "0 * * * * ?") // Runs every minute
//	@Scheduled(cron = "0 0 * * * *") // Runs every hour, every day
	public void scheduleExpiringCertificatesCheck() {
		List<Certificate> weeklyExpiringCertificates = getCertificatesExpiringSoon();
		List<Certificate> monthlyExpiringCertificates = getCertificatesExpiringInMonth();

		// Handle weekly expiring certificates
		if (!weeklyExpiringCertificates.isEmpty()) {
			emailService.sendExpiringCertificateNotification(weeklyExpiringCertificates);
			System.out.println("Scheduled check: Found " + weeklyExpiringCertificates.size()
					+ " certificates expiring in the next week.");
		} else {
			System.out.println("Scheduled check: No certificates expiring in the next week.");
		}

		// Handle monthly expiring certificates
		if (!monthlyExpiringCertificates.isEmpty()) {
			emailService.sendExpiringCertificateNotification(monthlyExpiringCertificates);
			System.out.println("Scheduled check: Found " + monthlyExpiringCertificates.size()
					+ " certificates expiring in the next month.");
		} else {
			System.out.println("Scheduled check: No certificates expiring in the next month.");
		}
	}

	public List<Certificate> getCertificatesExpiringSoon() {
		LocalDate today = LocalDate.now();
		LocalDate oneWeekLater = today.plusWeeks(1);
		return certificateRepository.findByExpiryDateBetween(today, oneWeekLater);
	}

	public List<Certificate> getCertificatesExpiringInMonth() {
		LocalDate today = LocalDate.now();
		LocalDate oneMonthLater = today.plusMonths(1);
		return certificateRepository.findByExpiryDateBetween(today, oneMonthLater);
	}

	public void updateCertificate(Long id, Certificate request) {
		Certificate existingCertificate = certificateRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Certificate with ID " + id + " does not exist."));

		// Update certificate details
		existingCertificate.setCertificateName(request.getCertificateName());
		existingCertificate.setCreateDate(LocalDate.now());
		existingCertificate.setUserEmail(request.getUserEmail());
		existingCertificate.setCreatedBy(request.getCreatedBy());
		existingCertificate.setReportManager(request.getReportManager());
		existingCertificate.setReportDirector(request.getReportDirector());

		if (request.getExpiryDate() != null) {
			existingCertificate.setExpiryDate(request.getExpiryDate());
		} else {
			existingCertificate.setExpiryDate(existingCertificate.getCreateDate().plusMonths(1));
		}

		certificateRepository.save(existingCertificate);

		// Send notification if the certificate is expiring soon
//	    if (isExpiringSoon(existingCertificate)) {
//	        emailService.sendExpiringCertificateNotification(List.of(existingCertificate)); // Send email for the single certificate
//	    }
	}

//	private boolean isExpiringSoon(Certificate certificate) {
//	    LocalDate today = LocalDate.now();
//	    LocalDate expiryDate = certificate.getExpiryDate();
//	    long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);
//
//	    // Check if the certificate is expiring within the next 7 days
//	    return daysUntilExpiry <= 7 && daysUntilExpiry >= 0;
//	}

	/***********************************
	 * Download certificate
	 ***************************************************************************/

	public Resource getMostRecentCertificateFile(String fileType, Long id) {
		List<Certificate> certificates = certificateRepository.findTopByFileTypeAndIdOrderByCreateDateDesc(fileType,
				id);
		if (certificates != null && !certificates.isEmpty()) {
			return new ByteArrayResource(certificates.get(0).getFileData());
		}
		return null;
	}

	public Optional<Certificate> getCertificateById(Long id) {
		return certificateRepository.findById(id);
	}

	/***** Renews the given certificate by extending its expiry date. ***/

	public void renewCertificate(Long certificateId) {
		if (certificateId == null) {
			throw new IllegalArgumentException("Certificate cannot be null");
		}
		Certificate certificate = certificateRepository.findById(certificateId)
				.orElseThrow(() -> new IllegalArgumentException("Certificate not found with id: " + certificateId));

		// Determine the new expiry date (e.g., extend by one month)
		LocalDate newExpiryDate = certificate.getExpiryDate().plusMonths(1);

		// Update the certificate's expiry date
		certificate.setExpiryDate(newExpiryDate);

		// Save the updated certificate back to the database
		certificateRepository.save(certificate);
		// Optionally, send a confirmation email about successful renewal
		sendRenewalNotification(certificate);

		System.out.println("Certificate renewed. New expiry date: " + newExpiryDate);
	}

	public void deleteCertificatesByUserEmail(String userEmail) {
		List<Certificate> certificates = certificateRepository.findByUserEmail(userEmail);

		if (!certificates.isEmpty()) {
			String reportManager = certificates.get(0).getReportManager();
			String reportDirector = certificates.get(0).getReportDirector();

			certificateRepository.deleteByUserEmail(userEmail);

			emailService.sendDeletionNotification(userEmail, reportManager, reportDirector);
		} else {
			throw new IllegalArgumentException("No certificates found for user with email: " + userEmail);
		}
	}

	public void sendRenewalNotification(Certificate certificate) {
		emailService.sendRenewalNotification(certificate);
	}

}
