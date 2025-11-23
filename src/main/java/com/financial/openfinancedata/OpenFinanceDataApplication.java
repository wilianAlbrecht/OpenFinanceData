package com.financial.openfinancedata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OpenFinanceDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenFinanceDataApplication.class, args);
	}

}
