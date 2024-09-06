package com.mss.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobLauncherComponent {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importCertificateJob;

    public void runJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("JobParam", "SomeValue")
                .toJobParameters();
        jobLauncher.run(importCertificateJob, params);
    }
}

