package com.berxy.projeto.AreaEstudo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaEstudoService {

    @Autowired
    private AreaEstudoRepository areaEstudoRepository;

    public List<AreaEstudo> list(){return areaEstudoRepository.findAll();}

    public AreaEstudo adicionarArea(String nome, String descricao){
        AreaEstudo area = new AreaEstudo(nome, descricao);
        return  areaEstudoRepository.save(area);}


    public AreaEstudo GetByid(Long idArea) {
        return areaEstudoRepository.findById(idArea)
                .orElseThrow(() -> new RuntimeException("Area nao encontrado"));}

    public AreaEstudo atualizarArea(Long id, String nome, String descricao){
        AreaEstudo area = GetByid(id);
        area.atualizarArea(nome, descricao);

        return areaEstudoRepository.save(area);
    }


}

