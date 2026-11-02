package com.berxy.projeto.PreparacaoIdioma;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planos-intercambio/{idPlano}/preparacoes-idioma")
public class PreparacaoIdiomaController {
    private final PreparacaoIdiomaService service;

    public PreparacaoIdiomaController(PreparacaoIdiomaService service) { this.service = service; }

    @GetMapping
    public List<PreparacaoIdiomaResponse> list(@PathVariable("idPlano") Long idPlano) {
        List<PreparacaoIdiomaResponse> respostas = new ArrayList<>();
        for (PreparacaoIdioma preparacao : service.list(idPlano)) {
            respostas.add(PreparacaoIdiomaResponse.de(preparacao));
        }
        return respostas;
    }

    @GetMapping("/{idPreparacao}")
    public PreparacaoIdiomaResponse buscar(
            @PathVariable("idPlano") Long idPlano,
            @PathVariable("idPreparacao") Long idPreparacao
    ) {
        return PreparacaoIdiomaResponse.de(service.buscar(idPlano, idPreparacao));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PreparacaoIdiomaResponse salvar(
            @PathVariable("idPlano") Long idPlano,
            @RequestBody PreparacaoIdiomaRequest dados
    ) {
        return PreparacaoIdiomaResponse.de(service.adicionarPreparacao(idPlano, dados));
    }

    @PutMapping("/{idPreparacao}")
    public PreparacaoIdiomaResponse atualizar(
            @PathVariable("idPlano") Long idPlano,
            @PathVariable("idPreparacao") Long idPreparacao,
            @RequestBody PreparacaoIdiomaRequest dados
    ) {
        return PreparacaoIdiomaResponse.de(
                service.atualizarPreparacao(idPlano, idPreparacao, dados)
        );
    }

    @PatchMapping("/{idPreparacao}/iniciar")
    public PreparacaoIdiomaResponse iniciar(
            @PathVariable("idPlano") Long idPlano,
            @PathVariable("idPreparacao") Long idPreparacao
    ) {
        return PreparacaoIdiomaResponse.de(service.iniciar(idPlano, idPreparacao));
    }

    @PatchMapping("/{idPreparacao}/concluir")
    public PreparacaoIdiomaResponse concluir(
            @PathVariable("idPlano") Long idPlano,
            @PathVariable("idPreparacao") Long idPreparacao
    ) {
        return PreparacaoIdiomaResponse.de(service.concluir(idPlano, idPreparacao));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarValidacao(IllegalArgumentException e) {
        return Map.of("mensagem", e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> tratarConflito(IllegalStateException e) {
        return Map.of("mensagem", e.getMessage());
    }
}