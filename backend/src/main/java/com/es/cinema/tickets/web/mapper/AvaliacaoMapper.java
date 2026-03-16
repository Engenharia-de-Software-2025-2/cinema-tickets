package com.es.cinema.tickets.web.mapper;

import com.es.cinema.tickets.persistence.entity.Avaliacao;
import com.es.cinema.tickets.web.dto.response.AvaliacaoRegistradaResponse;
import com.es.cinema.tickets.web.dto.response.AvaliacaoResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvaliacaoMapper {

    public AvaliacaoResponse toResponse(Avaliacao avaliacao) {
        return AvaliacaoResponse.builder()
                .id(avaliacao.getId())
                .nota(avaliacao.getNota())
                .comentario(avaliacao.getComentario())
                .nomeUsuario(avaliacao.getUsuario().getNome())
                .criadoEm(avaliacao.getCriadoEm())
                .detalhesSessao(AvaliacaoResponse.DetalhesSessaoResponse.builder()
                        .sala(avaliacao.getSessao().getSala().getNome())
                        .data(avaliacao.getSessao().getInicio().toLocalDate().toString())
                        .horario(avaliacao.getSessao().getInicio().toLocalTime().toString())
                        .build())
                .build();
    }

    public List<AvaliacaoResponse> toResponseList(List<Avaliacao> avaliacoes) {
        return avaliacoes.stream()
                .map(this::toResponse)
                .toList();
    }

    public AvaliacaoRegistradaResponse toRegistradaResponse(Avaliacao avaliacao) {
        return AvaliacaoRegistradaResponse.builder()
                .id(avaliacao.getId())
                .nota(avaliacao.getNota())
                .comentario(avaliacao.getComentario())
                .ingressoId(avaliacao.getIngresso().getCodigo())
                .filmeTitulo(avaliacao.getFilme().getTitulo())
                .mensagem("Avaliação registrada com sucesso!")
                .build();
    }
}
