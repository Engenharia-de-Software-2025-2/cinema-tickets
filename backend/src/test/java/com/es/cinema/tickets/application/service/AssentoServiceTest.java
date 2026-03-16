package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.exception.notfound.SessaoNotFoundException;
import com.es.cinema.tickets.persistence.entity.AssentoSessao;
import com.es.cinema.tickets.persistence.repository.AssentoSessaoRepository;
import com.es.cinema.tickets.persistence.repository.SessaoRepository;
import com.es.cinema.tickets.web.dto.response.SessaoAssentosResponse;
import com.es.cinema.tickets.web.mapper.AssentoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssentoServiceTest {

    private AssentoSessaoRepository assentoSessaoRepository;
    private SessaoRepository sessaoRepository;
    private AssentoMapper assentoMapper;
    private AssentoService assentoService;

    @BeforeEach
    void setUp() {
        assentoSessaoRepository = mock(AssentoSessaoRepository.class);
        sessaoRepository = mock(SessaoRepository.class);
        assentoMapper = mock(AssentoMapper.class);

        assentoService = new AssentoService(assentoSessaoRepository, sessaoRepository, assentoMapper);
    }

    @Test
    void listarPorSessao_shouldThrow_whenSessaoDoesNotExist() {
        Long sessaoId = 1L;
        when(sessaoRepository.existsById(sessaoId)).thenReturn(false);

        assertThrows(SessaoNotFoundException.class, () -> assentoService.listarPorSessao(sessaoId));
    }

    @Test
    void listarPorSessao_shouldReturnMappedResponse_whenSessaoExists() {
        Long sessaoId = 1L;
        when(sessaoRepository.existsById(sessaoId)).thenReturn(true);

        AssentoSessao a1 = AssentoSessao.builder()
                .id(10L)
                .codigo("A1")
                .tipo(null)
                .valor(BigDecimal.valueOf(20))
                .status(null)
                .build();

        AssentoSessao a2 = AssentoSessao.builder()
                .id(11L)
                .codigo("A2")
                .tipo(null)
                .valor(BigDecimal.valueOf(25))
                .status(null)
                .build();

        List<AssentoSessao> assentos = List.of(a1, a2);
        when(assentoSessaoRepository.findBySessaoIdOrderByCodigoAsc(sessaoId)).thenReturn(assentos);

        SessaoAssentosResponse responseMock = mock(SessaoAssentosResponse.class);
        when(assentoMapper.toSessaoAssentosResponse(sessaoId, assentos)).thenReturn(responseMock);

        SessaoAssentosResponse result = assentoService.listarPorSessao(sessaoId);

        verify(sessaoRepository).existsById(sessaoId);
        verify(assentoSessaoRepository).findBySessaoIdOrderByCodigoAsc(sessaoId);
        verify(assentoMapper).toSessaoAssentosResponse(sessaoId, assentos);

        assertSame(responseMock, result);
    }
}