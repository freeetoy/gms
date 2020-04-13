package com.gms.web.admin;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableCaching
public class GmsApplication {

	//Spring Boot 에서 TimeZone 설정하려면 아래와 같이 @PostConStruct를 설정해주면 된다.

	@PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

	
	public static void main(String[] args) {
		SpringApplication.run(GmsApplication.class, args);
	}

}
