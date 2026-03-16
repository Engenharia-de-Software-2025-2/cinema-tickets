package com.es.cinema.tickets.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SalaResponse {
    private long id;
    private String nome;
    private int capacidade;
}
