package com.es.cinema.tickets.web.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PagamentoResponse {
    private String status;
    private String mensagem;
    private String ingressoId;
}
