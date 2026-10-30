package com.berxy.projeto.AreaEstudo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaEstudoRepository extends JpaRepository<AreaEstudo, Long> {
}
