package com.example.demo.repository;

import com.example.demo.model.Transaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String> {
}
