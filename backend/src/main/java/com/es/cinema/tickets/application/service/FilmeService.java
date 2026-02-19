package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.exception.notfound.FilmeNotFoundException;
import com.es.cinema.tickets.persistence.entity.Filme;
import com.es.cinema.tickets.persistence.repository.FilmeRepository;
import com.es.cinema.tickets.web.dto.response.FilmeResponseDTO;
import com.es.cinema.tickets.web.mapper.FilmeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmeService {

    private final FilmeRepository filmeRepository;

    @Transactional(readOnly = true)
    public List<FilmeResponseDTO> listarTodos() {
        return filmeRepository.findAll()
                .stream()
                .map(FilmeMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Filme buscarPorId(Long id) {
        return filmeRepository.findById(id)
                .orElseThrow(() -> new FilmeNotFoundException(id));
    }
}
