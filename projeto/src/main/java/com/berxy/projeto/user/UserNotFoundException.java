package com.berxy.projeto.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuário não encontrado: id " + id);
    }

    public UserNotFoundException(String email) {
        super("Usuário não encontrado: " + email);
    }
}