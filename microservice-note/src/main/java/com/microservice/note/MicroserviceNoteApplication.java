package com.microservice.note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaRepositories
@SpringBootApplication
public class MicroserviceNoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceNoteApplication.class, args);
	}

}
