package com.berxy.projeto.Insituicao;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional
public class InstituicaoService {
    private final InstituicaoRepository repository;

    public InstituicaoService(InstituicaoRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<Instituicao> list() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public Instituicao buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituição não encontrada."));
    }

    public Instituicao adicionarInstituicao(InstituicaoRequest dados) {
        return repository.save(new Instituicao(dados.nome(), dados.descricao(), dados.siteOficial()));
    }

    public Instituicao atualizarInstituicao(Long id, InstituicaoRequest dados) {
        Instituicao instituicao = buscar(id);
        instituicao.atualizarConteudo(dados.nome(), dados.descricao(), dados.siteOficial());
        return repository.save(instituicao);
    }
}
