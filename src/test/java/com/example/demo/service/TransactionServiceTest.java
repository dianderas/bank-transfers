package com.example.demo.service;
import static org.mockito.Mockito.*;

import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.TransferResultDTO;
import com.example.demo.model.Account;
import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TransactionServiceTest {
    private AccountService accountService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        accountService = mock(AccountService.class);
        transactionService = new TransactionService(accountService, mock(TransactionRepository.class));
    }

    @Test
    void testSuccessfulTransfer() {
        Account fromAccount = Account.builder().accountHolder("Juan").balance(100).currency("USD").build();
        Account toAccount = Account.builder().accountHolder("Juan").balance(500).currency("USD").build();
        TransferRequest request = new TransferRequest();
        request.setFromAccountId("1");
        request.setToAccountId("2");
        request.setAmount(100);

        when(accountService.getAccountById("1")).thenReturn(Mono.just(fromAccount));
        when(accountService.getAccountById("2")).thenReturn(Mono.just(toAccount));
        when(accountService.updateAccount(fromAccount)).thenReturn(Mono.just(fromAccount));
        when(accountService.updateAccount(toAccount)).thenReturn(Mono.just(toAccount));

        Mono<String> result = transactionService.makeTransfer(request)
                .map(transaction -> transaction.getMessage());

        StepVerifier.create(result)
                .expectNext("Transferencia exitosa")
                .verifyComplete();
    }

    @Test
    void testInsufficientFunds() {
        Account fromAccount = Account.builder().accountHolder("Juan").balance(100).currency("USD").build();
        Account toAccount = Account.builder().accountHolder("Juan").balance(500).currency("USD").build();
        TransferRequest request = new TransferRequest();
        request.setFromAccountId("1");
        request.setToAccountId("2");
        request.setAmount(1000);

        when(accountService.getAccountById("1")).thenReturn(Mono.just(fromAccount));
        when(accountService.getAccountById("2")).thenReturn(Mono.just(toAccount));

        Mono<String> result = transactionService.makeTransfer(request)
                .map(Transaction::getMessage);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage().equals("No hay suficiente saldo en la cuenta de origen"))
                .verify();
    }

    @Test
    void testTransferToSameAccount() {
        Account fromAccount = Account.builder().accountHolder("Juan").balance(100).currency("USD").build();
        TransferRequest request = new TransferRequest();
        request.setFromAccountId("1");
        request.setToAccountId("1");
        request.setAmount(100);

        Mono<String> result = transactionService.makeTransfer(request)
                .map(Transaction::getMessage);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().equals("No se puede transferir a la misma cuenta"))
                .verify();
    }

    @Test
    void testMakeMultipleTransfersSuccess() {
        Account fromAccount1 = Account.builder().accountHolder("Juan").balance(500).currency("USD").build();
        Account toAccount1 = Account.builder().accountHolder("Maria").balance(300).currency("USD").build();

        Account fromAccount2 = Account.builder().accountHolder("Carlos").balance(1000).currency("USD").build();
        Account toAccount2 = Account.builder().accountHolder("Ana").balance(700).currency("USD").build();

        TransferRequest request1 = new TransferRequest();
        request1.setFromAccountId("1");
        request1.setToAccountId("2");
        request1.setAmount(100);

        TransferRequest request2 = new TransferRequest();
        request2.setFromAccountId("3");
        request2.setToAccountId("4");
        request2.setAmount(200);

        when(accountService.getAccountById("1")).thenReturn(Mono.just(fromAccount1));
        when(accountService.getAccountById("2")).thenReturn(Mono.just(toAccount1));
        when(accountService.updateAccount(fromAccount1)).thenReturn(Mono.just(fromAccount1));
        when(accountService.updateAccount(toAccount1)).thenReturn(Mono.just(toAccount1));

        when(accountService.getAccountById("3")).thenReturn(Mono.just(fromAccount2));
        when(accountService.getAccountById("4")).thenReturn(Mono.just(toAccount2));
        when(accountService.updateAccount(fromAccount2)).thenReturn(Mono.just(fromAccount2));
        when(accountService.updateAccount(toAccount2)).thenReturn(Mono.just(toAccount2));

        Flux<TransferRequest> requests = Flux.just(request1, request2);
        Flux<TransferResultDTO> results = transactionService.makeMultipleTransfers(requests);

        StepVerifier.create(results)
                .expectNextMatches(result -> result.isSuccess() && result.getMessage().equals("Transferencia exitosa"))
                .expectNextMatches(result -> result.isSuccess() && result.getMessage().equals("Transferencia exitosa"))
                .verifyComplete();
    }

    @Test
    void testMakeMultipleTransfersWithFailures() {
        Account fromAccount = Account.builder().accountHolder("Juan").balance(100).currency("USD").build();
        Account toAccount = Account.builder().accountHolder("Maria").balance(300).currency("USD").build();

        TransferRequest validRequest = new TransferRequest();
        validRequest.setFromAccountId("1");
        validRequest.setToAccountId("2");
        validRequest.setAmount(50);

        TransferRequest invalidRequest = new TransferRequest();
        invalidRequest.setFromAccountId("1");
        invalidRequest.setToAccountId("1");
        invalidRequest.setAmount(500);

        when(accountService.getAccountById("1")).thenReturn(Mono.just(fromAccount));
        when(accountService.getAccountById("2")).thenReturn(Mono.just(toAccount));
        when(accountService.updateAccount(fromAccount)).thenReturn(Mono.just(fromAccount));
        when(accountService.updateAccount(toAccount)).thenReturn(Mono.just(toAccount));

        Flux<TransferRequest> requests = Flux.just(validRequest, invalidRequest);
        Flux<TransferResultDTO> results = transactionService.makeMultipleTransfers(requests);

        StepVerifier.create(results)
                .expectNextMatches(result -> result.isSuccess() && result.getMessage().equals("Transferencia exitosa"))
                .expectNextMatches(result -> !result.isSuccess() && result.getMessage().startsWith("Error en la transferencia"))
                .verifyComplete();
    }
}
