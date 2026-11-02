package com.berxy.projeto.AreaEstudo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/area-estudo")
public class AreaEstudoController {

    @ExceptionHandler(java.util.NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String naoEncontrada(java.util.NoSuchElementException erro) {
        return erro.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String dadosInvalidos(IllegalArgumentException erro) {
        return erro.getMessage();
    }

    @Autowired
    private AreaEstudoService areaEstudoService;
    @GetMapping
    public List<AreaEstudo> listar() {
        return areaEstudoService.listar();
    }

    @GetMapping("/{idArea}")
    public AreaEstudo buscar(@PathVariable Long idArea) {
        return areaEstudoService.buscarPorId(idArea);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AreaEstudo cadastrar(@RequestBody AreaEstudoRequest dados) {
        return areaEstudoService.cadastrar(dados.nome(), dados.descricao());
    }

    @PutMapping("/{idArea}")
    public AreaEstudo atualizar(@PathVariable Long idArea, @RequestBody AreaEstudoRequest dados) {
        return areaEstudoService.atualizar(idArea, dados.nome(), dados.descricao());
    }

}
