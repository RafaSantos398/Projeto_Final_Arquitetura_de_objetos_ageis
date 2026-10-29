package com.berxy.projeto.Insituicao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/instituicoes")
public class InstituicaoController {
    private final InstituicaoService service;

    public InstituicaoController(InstituicaoService service) { this.service = service; }

    @GetMapping
    public List<Instituicao> list() { return service.list(); }

    @GetMapping("/{id}")
    public Instituicao buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Instituicao salvar(@RequestBody InstituicaoRequest dados) {
        return service.adicionarInstituicao(dados);
    }

    @PutMapping("/{id}")
    public Instituicao atualizar(@PathVariable Long id, @RequestBody InstituicaoRequest dados) {
        return service.atualizarInstituicao(id, dados);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarValidacao(IllegalArgumentException e) {
        return Map.of("mensagem", e.getMessage());
    }
}
