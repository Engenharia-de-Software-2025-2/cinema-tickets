package com.es.cinema.tickets.web.controller;

import com.es.cinema.tickets.application.service.ValidacaoService;
import com.es.cinema.tickets.web.dto.request.ValidacaoIngressoRequest;
import com.es.cinema.tickets.web.dto.response.ValidacaoIngressoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingressos")
@RequiredArgsConstructor
public class ValidacaoController {

    private final ValidacaoService validacaoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/validacoes")
    public ResponseEntity<ValidacaoIngressoResponse> validar(
            @Valid @RequestBody ValidacaoIngressoRequest request
    ) {
        ValidacaoIngressoResponse response = validacaoService.validar(request.getCodigoVoucher());
        return ResponseEntity.ok(response);
    }
}
