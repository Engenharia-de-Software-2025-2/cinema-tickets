package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.exception.business.IngressoAcessoNegadoException;
import com.es.cinema.tickets.exception.notfound.IngressoNotFoundException;
import com.es.cinema.tickets.persistence.entity.Ingresso;
import com.es.cinema.tickets.persistence.entity.User;
import com.es.cinema.tickets.persistence.enums.Role;
import com.es.cinema.tickets.persistence.repository.IngressoRepository;
import com.es.cinema.tickets.web.dto.response.IngressoDetalheResponse;
import com.es.cinema.tickets.web.dto.response.IngressosListResponse;
import com.es.cinema.tickets.web.mapper.IngressoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IngressoServiceTest {

    private IngressoRepository ingressoRepository;
    private IngressoMapper ingressoMapper;
    private IngressoService ingressoService;

    @BeforeEach
    void setUp() {
        ingressoRepository = mock(IngressoRepository.class);
        ingressoMapper = mock(IngressoMapper.class);
        ingressoService = new IngressoService(ingressoRepository, ingressoMapper);
    }

    @Test
    void buscarPorCodigo_shouldReturnDetalheResponse_whenIngressoExistsAndUserMatches() {
        // cria User usando builder
        User user = User.builder()
                .id(1L)
                .email("luis@email.com")
                .passwordHash("encoded")
                .role(Role.USER)
                .nome("Luis")
                .cpf("12345678901")
                .celular("999999999")
                .build();

        Ingresso ingresso = Ingresso.builder()
                .id(10L)
                .codigo("ABC123")
                .usuario(user)
                .build();

        when(ingressoRepository.findByCodigoComDetalhes("ABC123")).thenReturn(Optional.of(ingresso));

        IngressoDetalheResponse detalheResponse = IngressoDetalheResponse.builder()
                .ingressoId("10")
                .status("CONFIRMADO")
                .codigoAutenticacao("XYZ")
                .build();

        when(ingressoMapper.toDetalheResponse(ingresso)).thenReturn(detalheResponse);

        IngressoDetalheResponse result = ingressoService.buscarPorCodigo("ABC123", 1L);

        assertNotNull(result);
        verify(ingressoRepository).findByCodigoComDetalhes("ABC123");
        verify(ingressoMapper).toDetalheResponse(ingresso);
    }

    @Test
    void buscarPorCodigo_shouldThrowNotFound_whenIngressoDoesNotExist() {
        when(ingressoRepository.findByCodigoComDetalhes("NOTFOUND")).thenReturn(Optional.empty());

        assertThrows(IngressoNotFoundException.class, () ->
                ingressoService.buscarPorCodigo("NOTFOUND", 1L)
        );
    }

    @Test
    void buscarPorCodigo_shouldThrowAcessoNegado_whenUserIdDoesNotMatch() {
        User user = User.builder()
                .id(2L) // diferente do userId passado
                .email("luis@email.com")
                .passwordHash("encoded")
                .role(Role.USER)
                .nome("Luis")
                .cpf("12345678901")
                .celular("999999999")
                .build();

        Ingresso ingresso = Ingresso.builder()
                .id(10L)
                .codigo("ABC123")
                .usuario(user)
                .build();

        when(ingressoRepository.findByCodigoComDetalhes("ABC123")).thenReturn(Optional.of(ingresso));

        assertThrows(IngressoAcessoNegadoException.class, () ->
                ingressoService.buscarPorCodigo("ABC123", 1L)
        );
    }

    @Test
    void listarPorUsuario_shouldReturnListResponse() {
        User user = User.builder()
                .id(1L)
                .email("luis@email.com")
                .passwordHash("encoded")
                .role(Role.USER)
                .nome("Luis")
                .cpf("12345678901")
                .celular("999999999")
                .build();

        Ingresso ingresso1 = Ingresso.builder().id(10L).usuario(user).build();
        Ingresso ingresso2 = Ingresso.builder().id(11L).usuario(user).build();

        List<Ingresso> ingressos = List.of(ingresso1, ingresso2);

        when(ingressoRepository.findAllByUsuarioId(1L)).thenReturn(ingressos);

        when(ingressoMapper.toResumoList(ingressos)).thenReturn(List.of());

        IngressosListResponse result = ingressoService.listarPorUsuario(1L);

        assertNotNull(result);
        verify(ingressoRepository).findAllByUsuarioId(1L);
        verify(ingressoMapper).toResumoList(ingressos);
    }
}