package com.es.cinema.tickets.persistence.repository;

import com.es.cinema.tickets.persistence.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    @Query("""
            SELECT a FROM Avaliacao a
              JOIN FETCH a.usuario
              JOIN FETCH a.sessao s
              JOIN FETCH s.sala
            WHERE a.filme.id = :filmeId
            ORDER BY a.criadoEm DESC
            """)
    List<Avaliacao> findAllByFilmeId(@Param("filmeId") Long filmeId);

    boolean existsByIngressoId(Long ingressoId);
}
