package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    private String id;
    @Column("from_account_id")
    private String fromAccountId;
    @Column("to_account_id")
    private String toAccountId;
    private double amount;
    private String currency;
    private String status;
    private String message;
}
