package com.es.cinema.tickets.web.controller;

import com.es.cinema.tickets.application.service.FilmeService;
import com.es.cinema.tickets.web.dto.response.FilmeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/filmes")
@RequiredArgsConstructor
public class FilmeController {

    private final FilmeService filmeService;

    @GetMapping
    public ResponseEntity<List<FilmeResponseDTO>> listarTodos() {
        return ResponseEntity.ok(filmeService.listarTodos());
    }
}
