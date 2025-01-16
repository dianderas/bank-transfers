package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "El username de usuario es obligatorio")
    private String username;
    @NotBlank(message = "El password de usuario es obligatorio")
    private String password;
    @NotBlank(message = "El role de usuario es obligatorio")
    private String role;
}
