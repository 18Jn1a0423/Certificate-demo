package com.mss.demo.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.logging.Logger;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger logger = Logger.getLogger(JobCompletionNotificationListener.class.getName());

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // Actions before job execution
        logger.info("Starting job: " + jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Actions after job execution
        JobInstance jobInstance = jobExecution.getJobInstance();
        JobParameters jobParameters = jobExecution.getJobParameters();
        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();

        // Log the job execution status
        logger.info("Job completed with status: " + jobExecution.getStatus());
        logger.info("Job Name: " + jobInstance.getJobName());
        logger.info("Job Parameters: " + jobParameters.toString());
        logger.info("Number of Steps: " + stepExecutions.size());

        // Log details of each step
        for (StepExecution stepExecution : stepExecutions) {
            logger.info("Step Name: " + stepExecution.getStepName());
            logger.info("Step Status: " + stepExecution.getStatus());
            logger.info("Read Count: " + stepExecution.getReadCount());
            logger.info("Write Count: " + stepExecution.getWriteCount());
            logger.info("Commit Count: " + stepExecution.getCommitCount());
            logger.info("Rollback Count: " + stepExecution.getRollbackCount());
            logger.info("Filter Count: " + stepExecution.getFilterCount());
            logger.info("Read Skip Count: " + stepExecution.getReadSkipCount());
            logger.info("Write Skip Count: " + stepExecution.getWriteSkipCount());
            logger.info("Process Skip Count: " + stepExecution.getProcessSkipCount());
            logger.info("Process Skip Count: " + stepExecution.getProcessSkipCount());
            logger.info("Exception: " + (stepExecution.getFailureExceptions().isEmpty() ? "None" : stepExecution.getFailureExceptions()));
        }
    }
}


