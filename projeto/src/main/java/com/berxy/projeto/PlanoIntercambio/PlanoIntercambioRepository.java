package com.berxy.projeto.PlanoIntercambio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanoIntercambioRepository extends JpaRepository<PlanoIntercambio, Long> {
}
