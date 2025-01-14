package com.example.demo.controller;

import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.TransferResultDTO;
import com.example.demo.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfers")
    public Flux<TransferResultDTO> transferMultiple(@RequestBody Flux<TransferRequest> requests) {
        return transactionService.makeMultipleTransfers(requests);
    }
}
