package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.persistence.entity.Filme;
import com.es.cinema.tickets.persistence.enums.StatusFilme;
import com.es.cinema.tickets.persistence.repository.FilmeRepository;
import com.es.cinema.tickets.web.dto.response.FilmeResponse;
import com.es.cinema.tickets.web.mapper.FilmeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilmeServiceTest {

    private FilmeRepository filmeRepository;
    private FilmeMapper filmeMapper;
    private FilmeService filmeService;

    @BeforeEach
    void setUp() {
        filmeRepository = mock(FilmeRepository.class);
        filmeMapper = mock(FilmeMapper.class);
        filmeService = new FilmeService(filmeRepository, filmeMapper);
    }

    @Test
    void listarTodos_shouldReturnMappedList() {
        Filme f1 = Filme.builder()
                .id(1L)
                .titulo("Filme 1")
                .poster("poster1.jpg")
                .backdrop("backdrop1.jpg")
                .classificacao("L")
                .duracao(120)
                .status(StatusFilme.EM_CARTAZ)
                .build();

        Filme f2 = Filme.builder()
                .id(2L)
                .titulo("Filme 2")
                .poster("poster2.jpg")
                .backdrop("backdrop2.jpg")
                .classificacao("L")
                .duracao(90)
                .status(StatusFilme.EM_BREVE)
                .build();

        when(filmeRepository.findAll()).thenReturn(List.of(f1, f2));

        FilmeResponse resp1 = FilmeResponse.builder()
                .id(f1.getId())
                .titulo(f1.getTitulo())
                .poster(f1.getPoster())
                .backdrop(f1.getBackdrop())
                .classificacao(f1.getClassificacao())
                .duracao(f1.getDuracao())
                .generos(f1.getGeneros())
                .diretores(f1.getDiretores())
                .sinopse(f1.getSinopse())
                .elenco(f1.getElenco())
                .status(f1.getStatus())
                .mediaAvaliacao(f1.getMediaAvaliacao())
                .qtdAvaliacoes(f1.getQtdAvaliacoes())
                .build();

        FilmeResponse resp2 = FilmeResponse.builder()
                .id(f2.getId())
                .titulo(f2.getTitulo())
                .poster(f2.getPoster())
                .backdrop(f2.getBackdrop())
                .classificacao(f2.getClassificacao())
                .duracao(f2.getDuracao())
                .generos(f2.getGeneros())
                .diretores(f2.getDiretores())
                .sinopse(f2.getSinopse())
                .elenco(f2.getElenco())
                .status(f2.getStatus())
                .mediaAvaliacao(f2.getMediaAvaliacao())
                .qtdAvaliacoes(f2.getQtdAvaliacoes())
                .build();

        when(filmeMapper.toResponse(f1)).thenReturn(resp1);
        when(filmeMapper.toResponse(f2)).thenReturn(resp2);

        List<FilmeResponse> result = filmeService.listarTodos();

        verify(filmeRepository).findAll();
        verify(filmeMapper).toResponse(f1);
        verify(filmeMapper).toResponse(f2);

        assertEquals(2, result.size());
        assertEquals("Filme 1", result.get(0).getTitulo());
        assertEquals("Filme 2", result.get(1).getTitulo());
    }

    @Test
    void buscarPorId_shouldReturnMappedResponse_whenFilmeExists() {
        Filme f = Filme.builder()
                .id(1L)
                .titulo("Filme")
                .poster("poster.jpg")
                .backdrop("backdrop.jpg")
                .classificacao("L")
                .duracao(100)
                .status(StatusFilme.EM_CARTAZ)
                .build();

        when(filmeRepository.findById(1L)).thenReturn(Optional.of(f));

        FilmeResponse response = FilmeResponse.builder()
                .id(f.getId())
                .titulo(f.getTitulo())
                .poster(f.getPoster())
                .backdrop(f.getBackdrop())
                .classificacao(f.getClassificacao())
                .duracao(f.getDuracao())
                .generos(f.getGeneros())
                .diretores(f.getDiretores())
                .sinopse(f.getSinopse())
                .elenco(f.getElenco())
                .status(f.getStatus())
                .mediaAvaliacao(f.getMediaAvaliacao())
                .qtdAvaliacoes(f.getQtdAvaliacoes())
                .build();

        when(filmeMapper.toResponse(f)).thenReturn(response);

        FilmeResponse result = filmeService.buscarPorId(1L);

        verify(filmeRepository).findById(1L);
        verify(filmeMapper).toResponse(f);

        assertSame(response, result);
    }

    @Test
    void buscarPorId_shouldThrow_whenFilmeNotFound() {
        when(filmeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> filmeService.buscarPorId(1L)); // ou FilmeNotFoundException
    }
}