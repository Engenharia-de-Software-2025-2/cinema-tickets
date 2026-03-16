package com.es.cinema.tickets.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AvaliacaoResponse {

    private Long id;
    private Integer nota;
    private String comentario;

    @JsonProperty("nome_usuario")
    private String nomeUsuario;

    @JsonProperty("criado_em")
    private LocalDateTime criadoEm;

    @JsonProperty("detalhes_sessao")
    private DetalhesSessaoResponse detalhesSessao;

    @Getter
    @Builder
    public static class DetalhesSessaoResponse {
        private String sala;
        private String data;
        private String horario;
    }
}
