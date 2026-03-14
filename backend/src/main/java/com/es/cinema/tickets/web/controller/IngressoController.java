package com.es.cinema.tickets.web.controller;

import com.es.cinema.tickets.application.service.IngressoService;
import com.es.cinema.tickets.security.AuthUserDetails;
import com.es.cinema.tickets.web.dto.response.IngressoDetalheResponse;
import com.es.cinema.tickets.web.dto.response.IngressosListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingressos")
@RequiredArgsConstructor
public class IngressoController {

    private final IngressoService ingressoService;

    @GetMapping("/{codigo}")
    public ResponseEntity<IngressoDetalheResponse> buscarPorCodigo(
            @PathVariable String codigo,
            @AuthenticationPrincipal AuthUserDetails userDetails
    ) {
        IngressoDetalheResponse response = ingressoService.buscarPorCodigo(codigo, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<IngressosListResponse> listar(
            @AuthenticationPrincipal AuthUserDetails userDetails
    ) {
        IngressosListResponse response = ingressoService.listarPorUsuario(userDetails.getId());
        return ResponseEntity.ok(response);
    }
}
