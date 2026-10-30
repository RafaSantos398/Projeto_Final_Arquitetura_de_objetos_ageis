package com.berxy.projeto.TarefaPlano;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/planos-intercambio/{idPlano}/tarefas")
public class TarefaPlanoController {
    @Autowired
    private TarefaPlanoService tarefaPlanoService;

    @GetMapping
    public List<TarefaPlanoResponse> list(@PathVariable("idPlano") Long idPlano) {
        List<TarefaPlanoResponse> respostas = new ArrayList<>();
        for (TarefaPlano tarefa : tarefaPlanoService.list(idPlano)) {
            respostas.add(TarefaPlanoResponse.de(tarefa));
        }
        return respostas;
    }

    @GetMapping("/{idTarefa}")
    public TarefaPlanoResponse buscar(
            @PathVariable("idPlano") Long idPlano,
            @PathVariable("idTarefa") Long idTarefa
    ) {
        return TarefaPlanoResponse.de(tarefaPlanoService.GetByid(idPlano, idTarefa));
    }

    @PostMapping("/gerar")
    public TarefaPlanoResponse gerar(
            @PathVariable("idPlano") Long idPlano,
            @RequestBody TarefaPlanoRequest dados
    ) {
        return TarefaPlanoResponse.de(
                tarefaPlanoService.gerarTarefa(idPlano, dados.modeloOrigemId())
        );
    }

    @PatchMapping("/{idTarefa}/iniciar")
    public TarefaPlanoResponse iniciar(
            @PathVariable("idPlano") Long idPlano,
            @PathVariable("idTarefa") Long idTarefa
    ) {
        return TarefaPlanoResponse.de(tarefaPlanoService.iniciar(idPlano, idTarefa));
    }

    @PatchMapping("/{idTarefa}/concluir")
    public TarefaPlanoResponse concluir(
            @PathVariable("idPlano") Long idPlano,
            @PathVariable("idTarefa") Long idTarefa
    ) {
        return TarefaPlanoResponse.de(tarefaPlanoService.concluir(idPlano, idTarefa));
    }

    @PatchMapping("/{idTarefa}/reabrir")
    public TarefaPlanoResponse reabrir(
            @PathVariable("idPlano") Long idPlano,
            @PathVariable("idTarefa") Long idTarefa
    ) {
        return TarefaPlanoResponse.de(tarefaPlanoService.reabrir(idPlano, idTarefa));
    }

    @PatchMapping("/{idTarefa}/prazo")
    public TarefaPlanoResponse alterarPrazo(
            @PathVariable("idPlano") Long idPlano,
            @PathVariable("idTarefa") Long idTarefa,
            @RequestBody TarefaPlanoRequest.PrazoRequest dados
    ) {
        return TarefaPlanoResponse.de(
                tarefaPlanoService.alterarPrazo(idPlano, idTarefa, dados.prazo())
        );
    }
}
