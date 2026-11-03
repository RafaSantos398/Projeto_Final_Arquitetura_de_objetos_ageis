package com.berxy.projeto.Curso;

import com.berxy.projeto.ModeloTarefa.ModeloTarefa;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cursos")
public class CursoController {
    private final CursoService service;

    public CursoController(CursoService service) { this.service = service; }

    @PostMapping("/pessoais")
    @ResponseStatus(HttpStatus.CREATED)
    public CursoResponse criarPessoal(@RequestBody CursoPessoalRequest dados) {
        return CursoResponse.de(service.criarPessoal(dados));
    }

    @PutMapping("/pessoais/{id}")
    public CursoResponse editarPessoal(@PathVariable Long id, @RequestBody CursoPessoalRequest dados) {
        return CursoResponse.de(service.atualizarPessoal(id, dados));
    }

    // GET /cursos ou GET /cursos?instituicaoId=1
    @GetMapping
    public List<CursoResponse> list(@RequestParam(required = false) Long instituicaoId) {
        List<CursoResponse> respostas = new ArrayList<>();
        for (Curso curso : service.list(instituicaoId)) {
            respostas.add(CursoResponse.de(curso));
        }
        return respostas;
    }

    @GetMapping("/{id}")
    public CursoResponse buscar(@PathVariable Long id) {
        return CursoResponse.de(service.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CursoResponse salvar(@RequestBody CursoRequest dados) {
        return CursoResponse.de(service.adicionarCurso(dados));
    }

    @PutMapping("/{id}")
    public CursoResponse atualizar(@PathVariable Long id, @RequestBody CursoRequest dados) {
        return CursoResponse.de(service.atualizarCurso(id, dados));
    }

    @PatchMapping("/{id}/ativar")
    public CursoResponse ativar(@PathVariable Long id) {
        return CursoResponse.de(service.ativarCurso(id));
    }

    @PatchMapping("/{id}/desativar")
    public CursoResponse desativar(@PathVariable Long id) {
        return CursoResponse.de(service.desativarCurso(id));
    }

    @GetMapping("/{id}/modelos-tarefa")
    public List<ModeloTarefa> listarModelos(@PathVariable Long id) {
        return service.listarModelosAtivos(id);
    }

    @PostMapping("/{id}/modelos-tarefa/{idModelo}")
    public List<ModeloTarefa> associarModelo(@PathVariable Long id, @PathVariable Long idModelo) {
        return service.associarModelo(id, idModelo);
    }

    @DeleteMapping("/{id}/modelos-tarefa/{idModelo}")
    public List<ModeloTarefa> desassociarModelo(@PathVariable Long id, @PathVariable Long idModelo) {
        return service.desassociarModelo(id, idModelo);
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