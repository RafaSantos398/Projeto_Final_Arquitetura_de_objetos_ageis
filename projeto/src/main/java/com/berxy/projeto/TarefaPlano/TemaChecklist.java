package com.berxy.projeto.TarefaPlano;

import com.berxy.projeto.ModeloTarefa.CategoriaTarefa;
import java.util.List;

// Sugestões gerais de organização. Não são requisitos legais de um destino.
public enum TemaChecklist {
    DOCUMENTOS("Passaporte e documentos", CategoriaTarefa.DOCUMENTACAO, List.of(
        new Sugestao("passaporte", "Conferir meu passaporte", "Verificar se tenho passaporte e consultar os requisitos de validade do destino nas fontes oficiais."),
        new Sugestao("emissao", "Organizar emissão ou renovação", "Se necessário, consultar o procedimento oficial, separar documentos e reservar tempo para o atendimento."),
        new Sugestao("entrada", "Pesquisar requisitos de entrada", "Consultar fontes oficiais do destino para saber quais documentos, autorizações ou vistos se aplicam à minha viagem."),
        new Sugestao("copias", "Guardar cópias dos documentos", "Organizar cópias seguras dos documentos e os contatos necessários durante a viagem.")
    )),
    FINANCAS("Reserva financeira", CategoriaTarefa.FINANCEIRO, List.of(
        new Sugestao("custos", "Estimar o custo do intercâmbio", "Anotar minhas estimativas de curso, transporte, moradia, alimentação, documentação e imprevistos, na mesma moeda."),
        new Sugestao("meta", "Definir minha meta financeira", "Preencher a aba Finanças com a meta total, o valor já guardado e quanto consigo poupar por mês."),
        new Sugestao("mensal", "Revisar meu orçamento mensal", "Comparar receitas e despesas e definir uma quantia mensal que cabe no meu orçamento."),
        new Sugestao("revisao", "Acompanhar minha reserva", "Atualizar o valor guardado e revisar as estimativas conforme obtenho cotações e novas informações.")
    )),
    MORADIA("Moradia e rotina", CategoriaTarefa.OUTROS, List.of(
        new Sugestao("opcoes", "Comparar opções de moradia", "Pesquisar localização, transporte, condições de contratação e valores estimados de diferentes acomodações."),
        new Sugestao("despesas", "Estimar despesas da rotina", "Levantar minhas estimativas de alimentação, transporte, internet e outras despesas recorrentes."),
        new Sugestao("contrato", "Conferir condições da acomodação", "Entender o que está incluído, as regras, os pagamentos e as condições de cancelamento antes de confirmar."),
        new Sugestao("chegada", "Planejar os primeiros dias", "Organizar hospedagem inicial, trajeto de chegada e itens essenciais para começar a rotina.")
    )),
    VIAGEM("Viagem e embarque", CategoriaTarefa.OUTROS, List.of(
        new Sugestao("datas", "Planejar as datas da viagem", "Conferir a previsão de início e organizar uma janela de chegada compatível com meus compromissos."),
        new Sugestao("transporte", "Comparar passagens e trajetos", "Pesquisar custos, conexões, bagagem e condições de alteração antes de comprar."),
        new Sugestao("bagagem", "Preparar minha lista de bagagem", "Listar itens pessoais, documentos e materiais de estudo, conferindo as regras da transportadora."),
        new Sugestao("contatos", "Organizar contatos e reservas", "Reunir comprovantes, endereços e contatos importantes para consultar durante a viagem.")
    )),
    IDIOMA("Idioma e adaptação", CategoriaTarefa.IDIOMA, List.of(
        new Sugestao("nivel", "Identificar meu nível de idioma", "Avaliar o que já consigo fazer e definir quais habilidades quero desenvolver."),
        new Sugestao("rotina", "Montar uma rotina de estudos", "Definir tempo semanal e cadastrar uma preparação de idioma no plano."),
        new Sugestao("pratica", "Praticar situações do dia a dia", "Preparar vocabulário e conversação para transporte, moradia, compras e estudos."),
        new Sugestao("cultura", "Conhecer a rotina do destino", "Pesquisar hábitos locais e organizar dúvidas para uma adaptação mais tranquila.")
    )),
    ESTUDOS("Inscrição e estudos", CategoriaTarefa.INSCRICAO, List.of(
        new Sugestao("objetivo", "Definir meu objetivo de estudos", "Escrever o que quero aprender e os critérios que vou usar para comparar oportunidades."),
        new Sugestao("oportunidades", "Pesquisar cursos e oportunidades", "Pesquisar opções mesmo que ainda não estejam no catálogo e registrar as informações em tarefas próprias."),
        new Sugestao("prazos", "Organizar prazos de inscrição", "Consultar cada instituição e anotar os prazos e documentos exigidos por ela."),
        new Sugestao("bolsas", "Pesquisar possibilidades de bolsa", "Consultar editais e instituições, conferindo critérios, prazos e valores diretamente nas fontes responsáveis.")
    ));

    private final String titulo;
    private final CategoriaTarefa categoria;
    private final List<Sugestao> sugestoes;

    TemaChecklist(String titulo, CategoriaTarefa categoria, List<Sugestao> sugestoes) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.sugestoes = sugestoes;
    }
    public String getTitulo() { return titulo; }
    public CategoriaTarefa getCategoria() { return categoria; }
    public List<Sugestao> getSugestoes() { return sugestoes; }
    public record Sugestao(String chave, String titulo, String descricao) { }
    public record Resposta(TemaChecklist tema, String titulo, CategoriaTarefa categoria, int quantidade) {
        public static Resposta de(TemaChecklist tema) {
            return new Resposta(tema, tema.titulo, tema.categoria, tema.sugestoes.size());
        }
    }
}
