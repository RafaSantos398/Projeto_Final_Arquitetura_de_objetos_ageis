package com.berxy.projeto.PreparacaoIdioma;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreparacaoIdiomaRepository extends JpaRepository<PreparacaoIdioma, Long> {

    List<PreparacaoIdioma> findByPlanoIntercambioId(Long planoId);

    Optional<PreparacaoIdioma> findByIdAndPlanoIntercambioId(Long id, Long planoId);
}
