package com.berxy.projeto.PlanoIntercambio;

import com.berxy.projeto.Curso.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanoCursoRepository extends JpaRepository<Curso, Long> {
}
