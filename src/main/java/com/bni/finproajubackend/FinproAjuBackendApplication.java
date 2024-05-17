package com.bni.finproajubackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinproAjuBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinproAjuBackendApplication.class, args);
	}

}
