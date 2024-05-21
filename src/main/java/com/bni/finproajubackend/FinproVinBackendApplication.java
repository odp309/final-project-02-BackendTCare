package com.bni.finproajubackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinproVinBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinproVinBackendApplication.class, args);
	}

}
