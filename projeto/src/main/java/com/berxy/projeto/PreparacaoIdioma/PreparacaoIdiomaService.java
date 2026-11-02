package com.berxy.projeto.PreparacaoIdioma;

import com.berxy.projeto.PlanoIntercambio.PlanoIntercambio;
import com.berxy.projeto.PlanoIntercambio.PlanoIntercambioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class PreparacaoIdiomaService {
    private final PreparacaoIdiomaRepository repository;
    private final PlanoIntercambioRepository planoRepository;

    public PreparacaoIdiomaService(
            PreparacaoIdiomaRepository repository,
            PlanoIntercambioRepository planoRepository
    ) {
        this.repository = repository;
        this.planoRepository = planoRepository;
    }

    @Transactional(readOnly = true)
    public List<PreparacaoIdioma> list(Long planoId) {
        buscarPlano(planoId);
        return repository.findByPlanoIntercambioId(planoId);
    }

    @Transactional(readOnly = true)
    public PreparacaoIdioma buscar(Long planoId, Long id) {
        buscarPlano(planoId);
        return repository.findByIdAndPlanoIntercambioId(id, planoId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Preparação de idioma não encontrada neste plano."));
    }

    public PreparacaoIdioma adicionarPreparacao(Long planoId, PreparacaoIdiomaRequest dados) {
        PlanoIntercambio plano = buscarPlano(planoId);
        PreparacaoIdioma preparacao = new PreparacaoIdioma(
                plano, dados.idioma(), dados.nivelAlvo(),
                dados.dataMeta(), dados.minutosSemanaisPlanejados()
        );
        verificarIdiomaDuplicado(planoId, preparacao.getIdioma(), null);
        return repository.save(preparacao);
    }

    public PreparacaoIdioma atualizarPreparacao(Long planoId, Long id, PreparacaoIdiomaRequest dados) {
        PreparacaoIdioma preparacao = buscar(planoId, id);
        if (!preparacao.isConcluida()) {
            verificarIdiomaDuplicado(planoId, dados.idioma(), id);
        }
        preparacao.atualizarConteudo(
                dados.idioma(), dados.nivelAlvo(),
                dados.dataMeta(), dados.minutosSemanaisPlanejados()
        );
        return repository.save(preparacao);
    }

    public PreparacaoIdioma iniciar(Long planoId, Long id) {
        PreparacaoIdioma preparacao = buscar(planoId, id);
        preparacao.iniciar();
        return repository.save(preparacao);
    }

    public PreparacaoIdioma concluir(Long planoId, Long id) {
        PreparacaoIdioma preparacao = buscar(planoId, id);
        preparacao.concluir();
        return repository.save(preparacao);
    }

    private PlanoIntercambio buscarPlano(Long planoId) {
        return planoRepository.findById(planoId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Plano não encontrado."));
    }

    // Um plano não pode ter duas preparações em aberto para o mesmo idioma.
    private void verificarIdiomaDuplicado(Long planoId, String idioma, Long idIgnorado) {
        for (PreparacaoIdioma outra : repository.findByPlanoIntercambioId(planoId)) {
            if (!outra.getId().equals(idIgnorado) && !outra.isConcluida()
                    && outra.getIdioma().equalsIgnoreCase(idioma == null ? null : idioma.strip())) {
                throw new IllegalStateException(
                        "Já existe uma preparação em aberto para este idioma no plano."
                );
            }
        }
    }
}
