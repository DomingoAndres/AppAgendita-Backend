package com.microservice.microservice_user;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.microservice.microservice_user.config.CorsConfig;

class CorsConfigTest {

    @Test
    void corsConfigurerBeanShouldConfigureCors() {
        CorsConfig config = new CorsConfig();
        WebMvcConfigurer webMvcConfigurer = config.corsConfigurer();
        assertThat(webMvcConfigurer).isNotNull();

        // Verifica que el método addCorsMappings no lanza excepción
        CorsRegistry registry = new CorsRegistry();
        webMvcConfigurer.addCorsMappings(registry);
    }
}