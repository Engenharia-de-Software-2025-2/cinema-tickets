package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.persistence.entity.Notificacao;
import com.es.cinema.tickets.persistence.entity.Sessao;
import com.es.cinema.tickets.persistence.entity.Filme;
import com.es.cinema.tickets.persistence.entity.Sala;
import com.es.cinema.tickets.persistence.repository.NotificacaoRepository;
import com.es.cinema.tickets.persistence.repository.SessaoRepository;
import com.es.cinema.tickets.web.dto.request.NotificacaoRequest;
import com.es.cinema.tickets.web.dto.response.NotificacaoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificacaoServiceTest {

    private NotificacaoRepository notificacaoRepository;
    private SessaoRepository sessaoRepository;
    private NotificacaoService notificacaoService;

    @BeforeEach
    void setUp() {
        notificacaoRepository = mock(NotificacaoRepository.class);
        sessaoRepository = mock(SessaoRepository.class);
        notificacaoService = new NotificacaoService(notificacaoRepository, sessaoRepository);
    }

    @Test
    void agendar_shouldSaveNotificacaoAndReturnResponse() {
        Filme filme = Filme.builder().titulo("Filme Teste").build();
        Sala sala = Sala.builder().nome("Sala 1").build();
        Sessao sessao = Sessao.builder()
                .id(1L)
                .filme(filme)
                .sala(sala)
                .inicio(LocalDateTime.of(2026, 3, 16, 20, 0))
                .tipo("2D")
                .build();

        when(sessaoRepository.findWithFilmeAndSalaById(1L)).thenReturn(Optional.of(sessao));

        NotificacaoRequest dto = new NotificacaoRequest(
                List.of(1L), 
                30, 
                1L, 
                "token123"
        );

        NotificacaoResponse response = notificacaoService.agendar(dto, 1L);

        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(notificacaoRepository).save(captor.capture());
        Notificacao saved = captor.getValue();

        assertEquals("token123", saved.getDeviceToken());
        assertEquals(1L, saved.getUsuarioId());
        assertEquals("Filme Teste", saved.getTituloFilme());
        assertEquals("Sala 1", saved.getSala());
        assertEquals("20:00", saved.getHorario());
        assertFalse(saved.isVisto());
        assertFalse(saved.isEnviado());
        assertNotNull(saved.getDataEnvioAgendada());

        assertEquals("SUCESSO", response.status());
    }

    @Test
    void agendar_shouldThrowException_whenSessaoNotFound() {
        when(sessaoRepository.findWithFilmeAndSalaById(999L)).thenReturn(Optional.empty());

        NotificacaoRequest dto = new NotificacaoRequest(
                List.of(1L), 
                30,
                999L,
                "token123"
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                notificacaoService.agendar(dto, 1L)
        );

        assertTrue(exception.getMessage().contains("Sessão não encontrada"));
    }

    @Test
    void listarPorUsuario_shouldReturnOnlyPastOrCurrentNotificacoes() {
        LocalDateTime agora = LocalDateTime.now(java.time.ZoneId.of("America/Sao_Paulo"));
        Long usuarioId = 1L;

        Notificacao n1 = Notificacao.builder()
                .id(1L)
                .usuarioId(usuarioId)
                .enviado(true) 
                .dataEnvioAgendada(agora.minusHours(1))
                .build();

        Notificacao n2 = Notificacao.builder()
                .id(2L)
                .usuarioId(usuarioId)
                .enviado(false)
                .dataEnvioAgendada(agora.plusHours(1)) 
                .build();

        when(notificacaoRepository.findByUsuarioIdOrderByDataEnvioAgendadaDesc(usuarioId))
                .thenReturn(List.of(n1, n2));

        List<Notificacao> result = notificacaoService.listarPorUsuario(usuarioId);

        assertEquals(1, result.size());
        assertEquals(n1.getId(), result.getFirst().getId());
    }

    @Test
    void alternarVisto_shouldToggleVisto() {
        Notificacao n = Notificacao.builder().id(1L).visto(false).build();
        when(notificacaoRepository.findById(1L)).thenReturn(Optional.of(n));

        notificacaoService.alternarVisto(1L);

        assertTrue(n.isVisto());
        verify(notificacaoRepository).save(n);

        notificacaoService.alternarVisto(1L);
        assertFalse(n.isVisto());
        verify(notificacaoRepository, times(2)).save(n);
    }

    @Test
    void alternarVisto_shouldThrowException_whenNotificacaoNotFound() {
        when(notificacaoRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                notificacaoService.alternarVisto(999L)
        );

        assertTrue(exception.getMessage().contains("Notificação não encontrada"));
    }
}