package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @GetMapping
    public Mono<String> healthCheck(){
        return Mono.just("La aplicación está funcionando correctamente");
    }
}
