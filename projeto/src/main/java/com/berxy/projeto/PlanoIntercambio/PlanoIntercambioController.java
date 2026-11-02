package com.berxy.projeto.PlanoIntercambio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planos-intercambio")
public class PlanoIntercambioController {
    @Autowired
    private PlanoIntercambioService planoIntercambioService;

    @Autowired
    private com.berxy.projeto.security.AcessoService acesso;

    @GetMapping
    public List<PlanoIntercambioResponse> list(org.springframework.security.core.Authentication authentication) {
        List<PlanoIntercambioResponse> respostas = new ArrayList<>();
        var usuario = acesso.usuarioAtual(authentication);
        List<PlanoIntercambio> planos = usuario.getRole() == com.berxy.projeto.user.Role.ADMIN
                ? planoIntercambioService.list() : planoIntercambioService.listarDoUsuario(usuario.getId());
        for (PlanoIntercambio plano : planos) {
            respostas.add(PlanoIntercambioResponse.de(plano));
        }
        return respostas;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanoIntercambioResponse salvar(@RequestBody PlanoIntercambioRequest dados,
                                          org.springframework.security.core.Authentication authentication) {
        Long usuarioId = dados.usuarioId() == null ? acesso.usuarioAtual(authentication).getId() : dados.usuarioId();
        acesso.exigirUsuario(authentication, usuarioId);
        PlanoIntercambio plano = planoIntercambioService.adicionarPlano(
                usuarioId, dados.titulo(), dados.tipoIntercambio(), dados.status(),
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

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarDadosInvalidos(IllegalArgumentException erro) {
        return Map.of("mensagem", erro.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> tratarConflito(IllegalStateException erro) {
        return Map.of("mensagem", erro.getMessage());
    }
}
