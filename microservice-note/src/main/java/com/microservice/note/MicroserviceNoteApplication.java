package com.microservice.note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaRepositories
@SpringBootApplication
public class MicroserviceNoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceNoteApplication.class, args);
	}

}
