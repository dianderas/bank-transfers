package com.example.demo.service;

import com.example.demo.model.Account;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {
    private final Map<String, Account> accountRepository = new ConcurrentHashMap<>();

    public Mono<Account> createAccount(Account account){
        accountRepository.put(account.getId(), account);
        return Mono.just(account);
    }

    public Mono<Account> getAccountById(String id){
        return Mono.justOrEmpty(accountRepository.get(id));
    }

    public Flux<Account> getAllAccounts(){
        return Flux.fromIterable(accountRepository.values());
    }

    public Mono<Account> updateAccount(Account account){
        accountRepository.put(account.getId(), account);
        return Mono.just(account);
    }

    public Mono<Void> deleteAccountById(String id){
        accountRepository.remove(id);
        return Mono.empty();
    }
}
