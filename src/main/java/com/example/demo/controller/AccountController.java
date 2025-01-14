package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.service.AccountService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Mono<Account> createAccount(@RequestBody Account account){
        return accountService.createAccount(account);
    }

    @GetMapping
    public Flux<Account> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Mono<Account> getAccountById(@PathVariable String id){
        return accountService.getAccountById(id);
    }

    @PutMapping
    public Mono<Account> updateAccount(@RequestBody Account account){
        return accountService.updateAccount(account);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteAccountById(@PathVariable String id) {
        return accountService.deleteAccountById(id);
    }
}
