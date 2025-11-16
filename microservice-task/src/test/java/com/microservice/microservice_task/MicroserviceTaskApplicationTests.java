package com.microservice.microservice_task;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"eureka.client.enabled=false",
	"spring.cloud.config.enabled=false"
})
class MicroserviceTaskApplicationTests {

	@Test
	void contextLoads() {
		// Test básico de carga de contexto
	}

}
