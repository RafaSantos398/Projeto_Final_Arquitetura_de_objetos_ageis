package com.berxy.projeto.user;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException() {
        super("Senha atual incorreta");
    }
}
