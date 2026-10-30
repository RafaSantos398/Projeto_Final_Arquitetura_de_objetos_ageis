package com.berxy.projeto.TarefaPlano;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaPlanoRepository extends JpaRepository<TarefaPlano, Long> {
}
