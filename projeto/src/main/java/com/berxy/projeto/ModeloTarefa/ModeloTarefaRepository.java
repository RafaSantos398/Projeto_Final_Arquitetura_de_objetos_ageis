package com.berxy.projeto.ModeloTarefa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeloTarefaRepository extends JpaRepository<ModeloTarefa, Long>{
}

