package com.berxy.projeto.PlanoIntercambio;

import com.berxy.projeto.Curso.Curso;
import com.berxy.projeto.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PlanoIntercambioService {
    @Autowired
    private PlanoIntercambioRepository planoIntercambioRepository;

    @Autowired
    private PlanoUsuarioRepository planoUsuarioRepository;

    @Autowired
    private PlanoCursoRepository planoCursoRepository;

    public List<PlanoIntercambio> list() {
        return planoIntercambioRepository.findAll();
    }

    public PlanoIntercambio GetByid(Long id) {
        return planoIntercambioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano nao encontrado"));
    }

    public PlanoIntercambio adicionarPlano(
            Long usuarioId, String titulo, TipoIntercambio tipoIntercambio,
            StatusPlano status, BigDecimal orcamentoTotal, String moeda,
            LocalDate inicioPrevisto, Integer duracaoMeses, Boolean dependeBolsa
    ) {
        User usuario = planoUsuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        PlanoIntercambio plano = new PlanoIntercambio(
                usuario, titulo, tipoIntercambio, status
        );
        plano.atualizarPlanejamento(
                orcamentoTotal, moeda, inicioPrevisto, duracaoMeses, dependeBolsa
        );
        return planoIntercambioRepository.save(plano);
    }

    public PlanoIntercambio atualizarPlano(
            Long id, String titulo, TipoIntercambio tipoIntercambio,
            StatusPlano status, BigDecimal orcamentoTotal, String moeda,
            LocalDate inicioPrevisto, Integer duracaoMeses, Boolean dependeBolsa
    ) {
        PlanoIntercambio plano = GetByid(id);
        plano.atualizarIdentificacao(titulo, tipoIntercambio);
        plano.atualizarPlanejamento(
                orcamentoTotal, moeda, inicioPrevisto, duracaoMeses, dependeBolsa
        );
        plano.alterarStatus(status);
        return planoIntercambioRepository.save(plano);
    }

    public PlanoIntercambio selecionarCurso(Long id, Long cursoId) {
        PlanoIntercambio plano = GetByid(id);
        Curso curso = planoCursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso nao encontrado"));

        plano.selecionarCurso(curso);
        return planoIntercambioRepository.save(plano);
    }

    public PlanoIntercambio alterarStatus(Long id, StatusPlano status) {
        PlanoIntercambio plano = GetByid(id);
        plano.alterarStatus(status);
        return planoIntercambioRepository.save(plano);
    }
}
