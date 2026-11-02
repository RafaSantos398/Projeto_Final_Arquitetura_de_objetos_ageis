package com.berxy.projeto.exception;

import com.berxy.projeto.user.EmailAlreadyInUseException;
import com.berxy.projeto.user.InvalidPasswordException;
import com.berxy.projeto.user.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

// Aula 08: exceções + DTO + @ExceptionHandler, sem uma hierarquia de handlers.
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse naoEncontrado(UserNotFoundException erro) {
        return new ErroResponse(404, erro.getMessage());
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResponse emailEmUso(EmailAlreadyInUseException erro) {
        return new ErroResponse(409, erro.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse senhaIncorreta(InvalidPasswordException erro) {
        return new ErroResponse(400, erro.getMessage());
    }

    @ExceptionHandler(CampoInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse campoInvalido(CampoInvalidoException erro) {
        return new ErroResponse(400, "Dados inválidos", Map.of(erro.getCampo(), erro.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResponse conflitoNoBanco() {
        return new ErroResponse(409, "Os dados conflitam com um registro existente");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse dadosInvalidos(MethodArgumentNotValidException erro) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (var campo : erro.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(campo.getField(), campo.getDefaultMessage());
        }
        return new ErroResponse(400, "Dados inválidos", campos);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse formatoInvalido() {
        return new ErroResponse(400, "Confira o JSON e os tipos dos dados enviados.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse rotaInexistente() {
        return new ErroResponse(404, "Rota não encontrada.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> statusDoServico(ResponseStatusException erro) {
        return ResponseEntity.status(erro.getStatusCode()).body(
                new ErroResponse(erro.getStatusCode().value(), erro.getReason()));
    }
}
