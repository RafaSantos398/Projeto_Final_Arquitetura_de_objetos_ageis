package com.berxy.projeto.AreaEstudo;

import com.berxy.projeto.ModeloTarefa.ModeloTarefa;
import com.berxy.projeto.ModeloTarefa.ModeloTarefaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/area-estudo")
public class AreaEstudoController {

    @Autowired
    private AreaEstudoService areaEstudoService;

    @GetMapping
    public List<AreaEstudo> list(){return areaEstudoService.list();}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AreaEstudo salvar(@RequestBody AreaEstudoRequest dados) {
        return areaEstudoService.adicionarArea(
                dados.nome(),
                dados.descricao()
        );
    }

    @GetMapping("/{idArea}")
    public AreaEstudo buscar(@PathVariable Long idArea){
        return areaEstudoService.GetByid(idArea);
    }

    @PutMapping("/{idArea}")
    @ResponseStatus(HttpStatus.OK)
    public AreaEstudo Atualizar(@RequestBody AreaEstudoRequest dados,
                                  @PathVariable Long idArea){
        return areaEstudoService.atualizarArea(idArea,dados.nome(), dados.descricao());}





}
