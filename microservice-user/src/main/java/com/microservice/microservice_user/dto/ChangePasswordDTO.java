package com.microservice.microservice_user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

//Esto meditar si sacarlo o no, no creo que valga la pena tener un DTO solo para esto si no hay un uso real todavia en la aplicacion

public class ChangePasswordDTO {

    @NotBlank(message = "La contraseña actual es requerida")
    private String currentPassword;

    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al mínimo 6 caracteres")
    private String newPassword;

    // Constructors
    public ChangePasswordDTO() {}

    public ChangePasswordDTO(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
