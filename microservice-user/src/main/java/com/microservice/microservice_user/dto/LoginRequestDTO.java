package com.microservice.microservice_user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LoginRequestDTO {

    @NotBlank(message = "El nombre de usuario o email es requerido")
    private String usernameOrEmail;

    @NotBlank(message = "La contraseña es requerida")
    private String password;

    // Constructors
    public LoginRequestDTO() {}

    public LoginRequestDTO(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

}
