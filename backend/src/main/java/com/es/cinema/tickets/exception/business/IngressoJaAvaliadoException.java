package com.es.cinema.tickets.exception.business;

import com.es.cinema.tickets.exception.ApiException;
import org.springframework.http.HttpStatus;

public class IngressoJaAvaliadoException extends ApiException {
    public IngressoJaAvaliadoException() {
        super(
                "INGRESSO_JA_AVALIADO",
                HttpStatus.CONFLICT,
                "Avaliação duplicada",
                "Este ingresso já foi avaliado."
        );
    }
}
