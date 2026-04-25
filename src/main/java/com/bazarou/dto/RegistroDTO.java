package com.bazarou.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 30, message = "Debe tener entre 3 y 30 caracteres")
    private String username;

    @NotBlank @Email(message = "Correo no válido")
    private String email;

    @NotBlank
    @Size(min = 6, message = "Mínimo 6 caracteres")
    private String password;

    @NotBlank
    private String confirmPassword;

    private String nombreCompleto;
    private String ciudad;
    private String estado;
}
