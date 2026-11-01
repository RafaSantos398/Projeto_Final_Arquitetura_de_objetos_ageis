package com.berxy.projeto.AreaEstudo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AreaEstudoService {

    @Autowired
    private AreaEstudoRepository areaEstudoRepository;

    public List<AreaEstudo> listar() {
        return areaEstudoRepository.findAll();
    }

    public AreaEstudo buscarPorId(Long id) {
        return areaEstudoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Área de estudo não encontrada."));
    }

    public AreaEstudo cadastrar(String nome, String descricao) {
        AreaEstudo area = new AreaEstudo(nome, descricao);
        return areaEstudoRepository.save(area);
    }

    public AreaEstudo atualizar(Long id, String nome, String descricao) {
        AreaEstudo area = buscarPorId(id);
        area.atualizarArea(nome, descricao);
        return areaEstudoRepository.save(area);
    }
}