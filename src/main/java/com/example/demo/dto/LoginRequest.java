package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El username de usuario es obligatorio")
    public String username;
    @NotBlank(message = "El password de usuario es obligatorio")
    public String password;
}
