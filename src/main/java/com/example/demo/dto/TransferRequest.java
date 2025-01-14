package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferRequest {

    @NotBlank(message = "El ID de la cuenta de origen es obligatorio")
    private String fromAccountId;

    @NotBlank(message = "El ID de la cuenta de destino es obligatorio")
    private String toAccountId;

    @Min(value = 1, message = "El monto debe ser mayor a cero")
    @Max(value = 10000, message = "El monto no puede ser mayor a $10000")
    private double amount;
}