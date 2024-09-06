package com.mss.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mss.demo.entity.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

	List<Certificate> findByFileType(String lowerCase);

	List<Certificate> findByExpiryDateBetween(LocalDate today, LocalDate oneWeekFromNow);

	List<Certificate> findByUserEmail(String userEmail);

	void deleteByUserEmail(String userEmail);


	List<Certificate> findTopByFileTypeAndIdOrderByCreateDateDesc(String fileType, Long id);

    long countByExpiryDateBetween(LocalDate startDate, LocalDate endDate);

}
