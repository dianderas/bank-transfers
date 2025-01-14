package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferResultDTO {
    private String fromAccountId;
    private String toAccountId;
    private double amount;
    private boolean success;
    private String currency;
    private String message;
}
