package com.es.cinema.tickets.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PagamentoResponse {
    private String status;
    private String mensagem;
    private List<String> ingressosIds;

    @JsonProperty("ingresso_codigo")
    private String ingressoCodigo;
}
