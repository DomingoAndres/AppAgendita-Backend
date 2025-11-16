package com.microservice.microservice_user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = MicroserviceUserApplicationTests.class)
class MicroserviceUserApplicationTests {

	//creo que es necesario dejarlo
	@Test
	void contextLoads() {

	}

}
