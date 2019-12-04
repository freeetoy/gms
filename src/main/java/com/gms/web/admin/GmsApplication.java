package com.gms.web.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmsApplication.class, args);
	}

}
