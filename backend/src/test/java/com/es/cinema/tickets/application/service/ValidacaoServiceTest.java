package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.persistence.entity.*;
import com.es.cinema.tickets.persistence.enums.*;
import com.es.cinema.tickets.persistence.repository.IngressoRepository;
import com.es.cinema.tickets.web.dto.response.ValidacaoIngressoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidacaoServiceTest {

    @Mock
    private IngressoRepository ingressoRepository;

    @InjectMocks
    private ValidacaoService validacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Ajusta a tolerância usando um setter que você deve criar
        validacaoService.setToleranciaHoras(3);
    }

    @Test
    void validar_deveRetornarFalhaCodigoInvalido() {
        when(ingressoRepository.findByCodigoComDetalhes("INVALID")).thenReturn(Optional.empty());

        ValidacaoIngressoResponse response = validacaoService.validar("INVALID");

        assertFalse(response.isValido());
        assertEquals("Código inválido", response.getMensagem());
    }

    @Test
    void validar_deveFalharSePedidoNaoPago() {
        Pedido pedido = Pedido.builder().status(StatusPedido.PENDENTE).build();
        Ingresso ingresso = criarIngresso(pedido);

        when(ingressoRepository.findByCodigoComDetalhes("COD123")).thenReturn(Optional.of(ingresso));

        ValidacaoIngressoResponse response = validacaoService.validar("COD123");

        assertFalse(response.isValido());
        assertEquals("Pagamento não confirmado para este ingresso", response.getMensagem());
    }

    @Test
    void validar_deveValidarComSucesso() {
        LocalDateTime inicioSessao = LocalDateTime.now().plusMinutes(10);
        Sessao sessao = Sessao.builder()
                .id(1L)
                .inicio(inicioSessao)
                .tipo("2D")
                .filme(Filme.builder().titulo("Filme Teste").build())
                .sala(Sala.builder().nome("Sala 1").capacidade(100).build())
                .build();

        AssentoSessao assento = AssentoSessao.builder()
                .id(1L)
                .codigo("B2")
                .build();

        Pedido pedido = Pedido.builder()
                .status(StatusPedido.PAGO)
                .sessao(sessao)
                .assentos(Set.of(assento))
                .build();

        User user = User.builder().id(1L).nome("Luis").build();
        Ingresso ingresso = Ingresso.builder()
                .id(1L)
                .codigo("COD123")
                .pedido(pedido)
                .usuario(user)
                .status(StatusIngresso.CONFIRMADO)
                .build();

        when(ingressoRepository.findByCodigoComDetalhes("COD123")).thenReturn(Optional.of(ingresso));

        ValidacaoIngressoResponse response = validacaoService.validar("COD123");

        assertTrue(response.isValido());
        assertEquals(StatusIngresso.UTILIZADO.name(), response.getStatus());
        assertEquals("Luis", response.getDadosIngresso().getCliente());
        assertEquals(List.of("B2"), response.getDadosIngresso().getAssentos());

        ArgumentCaptor<Ingresso> captor = ArgumentCaptor.forClass(Ingresso.class);
        verify(ingressoRepository).save(captor.capture());
        assertEquals(StatusIngresso.UTILIZADO, captor.getValue().getStatus());
        assertNotNull(captor.getValue().getDataHoraEntrada());
    }

    // Helpers
    private Ingresso criarIngresso(Pedido pedido) {
        return Ingresso.builder()
                .id(1L)
                .codigo("COD123")
                .pedido(pedido)
                .usuario(User.builder().id(1L).nome("Teste").build())
                .status(StatusIngresso.CONFIRMADO)
                .build();
    }
}