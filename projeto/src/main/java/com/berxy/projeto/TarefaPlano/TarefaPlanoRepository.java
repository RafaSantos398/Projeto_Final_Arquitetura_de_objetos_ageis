package com.berxy.projeto.TarefaPlano;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaPlanoRepository extends JpaRepository<TarefaPlano, Long> {
    Optional<TarefaPlano> findByPlanoIdAndCursoApoioId(Long planoId, Long cursoId);
    boolean existsByPlanoIdAndChaveSugestao(Long planoId, String chaveSugestao);
    List<TarefaPlano> findByPlanoIdOrderByIdAsc(Long planoId);

    Optional<TarefaPlano> findByIdAndPlanoId(Long id, Long planoId);

    Optional<TarefaPlano> findByPlanoIdAndModeloOrigemId(Long planoId, Long modeloOrigemId);
}
