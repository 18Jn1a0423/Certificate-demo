package com.mss.demo.concrete;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CertificateConfig {

    @Bean
    public List<CertificateGenerator> certificateGenerators() {
        return List.of(
            new PdfCertificateGenerator(),
            new CsvCertificateGenerator(),
            new ExcelCertificateGenerator());

    }
}
