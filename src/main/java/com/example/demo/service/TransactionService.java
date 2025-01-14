package com.example.demo.service;

import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.TransferResultDTO;
import com.example.demo.model.Account;
import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountService accountService, TransactionRepository transactionRepository) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }

    public Mono<Transaction> makeTransfer(TransferRequest request) {

        if (request.getToAccountId().equals(request.getFromAccountId())) {
            return saveTransaction(request, "FAILED", "No se puede transferir a la misma cuenta");
        }

        return accountService.getAccountById(request.getFromAccountId())
                .zipWith(accountService.getAccountById(request.getToAccountId()))
                .flatMap(accounts -> {
                    Account fromAccount = accounts.getT1();
                    Account toAccount = accounts.getT2();

                    if (fromAccount.getBalance() < request.getAmount()) {
                        return saveTransaction(
                                request, "FAILED", "No hay suficiente saldo en la cuenta de origen");
                    }

                    fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
                    toAccount.setBalance(toAccount.getBalance() + request.getAmount());

                    return accountService.updateAccount(fromAccount)
                            .then(accountService.updateAccount(toAccount))
                            .then(saveTransaction(request, "COMPLETED", "Transferencia exitosa"));
                }).onErrorResume(error -> saveTransaction(request, "FAILED", "Error en la transferencia: " + error.getMessage()));
    }

    public Flux<TransferResultDTO> makeMultipleTransfers(Flux<TransferRequest> requests) {
        return requests.flatMap(request ->
                validateRequest(request)
                        .flatMap(this::makeTransfer)
                        .map(transaction -> TransferResultDTO.builder()
                                .fromAccountId(transaction.getFromAccountId())
                                .toAccountId(transaction.getToAccountId())
                                .currency(transaction.getCurrency())
                                .amount(transaction.getAmount())
                                .success(true)
                                .message("Transferencia exitosa")
                                .build()).onErrorResume(error -> Mono.just(TransferResultDTO.builder()
                                        .fromAccountId(request.getFromAccountId())
                                        .toAccountId(request.getToAccountId())
                                        .currency(request.getCurrency())
                                        .amount(request.getAmount())
                                        .success(false)
                                        .message("Error en la transferencia: " + error.getMessage())
                                        .build()))
        );
    }

    private Mono<TransferRequest> validateRequest(TransferRequest request) {

        if (request.getFromAccountId() == null || request.getFromAccountId().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID de la cuenta origen es obligatorio"));
        }

        if (request.getToAccountId() == null || request.getToAccountId().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID de la cuenta destino es obligatorio"));
        }

        if (request.getAmount() <= 0 || request.getAmount() > 10000) {
            return Mono.error(new IllegalArgumentException("El monto debe estar entre 1 y 10000"));
        }

        return Mono.just(request);
    }

    private Mono<Transaction> saveTransaction(TransferRequest request, String status, String message) {
        Transaction transaction = Transaction.builder()
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .status(status)
                .currency(request.getCurrency())
                .message(message)
                .build();
        return transactionRepository.save(transaction);
    }
}
