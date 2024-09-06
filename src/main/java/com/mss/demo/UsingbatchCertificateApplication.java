package com.mss.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UsingbatchCertificateApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsingbatchCertificateApplication.class, args);
	}

}
