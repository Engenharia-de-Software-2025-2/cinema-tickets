package com.es.cinema.tickets.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AvaliacaoRegistradaResponse {

    private Long id;
    private Integer nota;
    private String comentario;

    @JsonProperty("ingresso_id")
    private String ingressoId;

    @JsonProperty("filme")
    private String filmeTitulo;

    private String mensagem;
}
