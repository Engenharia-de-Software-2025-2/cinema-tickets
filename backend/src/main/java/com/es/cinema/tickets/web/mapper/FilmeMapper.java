package com.es.cinema.tickets.web.mapper;

import com.es.cinema.tickets.persistence.entity.Filme;
import com.es.cinema.tickets.web.dto.response.FilmeResponseDTO;

public class FilmeMapper {

    private FilmeMapper() {
    }

    public static FilmeResponseDTO toResponseDTO(Filme filme) {
        return FilmeResponseDTO.builder()
                .id(filme.getId())
                .titulo(filme.getTitulo())
                .descricao(filme.getDescricao())
                .duracao(filme.getDuracao())
                .status(filme.getStatus())
                .build();
    }
}
