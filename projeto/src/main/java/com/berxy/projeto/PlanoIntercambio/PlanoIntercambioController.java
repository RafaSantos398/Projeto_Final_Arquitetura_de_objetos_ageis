package com.berxy.projeto.PlanoIntercambio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/planos-intercambio")
public class PlanoIntercambioController {
    @Autowired
    private PlanoIntercambioService planoIntercambioService;

    @GetMapping
    public List<PlanoIntercambioResponse> list() {
        List<PlanoIntercambioResponse> respostas = new ArrayList<>();
        for (PlanoIntercambio plano : planoIntercambioService.list()) {
            respostas.add(PlanoIntercambioResponse.de(plano));
        }
        return respostas;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanoIntercambioResponse salvar(@RequestBody PlanoIntercambioRequest dados) {
        PlanoIntercambio plano = planoIntercambioService.adicionarPlano(
                dados.usuarioId(), dados.titulo(), dados.tipoIntercambio(), dados.status(),
                dados.orcamentoTotal(), dados.moeda(), dados.inicioPrevisto(),
                dados.duracaoMeses(), dados.dependeBolsa()
        );
        return PlanoIntercambioResponse.de(plano);
    }

    @GetMapping("/{idPlano}")
    public PlanoIntercambioResponse buscar(@PathVariable("idPlano") Long idPlano) {
        return PlanoIntercambioResponse.de(planoIntercambioService.GetByid(idPlano));
    }

    @PutMapping("/{idPlano}")
    @ResponseStatus(HttpStatus.OK)
    public PlanoIntercambioResponse atualizar(
            @PathVariable("idPlano") Long idPlano,
            @RequestBody PlanoIntercambioRequest dados
    ) {
        PlanoIntercambio plano = planoIntercambioService.atualizarPlano(
                idPlano, dados.titulo(), dados.tipoIntercambio(), dados.status(),
                dados.orcamentoTotal(), dados.moeda(), dados.inicioPrevisto(),
                dados.duracaoMeses(), dados.dependeBolsa()
        );
        return PlanoIntercambioResponse.de(plano);
    }

    @PatchMapping("/{idPlano}/curso")
    public PlanoIntercambioResponse selecionarCurso(
            @PathVariable("idPlano") Long idPlano,
            @RequestBody PlanoIntercambioRequest.CursoRequest dados
    ) {
        return PlanoIntercambioResponse.de(
                planoIntercambioService.selecionarCurso(idPlano, dados.cursoId())
        );
    }

    @PatchMapping("/{idPlano}/status")
    public PlanoIntercambioResponse alterarStatus(
            @PathVariable("idPlano") Long idPlano,
            @RequestBody PlanoIntercambioRequest.StatusRequest dados
    ) {
        return PlanoIntercambioResponse.de(
                planoIntercambioService.alterarStatus(idPlano, dados.status())
        );
    }
}
