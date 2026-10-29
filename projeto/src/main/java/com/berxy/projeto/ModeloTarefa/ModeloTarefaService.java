package com.berxy.projeto.ModeloTarefa;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModeloTarefaService {

    @Autowired
    private ModeloTarefaRepository modeloTarefaRepository;

    public List<ModeloTarefa> list(){return modeloTarefaRepository.findAll();}

    public ModeloTarefa adicionarModelo(String titulo, String descricao, CategoriaTarefa categoria){
        ModeloTarefa modelo = new ModeloTarefa(titulo, descricao, categoria);
        return  modeloTarefaRepository.save(modelo);}


    public ModeloTarefa GetByid(Long id){
        return modeloTarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modelo nao encontrado"));}

    public ModeloTarefa atualizarModelo(Long id, String titulo, String descricao, CategoriaTarefa categoria){
        ModeloTarefa modelo = GetByid(id);
        modelo.atualizarConteudo(titulo, descricao, categoria);

        return modeloTarefaRepository.save(modelo);
    }

    public ModeloTarefa destivarModelo(Long id){

        ModeloTarefa modelo =  GetByid(id);
        modelo.desativar();
        return modeloTarefaRepository.save(modelo);
    }
}
