package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Mono<Account> createAccount(Account account){
        return accountRepository.save(account);
    }

    public Mono<Account> getAccountById(String id){
        return accountRepository.findById(id);
    }

    public Flux<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    public Mono<Account> updateAccount(Account account){
        return accountRepository.save(account);
    }

    public Mono<Void> deleteAccountById(String id){
       return accountRepository.deleteById(id);
    }
}
