package com.berxy.projeto.PlanoIntercambio;

import com.berxy.projeto.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanoUsuarioRepository extends JpaRepository<User, Long> {
}
