package com.es.cinema.tickets.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidacaoIngressoResponse {

    private boolean valido;
    private String status;
    private String mensagem;

    @JsonProperty("dados_ingresso")
    private DadosIngressoResponse dadosIngresso;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DadosIngressoResponse {
        private String cliente;
        private String filme;
        private String sala;
        private List<String> assentos;

        @JsonProperty("data_hora_entrada")
        private LocalDateTime dataHoraEntrada;
    }

    // factories

    public static ValidacaoIngressoResponse sucesso(
            String statusIngresso,
            String cliente,
            String filme,
            String sala,
            List<String> assentos,
            LocalDateTime dataHoraEntrada
    ) {
        return ValidacaoIngressoResponse.builder()
                .valido(true)
                .status(statusIngresso)
                .mensagem("Entrada Autorizada")
                .dadosIngresso(DadosIngressoResponse.builder()
                        .cliente(cliente)
                        .filme(filme)
                        .sala(sala)
                        .assentos(assentos)
                        .dataHoraEntrada(dataHoraEntrada)
                        .build())
                .build();
    }

    public static ValidacaoIngressoResponse falha(String mensagem) {
        return ValidacaoIngressoResponse.builder()
                .valido(false)
                .mensagem(mensagem)
                .build();
    }
}
