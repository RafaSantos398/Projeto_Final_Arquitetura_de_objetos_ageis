package com.berxy.projeto.PlanoIntercambio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanoIntercambioRepository extends JpaRepository<PlanoIntercambio, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from PlanoIntercambio p where p.id = :id")
    java.util.Optional<PlanoIntercambio> bloquearPorId(@org.springframework.data.repository.query.Param("id") Long id);
    java.util.List<PlanoIntercambio> findByUserIdOrderByIdAsc(Long usuarioId);
}
