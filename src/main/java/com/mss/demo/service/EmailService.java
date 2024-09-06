package com.mss.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.mss.demo.entity.Certificate;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	private static final String FROM_EMAIL = "bineetha5246@gmail.com";

	public void sendExpiringCertificateNotification(List<Certificate> certificates) {
		if (certificates.isEmpty()) {
			return;
		}

		for (Certificate certificate : certificates) {
			sendEmailToRecipient(certificate.getUserEmail(), certificate, "Certificate Holder");
			sendEmailToRecipient(certificate.getReportManager(), certificate, "Report Manager");
			sendEmailToRecipient(certificate.getReportDirector(), certificate, "Report Director");
		}
	}

	private void sendEmailToRecipient(String recipientEmail, Certificate certificate, String recipientRole) {
	    if (isValidEmail(recipientEmail)) {
	        try {
	            // Create email message
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	            // Attempt to read the logo image file
	            byte[] imageBytes;
	            try {
	                imageBytes = Files.readAllBytes(Paths.get("C:/Users/bchepuri/Downloads/miracle.jpg"));
	            } catch (IOException e) {
	                logger.error("Failed to read image file at path: C:/Users/bchepuri/Downloads/miracle.jpg", e);
	                return; // Skip sending this email if image loading fails
	            }

	            // Create a data source for the image
	            DataSource imageSource = new ByteArrayDataSource(imageBytes, "image/jpeg");

	            // HTML content with embedded image
	            String emailContent = "<html><head><style>"
	                    + "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f3f3f3; }"
	                    + ".container { padding: 20px; }"
	                    + ".content { max-width: 600px; margin: auto; background-color: #ffffff; border: 1px solid #dddddd; padding: 20px; border-radius: 4px; }"
	                    + ".header { background-color: #ffffff; color: white; padding: 10px; text-align: center; font-size: 24px; font-weight: bold; border-radius: 4px 4px 0 0; }"
	                    + ".footer { background-color: #f3f3f3; color: #888888; padding: 10px; text-align: center; font-size: 12px; border-top: 1px solid #dddddd; }"
	                    + "h1 { font-size: 20px; margin-bottom: 20px; color: #333333; }"
	                    + "p { font-size: 16px; margin-bottom: 20px; color: #555555; }"
	                    + "a { color: #ffffff; text-decoration: none; font-weight: bold; }"
	                    + ".button { background-color: #e50914; color: #ffffff; padding: 10px 20px; text-align: center; border-radius: 4px; text-decoration: none; display: inline-block; margin: 10px 0; font-size: 16px; }"
	                    + "</style></head><body><div class='container'><div class='content'>"
	                    + "<div class='header'><img src='cid:miracleLogo' alt='Miracle Software Systems'>"
	                    + "<p>Your Certificate is Expiring Soon!</p></div>"
	                    + "<p>Dear " + recipientRole + ",</p>"
	                    + "<p>The following certificate is expiring soon:</p><ul>"
	                    + "<li><strong>Certificate Name:</strong> " + certificate.getCertificateName() + "</li>"
	                    + "<li><strong>Expiry Date:</strong> " + certificate.getExpiryDate() + "</li>"
	                    + "</ul><p>If you wish to renew your certificate, simply click the button below:</p>"
	                    + "<a href='http://172.17.12.38:4200/updatecertificate/" + certificate.getId() + "' class='button'>RENEW NOW</a>"
	                    + "<p>If you have any questions or need further assistance, feel free to contact our team.</p>"
	                    + "<div class='footer'><p>&copy; 2024 Miracle Software Systems</p></div></div></div></body></html>";

	            // Set email details
	            helper.setTo(recipientEmail);
	            helper.setSubject("Certificate Expiration Notice");
	            helper.setText(emailContent, true); // Set to true for HTML content

	            // Add the image as an inline resource
	            helper.addInline("miracleLogo", imageSource);

	            // Send email
	            mailSender.send(message);
	            logger.info("Email sent to " + recipientEmail);
	        } catch (MessagingException e) {
	            logger.error("Failed to send email to " + recipientEmail, e);
	        }
	    } else {
	        logger.warn("Invalid email address: " + recipientEmail);
	    }
	}


	private boolean isValidEmail(String email) {
		// Simple regex for basic email validation
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email != null && email.matches(emailRegex);
	}

	public void sendDeletionNotification(String userEmail, String reportManager, String reportDirector) {
		String subject = "Certificate Deletion Notification";
		String body = "Dear User,\n\n" + "Your certificate has been deleted from our system.\n\n" + "Best Regards,\n"
				+ "Miracle Software Systems";

		sendSimpleMessage(userEmail, subject, body);
		sendSimpleMessage(reportManager, subject, body);
		sendSimpleMessage(reportDirector, subject, body);
	}

	public void sendSimpleMessage(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("bineetha5246@gmail.com"); // Replace with your email
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}


	public void sendRenewalNotification(Certificate certificate) {
	    String userEmail = certificate.getUserEmail();
	    String reportManager = certificate.getReportManager();
	    String reportDirector = certificate.getReportDirector();

	    try {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	        byte[] imageBytes;

	        // Attempt to read the logo image file
	        try {
	            imageBytes = Files.readAllBytes(Paths.get("C:/Users/bchepuri/Downloads/miracle.jpg"));
	        } catch (IOException e) {
	            logger.error("Failed to read image file at path: C:/Users/bchepuri/Downloads/miracle.jpg", e);
	            return; // Skip sending this email if image loading fails
	        }

	        // Create a data source for the image
	        DataSource imageSource = new ByteArrayDataSource(imageBytes, "image/jpeg");

	        // Prepare the email content for each recipient
	        sendEmail(userEmail, "Certificate Renewal Confirmation", prepareEmailContent("Certificate Holder", certificate, imageSource, helper));
	        
	        if (reportManager != null) {
	            sendEmail(reportManager, "Certificate Renewal Notification", prepareEmailContent("Manager", certificate, imageSource, helper));
	        }
	        
	        if (reportDirector != null) {
	            sendEmail(reportDirector, "Certificate Renewal Notification", prepareEmailContent("Director", certificate, imageSource, helper));
	        }

	    } catch (MessagingException e) {
	        logger.error("Failed to send email", e);
	    }
	}

	private String prepareEmailContent(String recipientRole, Certificate certificate, DataSource imageSource, MimeMessageHelper helper) throws MessagingException {
		 helper.addInline("miracleLogo", imageSource);
	    // Prepare the email content
	    return "<html>"
        + "<head>"
        + "<style>"
        + "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f3f3f3; }"
        + ".container { padding: 20px; }"
        + ".content { max-width: 600px; margin: auto; background-color: #ffffff; border: 1px solid #dddddd; padding: 20px; border-radius: 4px; }"
        + ".header { background-color: #ffffff; color: white; padding: 10px; text-align: center; font-size: 24px; font-weight: bold; border-radius: 4px 4px 0 0; }"
        + ".footer { background-color: #f3f3f3; color: #888888; padding: 10px; text-align: center; font-size: 12px; border-top: 1px solid #dddddd; }"
        + "h1 { font-size: 20px; margin-bottom: 20px; color: #333333; }"
        + "p { font-size: 16px; margin-bottom: 20px; color: #555555; }"
        + "a { color: #ffffff; text-decoration: none; font-weight: bold; }"
        + ".button { background-color: #e50914; color: #ffffff; padding: 10px 20px; text-align: center; border-radius: 4px; text-decoration: none; display: inline-block; margin: 10px 0; font-size: 16px; }"
        + "</style>"
        + "</head>"
        + "<body>"
        + "<div class='container'>"
        + "<div class='content'>"
        + "<div class='header'>"
        + "<img src='cid:miracleLogo' alt='Miracle Software Systems'>"
        + "<p>Your Certificate Renewed</p>"
        + "</div>"
        + "<p>Dear " + recipientRole + ",</p>"
        + "<p>We are pleased to inform you that your certificate has been successfully renewed.</p>"
        + "<ul>"
        + "<li><strong>Certificate Name:</strong> " + certificate.getCertificateName() + "</li>"
        + "<li><strong>New Expiry Date:</strong> " + certificate.getExpiryDate().toString() + "</li>"
        + "</ul>"
        + "<p>If you have any questions or need further assistance, feel free to contact our team.</p>"
        + "<div class='footer'>"
        + "<p>&copy; 2024 Miracle Software Systems</p>"
        + "</div>"
        + "</div>"
        + "</div>"
        + "</body>"
        + "</html>";
		

}
           private void sendEmail(String to, String subject, String content) {
	        try {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	            helper.setFrom(FROM_EMAIL);
	            helper.setTo(to);
	            helper.setSubject(subject);
	            helper.setText(content, true); // true indicates that the content is HTML

	            mailSender.send(message);
	            System.out.println("Email sent to " + to);
	        } catch (MessagingException e) {
	            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
	        }
	    }



}
