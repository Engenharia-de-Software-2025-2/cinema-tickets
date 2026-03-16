package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.exception.business.AssentosIndisponiveisException;
import com.es.cinema.tickets.exception.business.ValorDivergenteException;
import com.es.cinema.tickets.exception.notfound.SessaoNotFoundException;
import com.es.cinema.tickets.exception.notfound.UserNotFoundException;
import com.es.cinema.tickets.persistence.entity.*;
import com.es.cinema.tickets.persistence.enums.MetodoPagamento;
import com.es.cinema.tickets.persistence.enums.StatusAssento;
import com.es.cinema.tickets.persistence.enums.TipoAssento;
import com.es.cinema.tickets.persistence.repository.*;
import com.es.cinema.tickets.web.dto.request.PagamentoRequest;
import com.es.cinema.tickets.web.dto.response.PagamentoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagamentoServiceTest {

    private SessaoRepository sessaoRepository;
    private AssentoSessaoRepository assentoSessaoRepository;
    private PedidoRepository pedidoRepository;
    private UserRepository userRepository;
    private IngressoRepository ingressoRepository;
    private PagamentoService pagamentoService;

    @BeforeEach
    void setUp() {
        sessaoRepository = mock(SessaoRepository.class);
        assentoSessaoRepository = mock(AssentoSessaoRepository.class);
        pedidoRepository = mock(PedidoRepository.class);
        userRepository = mock(UserRepository.class);
        ingressoRepository = mock(IngressoRepository.class);
        pagamentoService = new PagamentoService(sessaoRepository, assentoSessaoRepository,
                pedidoRepository, userRepository, ingressoRepository);
    }

    @Test
    void processar_shouldAprovarCompraComSucesso() {
        User user = User.builder().id(1L).nome("Luis").build();
        Sessao sessao = Sessao.builder().id(1L).build();
        AssentoSessao assento = AssentoSessao.builder()
                .id(1L)
                .codigo("A1")
                .valor(BigDecimal.valueOf(50))
                .tipo(TipoAssento.COMUM)
                .status(StatusAssento.DISPONIVEL)
                .build();

        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(assentoSessaoRepository.findAllByIdWithLock(List.of(1L))).thenReturn(List.of(assento));

        PagamentoRequest request = new PagamentoRequest(
                1L,
                List.of(1L),
                BigDecimal.valueOf(50),
                MetodoPagamento.CARTAO_CREDITO,
                "token123"
        );

        PagamentoResponse response = pagamentoService.processar(request, 1L);

        assertEquals("aprovado", response.getStatus());
        assertEquals(1, response.getIngressosIds().size());
        assertNotNull(response.getIngressoCodigo());

        // Verifica que os métodos save foram chamados
        verify(assentoSessaoRepository).saveAll(List.of(assento));
        verify(pedidoRepository).save(any(Pedido.class));
        verify(ingressoRepository).save(any(Ingresso.class));

        assertFalse(assento.isDisponivel());
    }

    @Test
    void processar_shouldThrowSessaoNotFound() {
        when(sessaoRepository.findById(999L)).thenReturn(Optional.empty());

        PagamentoRequest request = new PagamentoRequest(
                1L,
                List.of(1L),
                BigDecimal.valueOf(50),
                MetodoPagamento.CARTAO_DEBITO,
                "token123"
        );

        assertThrows(SessaoNotFoundException.class, () ->
                pagamentoService.processar(request, 1L)
        );
    }

    @Test
    void processar_shouldThrowUserNotFound() {
        // Sessão mockada
        Filme filme = Filme.builder().id(1L).titulo("Filme Teste").build();
        Sala sala = Sala.builder().id(1L).nome("Sala 1").build();
        Sessao sessao = Sessao.builder()
                .id(1L)
                .filme(filme)
                .sala(sala)
                .inicio(LocalDateTime.now())
                .tipo("2D")
                .build();

        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(userRepository.findById(999L)).thenReturn(Optional.empty()); // usuário não existe

        PagamentoRequest request = new PagamentoRequest(
                1L,                  // Sessão existente
                List.of(1L),
                BigDecimal.valueOf(50),
                MetodoPagamento.CARTAO_DEBITO,
                "token123"
        );

        assertThrows(UserNotFoundException.class, () ->
                pagamentoService.processar(request, 999L)
        );
    }

    @Test
    void processar_shouldThrowAssentosIndisponiveisQuandoFaltam() {
        Sessao sessao = Sessao.builder().id(1L).build();
        User user = User.builder().id(1L).build();

        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(assentoSessaoRepository.findAllByIdWithLock(List.of(1L, 2L))).thenReturn(List.of()); // Nenhum assento encontrado

        PagamentoRequest request = new PagamentoRequest(
                1L,
                List.of(1L),
                BigDecimal.valueOf(50),
                MetodoPagamento.CARTAO_CREDITO,
                "token123"
        );

        assertThrows(AssentosIndisponiveisException.class, () ->
                pagamentoService.processar(request, 1L)
        );
    }

    @Test
    void processar_shouldThrowAssentosIndisponiveisQuandoNaoDisponivel() {
        Sessao sessao = Sessao.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        AssentoSessao assento = AssentoSessao.builder()
                .id(1L)
                .codigo("A1")
                .valor(BigDecimal.valueOf(50))
                .tipo(TipoAssento.COMUM)
                .status(StatusAssento.OCUPADO)
                .build();

        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(assentoSessaoRepository.findAllByIdWithLock(List.of(1L))).thenReturn(List.of(assento));

        PagamentoRequest request = new PagamentoRequest(
                1L,
                List.of(1L),
                BigDecimal.valueOf(50),
                MetodoPagamento.CARTAO_CREDITO,
                "token123"
        );

        assertThrows(AssentosIndisponiveisException.class, () ->
                pagamentoService.processar(request, 1L)
        );
    }

    @Test
    void processar_shouldThrowValorDivergente() {
        Sessao sessao = Sessao.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        AssentoSessao assento = AssentoSessao.builder()
                .id(1L)
                .codigo("A1")
                .valor(BigDecimal.valueOf(50))
                .tipo(TipoAssento.COMUM)
                .status(StatusAssento.DISPONIVEL)
                .build();

        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(assentoSessaoRepository.findAllByIdWithLock(List.of(1L))).thenReturn(List.of(assento));

        PagamentoRequest request = new PagamentoRequest(
                1L,
                List.of(1L),
                BigDecimal.valueOf(100),
                MetodoPagamento.CARTAO_CREDITO,
                "token123"
        );

        assertThrows(ValorDivergenteException.class, () ->
                pagamentoService.processar(request, 1L)
        );
    }
}