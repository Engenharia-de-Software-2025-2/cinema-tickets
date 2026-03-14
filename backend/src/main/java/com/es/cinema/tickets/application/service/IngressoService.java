package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.exception.business.IngressoAcessoNegadoException;
import com.es.cinema.tickets.exception.notfound.IngressoNotFoundException;
import com.es.cinema.tickets.persistence.entity.Ingresso;
import com.es.cinema.tickets.persistence.repository.IngressoRepository;
import com.es.cinema.tickets.web.dto.response.IngressoDetalheResponse;
import com.es.cinema.tickets.web.dto.response.IngressosListResponse;
import com.es.cinema.tickets.web.mapper.IngressoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final IngressoMapper ingressoMapper;

    @Transactional(readOnly = true)
    public IngressoDetalheResponse buscarPorCodigo(String codigo, Long usuarioId) {
        Ingresso ingresso = ingressoRepository.findByCodigoComDetalhes(codigo)
                .orElseThrow(() -> new IngressoNotFoundException(codigo));

        if (!ingresso.getUsuario().getId().equals(usuarioId)) {
            throw new IngressoAcessoNegadoException();
        }

        return ingressoMapper.toDetalheResponse(ingresso);
    }

    @Transactional(readOnly = true)
    public IngressosListResponse listarPorUsuario(Long usuarioId) {
        List<Ingresso> ingressos = ingressoRepository.findAllByUsuarioId(usuarioId);
        return IngressosListResponse.builder()
                .ingressos(ingressoMapper.toResumoList(ingressos))
                .build();
    }
}
