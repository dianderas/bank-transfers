package com.example.demo.service;

import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.TransferResultDTO;
import com.example.demo.model.Account;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {
    private final AccountService accountService;

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Mono<String> makeTransfer(TransferRequest request) {

        if (request.getToAccountId().equals(request.getFromAccountId())) {
            return Mono.error(new IllegalArgumentException("No se puede transferir a la misma cuenta"));
        }

        return accountService.getAccountById(request.getFromAccountId())
                .zipWith(accountService.getAccountById(request.getToAccountId()))
                .flatMap(accounts -> {
                    Account fromAccount = accounts.getT1();
                    Account toAccount = accounts.getT2();

                    if (fromAccount.getBalance() < request.getAmount()) {
                        return Mono.error(new IllegalStateException("No hay suficiente saldo en la cuenta de origen"));
                    }

                    fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
                    toAccount.setBalance(toAccount.getBalance() + request.getAmount());

                    return accountService.updateAccount(fromAccount)
                            .then(accountService.updateAccount(toAccount))
                            .then(Mono.just("Transferencia exitosa"));
                });
    }

    public Flux<TransferResultDTO> makeMultipleTransfers(Flux<TransferRequest> requests) {
        return requests.flatMap(request ->
                validateRequest(request)
                        .flatMap(validRequest -> makeTransfer(validRequest)
                                .map(result -> TransferResultDTO.builder()
                                        .fromAccountId(validRequest.getFromAccountId())
                                        .toAccountId(validRequest.getToAccountId())
                                        .amount(validRequest.getAmount())
                                        .success(true)
                                        .message("Transferencia exitosa")
                                        .build())
                        )
                        .onErrorResume(error -> Mono.just(TransferResultDTO.builder()
                                .fromAccountId(request.getFromAccountId())
                                .toAccountId(request.getToAccountId())
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
}
