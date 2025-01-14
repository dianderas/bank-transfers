package com.example.demo.repository;

import com.example.demo.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountRepository extends ReactiveCrudRepository<Account, String> {
}
