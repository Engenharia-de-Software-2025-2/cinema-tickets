package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.exception.notfound.SalaNotFoundException;
import com.es.cinema.tickets.persistence.entity.Sala;
import com.es.cinema.tickets.persistence.repository.SalaRepository;
import com.es.cinema.tickets.web.dto.response.SalaResponse;
import com.es.cinema.tickets.web.mapper.SalaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalaServiceTest {

    private SalaRepository salaRepository;
    private SalaMapper salaMapper;
    private SalaService salaService;

    @BeforeEach
    void setUp() {
        salaRepository = mock(SalaRepository.class);
        salaMapper = mock(SalaMapper.class);
        salaService = new SalaService(salaRepository, salaMapper);
    }

    @Test
    void listarTodas_deveRetornarListaDeSalaResponse() {
        Sala sala1 = Sala.builder().id(1L).nome("Sala 1").capacidade(100).build();
        Sala sala2 = Sala.builder().id(2L).nome("Sala 2").capacidade(80).build();

        when(salaRepository.findAll()).thenReturn(List.of(sala1, sala2));

        SalaResponse response1 = new SalaResponse(1L, "Sala 1", 100);
        SalaResponse response2 = new SalaResponse(2L, "Sala 2", 80);

        when(salaMapper.toResponse(sala1)).thenReturn(response1);
        when(salaMapper.toResponse(sala2)).thenReturn(response2);

        List<SalaResponse> resultado = salaService.listarTodas();

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(response1));
        assertTrue(resultado.contains(response2));

        verify(salaRepository).findAll();
        verify(salaMapper).toResponse(sala1);
        verify(salaMapper).toResponse(sala2);
    }

    @Test
    void getOrThrow_deveRetornarSalaExistente() {
        Sala sala = Sala.builder().id(1L).nome("Sala 1").capacidade(100).build();
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));

        Sala resultado = salaService.getOrThrow(1L);

        assertEquals(sala, resultado);
        verify(salaRepository).findById(1L);
    }

    @Test
    void getOrThrow_deveLancarSalaNotFoundException() {
        when(salaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(SalaNotFoundException.class, () -> salaService.getOrThrow(999L));
        verify(salaRepository).findById(999L);
    }

    @Test
    void buscarPorId_deveRetornarSalaResponse() {
        Sala sala = Sala.builder().id(1L).nome("Sala 1").capacidade(100).build();
        SalaResponse response = new SalaResponse(1L, "Sala 1", 100);

        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(salaMapper.toResponse(sala)).thenReturn(response);

        SalaResponse resultado = salaService.buscarPorId(1L);

        assertEquals(response, resultado);
        verify(salaRepository).findById(1L);
        verify(salaMapper).toResponse(sala);
    }

    @Test
    void buscarPorId_deveLancarSalaNotFoundException() {
        when(salaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(SalaNotFoundException.class, () -> salaService.buscarPorId(999L));
        verify(salaRepository).findById(999L);
    }
}