# Diagramas do Berxy

Diagramas conferidos com o código em 02/11/2026. As fontes `.mmd` são editáveis;
o GitHub renderiza os blocos Mermaid abaixo. A versão SVG do domínio pode ser
aberta diretamente no navegador e usada na apresentação.

## Classes e relações

[Diagrama em SVG](diagramas/dominio.svg) · [Fonte Mermaid](diagramas/dominio.mmd)

```mermaid
classDiagram
    direction TB
    class User {
        Long id
        String email
        String passwordHash
        Role role
        boolean active
    }
    class PerfilINtercambista {
        Long id
        LocalDate nascimento
        String paisResidencia
        atualizar(dados)
    }
    class ProficienciaIdioma {
        <<value object>>
        String idioma
        NivelProficiencia nivel
    }
    class Instituicao {
        Long id
        String nome
        String siteOficial
    }
    class AreaEstudo {
        Long id
        String nome
        String descricao
    }
    class Curso {
        Long id
        TipoIntercambio tipoIntercambio
        String idioma
        boolean ativo
        adicionarModeloTarefa(modelo)
    }
    class ModeloTarefa {
        Long id
        String titulo
        CategoriaTarefa categoria
        boolean ativo
    }
    class PlanoIntercambio {
        Long id
        StatusPlano status
        BigDecimal orcamentoTotal
        BigDecimal valorGuardado
        BigDecimal aporteMensal
        selecionarCurso(curso)
    }
    class TarefaPlano {
        Long id
        String titulo
        StatusTarefa status
        LocalDate prazo
        String anotacoes
        Instant concluidaEm
        moverPara(status)
    }
    class PreparacaoIdioma {
        Long id
        String idioma
        NivelProficiencia nivelAlvo
        StatusPreparacao status
        Integer minutosSemanaisPlanejados
    }
    User "1" -- "0..1" PerfilINtercambista : perfil
    PerfilINtercambista "1" *-- "0..*" ProficienciaIdioma : valores do perfil
    User "1" -- "0..*" PlanoIntercambio : dono
    Instituicao "1" -- "0..*" Curso : oferece
    Curso "0..*" -- "0..*" ModeloTarefa : modelos associados
    Curso "0..1" -- "0..*" PlanoIntercambio : escolhido
    PlanoIntercambio "0..*" -- "0..*" AreaEstudo : interesses no dominio
    PlanoIntercambio "1" -- "0..*" TarefaPlano : checklist
    ModeloTarefa "0..1" -- "0..*" TarefaPlano : origem opcional
    PlanoIntercambio "1" -- "0..*" PreparacaoIdioma : metas
```

A multiplicidade junto à classe indica quantas instâncias dela podem se relacionar
com uma instância da outra ponta. Curso é opcional no plano; modelo é opcional na
tarefa. ProficienciaIdioma é um objeto de valor pertencente ao perfil, sem identidade
própria. Os demais vínculos não significam automaticamente cascade remove. A associação
de áreas está no domínio, mas ainda não possui rota para manutenção no plano.

## Camadas de uma operação

```mermaid
flowchart LR
    React[React na Vercel] -->|HTTPS JSON e Bearer| Filter[Filtro JWT e AcessoService]
    Filter --> Controller[Controller e DTOs]
    Controller --> Service[Service e transacao]
    Service --> Domain[Entidade e regras]
    Service --> Repo[JpaRepository]
    Repo --> DB[(PostgreSQL no Render)]
    Service --> Response[DTO de resposta]
    Response --> React
```

Repositories são interfaces implementadas pelo Spring. O service consulta as relações
na transação; DTOs de saída evitam ciclos do grafo JPA. CORS é configurado no backend.

## Sequência de salvamento de anotações

```mermaid
sequenceDiagram
    actor Pessoa
    participant UI as React
    participant F as Filtro JWT
    participant A as AcessoService
    participant C as TarefaPlanoController
    participant S as TarefaPlanoService
    participant T as TarefaPlano
    participant DB as PostgreSQL
    Pessoa->>UI: Abrir caderno e escrever
    UI->>F: PUT anotacoes com Bearer e texto
    F->>F: Conferir assinatura e expiracao
    F->>A: Verificar papel e dono do plano
    A-->>F: Autorizar ou rejeitar
    F->>C: Encaminhar requisicao autorizada
    C->>S: atualizarAnotacoes(planoId, tarefaId, texto)
    S->>DB: Consultar tarefa dentro do plano
    S->>T: atualizarAnotacoes(texto)
    T->>T: Validar limite e atualizar horario
    S->>DB: Salvar na transacao
    S-->>C: Tarefa persistida
    C-->>UI: tarefaId, texto, atualizadoEm
    UI-->>Pessoa: Confirmacao de salvamento
```

Este é o fluxo de sucesso. JWT inválido termina em 401; falta de permissão em 403;
tarefa inexistente no plano em 404; texto inválido em 400. Nenhuma dessas falhas
precisa alterar o status da tarefa ou apagar o texto que a interface ainda mantém.

## Implantação pública e desenvolvimento

```mermaid
flowchart TB
    subgraph Publico
        Navegador[Navegador do usuario] -->|HTTPS| Vercel[Vercel serve React]
        Navegador -->|fetch HTTPS com JWT| Render[Render executa Docker Spring]
        Render -->|JDBC com SSL| PG[(PostgreSQL existente)]
    end
    subgraph Desenvolvimento
        Local[Navegador local] -->|localhost 3001| Nginx[Nginx serve React]
        Nginx -->|proxy /api| API[Spring dev no Compose]
        API --> H2[(H2 descartavel)]
    end
```

O Compose padrão na porta 3000 também pode acessar PostgreSQL com credenciais do
ambiente. O diagrama de desenvolvimento representa especificamente o modo `-Local`
usado nas verificações, que não grava no banco publicado. A Vercel não executa o
Nginx do Compose e não usa o prefixo `/api` na URL pública do backend.
