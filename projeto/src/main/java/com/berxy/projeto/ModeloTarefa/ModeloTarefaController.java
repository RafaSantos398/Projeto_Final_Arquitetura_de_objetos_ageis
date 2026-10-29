package com.berxy.projeto.ModeloTarefa;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/modelos-tarefa")
public class ModeloTarefaController {

    @Autowired
    private ModeloTarefaService modeloTarefaService;

    @GetMapping
    public List<ModeloTarefa> list(){return modeloTarefaService.list();}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModeloTarefa salvar(@RequestBody ModeloTarefaRequest dados) {
        return modeloTarefaService.adicionarModelo(
                dados.titulo(),
                dados.descricao(),
                dados.categoria()
        );
    }

    @GetMapping("/{idModelo}")
    public ModeloTarefa buscar(@PathVariable Long idModelo){
        return modeloTarefaService.GetByid(idModelo);
    }

    @PutMapping("/{idModelo}")
    @ResponseStatus(HttpStatus.OK)
    public ModeloTarefa Atualizar(@RequestBody ModeloTarefaRequest dados,
                                  @PathVariable Long idModelo){
        return modeloTarefaService.atualizarModelo(idModelo,dados.titulo(), dados.descricao(),dados.categoria());}


    @PatchMapping("/{idModelo}")
    public ModeloTarefa desativar(@PathVariable Long idModelo){
        return modeloTarefaService.destivarModelo(idModelo);
    }




}
