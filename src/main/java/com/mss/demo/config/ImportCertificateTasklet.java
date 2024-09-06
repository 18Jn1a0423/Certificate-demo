package com.mss.demo.config;

import java.time.LocalDate;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mss.demo.entity.Certificate;
import com.mss.demo.service.FileProcessingService;

@Component
public class ImportCertificateTasklet implements Tasklet {

	@Autowired
	private FileProcessingService fileProcessingService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String[] names = { "John Doe", "Jane Doe", "Alice Smith" };
		String createdBy = "Admin";
		String reportManager = "Manager";
		String reportDirector = "Director";

		boolean isMonthly = true; // or determine dynamically based on your logic

		for (String name : names) {
			Certificate request = new Certificate();
			request.setCertificateName(name);
			request.setUserEmail(name + "@example.com");
			request.setCreatedBy(createdBy);
			request.setReportManager(reportManager);
			request.setReportDirector(reportDirector);

			// Call saveCertificate with the isMonthly parameter
			fileProcessingService.saveCertificate(request, isMonthly);
		}

		return RepeatStatus.FINISHED;
	}

}
