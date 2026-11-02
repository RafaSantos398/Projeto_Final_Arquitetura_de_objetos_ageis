package com.berxy.projeto.PlanoIntercambio;

import com.berxy.projeto.Curso.Curso;
import com.berxy.projeto.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PlanoIntercambioService {
    @Autowired
    private PlanoIntercambioRepository planoIntercambioRepository;

    @Autowired
    private PlanoUsuarioRepository planoUsuarioRepository;

    @Autowired
    private PlanoCursoRepository planoCursoRepository;

    @Transactional(readOnly = true)
    public List<PlanoIntercambio> list() {
        return planoIntercambioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PlanoIntercambio> listarDoUsuario(Long usuarioId) {
        return planoIntercambioRepository.findByUserIdOrderByIdAsc(usuarioId);
    }

    @Transactional(readOnly = true)
    public PlanoIntercambio GetByid(Long id) {
        return planoIntercambioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plano não encontrado."));
    }

    public PlanoIntercambio adicionarPlano(
            Long usuarioId, String titulo, TipoIntercambio tipoIntercambio,
            StatusPlano status, BigDecimal orcamentoTotal, String moeda,
            LocalDate inicioPrevisto, Integer duracaoMeses, Boolean dependeBolsa
    ) {
        User usuario = planoUsuarioRepository.findById(usuarioId)
                .filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado."));

        plano.selecionarCurso(curso);
        return planoIntercambioRepository.save(plano);
    }

    public PlanoIntercambio alterarStatus(Long id, StatusPlano status) {
        PlanoIntercambio plano = GetByid(id);
        plano.alterarStatus(status);
        return planoIntercambioRepository.save(plano);
    }

    @Transactional(readOnly = true)
    public PlanejamentoFinanceiro consultarFinancas(Long id) {
        return PlanejamentoFinanceiro.de(GetByid(id), LocalDate.now());
    }

    public PlanejamentoFinanceiro atualizarFinancas(Long id, PlanejamentoFinanceiro.Request dados) {
        PlanoIntercambio plano = GetByid(id);
        plano.atualizarFinancas(dados.metaTotal(), dados.valorGuardado(), dados.aporteMensal());
        planoIntercambioRepository.save(plano);
        return PlanejamentoFinanceiro.de(plano, LocalDate.now());
    }
}
