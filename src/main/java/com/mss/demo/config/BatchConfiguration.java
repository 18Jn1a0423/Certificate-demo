package com.mss.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {


	@Bean
	public Job importCertificateJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
	                               Tasklet importCertificateTasklet, JobCompletionNotificationListener listener) {
	    return jobBuilderFactory.get("importCertificateJob")
	            .incrementer(new RunIdIncrementer())
	            .listener(listener)  // Register the listener here
	            .flow(importCertificateStep(stepBuilderFactory, importCertificateTasklet))
	            .end()
	            .build();
	}


	@Bean
	public Step importCertificateStep(StepBuilderFactory stepBuilderFactory, Tasklet importCertificateTasklet) {
		return stepBuilderFactory.get("importCertificateStep").tasklet(importCertificateTasklet).build();
	}

	@Bean
	public JobExecutionListener jobListener() {
		return new JobCompletionNotificationListener();
	}
}
