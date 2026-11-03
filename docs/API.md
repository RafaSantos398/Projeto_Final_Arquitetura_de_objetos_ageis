# Contratos da API Berxy

Base pública: `https://berxy-api.onrender.com`. Base local Java: `http://localhost:8080`.
No Compose, o navegador usa `http://localhost:3001/api` em testes e porta 3000 no modo usual.

Cadastro/login são públicos. As demais operações exigem `Authorization: Bearer TOKEN`.
USER acessa os próprios dados e consulta catálogos; ADMIN mantém catálogos. O bloqueio
por proprietário ocorre no backend. IDs usados nos exemplos são ilustrativos.

## Rotas reais

Nos caminhos, `{idPlano}` identifica o plano, `{idTarefa}` a tarefa dentro dele,
e `{usuarioId}` a conta. O método do controller é indicado para facilitar a leitura.
GET de lista retorna array; GET de item e alterações retornam objeto, salvo operação
sem corpo indicada pelo controller. Criação usa 201 quando anotada ou ResponseEntity.created;
DELETE de usuário é desativação lógica, DELETE de tarefa é remoção definitiva.

### AreaEstudoController

| Método | Caminho | Operação Java |
|---|---|---|
| GET | `/area-estudo` | `listar` |
| GET | `/area-estudo/{idArea}` | `buscar` |
| POST | `/area-estudo` | `cadastrar` |
| PUT | `/area-estudo/{idArea}` | `atualizar` |

### AuthController

| Método | Caminho | Operação Java |
|---|---|---|
| POST | `/auth/register` | `cadastrar` |
| POST | `/auth/login` | `login` |
| GET | `/auth/me` | `usuarioAtual` |
| POST | `/auth/logout` | `logout` |

### CursoController

| Método | Caminho | Operação Java |
|---|---|---|
| POST | `/cursos/pessoais` | `criarPessoal` |
| PUT | `/cursos/pessoais/{id}` | `editarPessoal` |
| GET | `/cursos` | `list` |
| GET | `/cursos/{id}` | `buscar` |
| POST | `/cursos` | `salvar` |
| PUT | `/cursos/{id}` | `atualizar` |
| PATCH | `/cursos/{id}/ativar` | `ativar` |
| PATCH | `/cursos/{id}/desativar` | `desativar` |
| GET | `/cursos/{id}/modelos-tarefa` | `listarModelos` |
| POST | `/cursos/{id}/modelos-tarefa/{idModelo}` | `associarModelo` |
| DELETE | `/cursos/{id}/modelos-tarefa/{idModelo}` | `desassociarModelo` |

### InstituicaoController

| Método | Caminho | Operação Java |
|---|---|---|
| GET | `/instituicoes` | `list` |
| GET | `/instituicoes/{id}` | `buscar` |
| POST | `/instituicoes` | `salvar` |
| PUT | `/instituicoes/{id}` | `atualizar` |

### ModeloTarefaController

| Método | Caminho | Operação Java |
|---|---|---|
| GET | `/modelos-tarefa` | `list` |
| POST | `/modelos-tarefa` | `salvar` |
| GET | `/modelos-tarefa/{idModelo}` | `buscar` |
| PUT | `/modelos-tarefa/{idModelo}` | `Atualizar` |
| PATCH | `/modelos-tarefa/{idModelo}` | `desativar` |

### PerfilController

| Método | Caminho | Operação Java |
|---|---|---|
| POST | `/users/{usuarioId}/perfil` | `cadastrar` |
| GET | `/users/{usuarioId}/perfil` | `consultar` |
| PUT | `/users/{usuarioId}/perfil` | `atualizar` |

### PlanoIntercambioController

| Método | Caminho | Operação Java |
|---|---|---|
| GET | `/planos-intercambio/{idPlano}/financeiro` | `consultarFinancas` |
| PUT | `/planos-intercambio/{idPlano}/financeiro` | `atualizarFinancas` |
| GET | `/planos-intercambio` | `list` |
| POST | `/planos-intercambio` | `salvar` |
| GET | `/planos-intercambio/{idPlano}` | `buscar` |
| PUT | `/planos-intercambio/{idPlano}` | `atualizar` |
| PATCH | `/planos-intercambio/{idPlano}/curso` | `selecionarCurso` |
| PATCH | `/planos-intercambio/{idPlano}/status` | `alterarStatus` |

### PreparacaoIdiomaController

| Método | Caminho | Operação Java |
|---|---|---|
| GET | `/planos-intercambio/{idPlano}/preparacoes-idioma` | `list` |
| GET | `/planos-intercambio/{idPlano}/preparacoes-idioma/{idPreparacao}` | `buscar` |
| POST | `/planos-intercambio/{idPlano}/preparacoes-idioma` | `salvar` |
| PUT | `/planos-intercambio/{idPlano}/preparacoes-idioma/{idPreparacao}` | `atualizar` |
| PATCH | `/planos-intercambio/{idPlano}/preparacoes-idioma/{idPreparacao}/iniciar` | `iniciar` |
| PATCH | `/planos-intercambio/{idPlano}/preparacoes-idioma/{idPreparacao}/concluir` | `concluir` |

### TarefaPlanoController

| Método | Caminho | Operação Java |
|---|---|---|
| POST | `/planos-intercambio/{idPlano}/tarefas/curso` | `agendarCurso` |
| GET | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/anotacoes` | `anotacoes` |
| PUT | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/anotacoes` | `salvarAnotacoes` |
| PATCH | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/status` | `mover` |
| GET | `/planos-intercambio/{idPlano}/tarefas/temas` | `temas` |
| POST | `/planos-intercambio/{idPlano}/tarefas/gerar-temas` | `gerarTemas` |
| POST | `/planos-intercambio/{idPlano}/tarefas` | `criar` |
| PUT | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}` | `atualizar` |
| DELETE | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}` | `remover` |
| GET | `/planos-intercambio/{idPlano}/tarefas` | `list` |
| GET | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}` | `buscar` |
| POST | `/planos-intercambio/{idPlano}/tarefas/gerar` | `gerar` |
| POST | `/planos-intercambio/{idPlano}/tarefas/gerar-checklist` | `gerarChecklist` |
| PATCH | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/iniciar` | `iniciar` |
| PATCH | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/concluir` | `concluir` |
| PATCH | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/reabrir` | `reabrir` |
| PATCH | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/prazo` | `alterarPrazo` |

### UserController

| Método | Caminho | Operação Java |
|---|---|---|
| POST | `/users` | `create` |
| GET | `/users` | `findAll` |
| GET | `/users/{id}` | `findById` |
| PUT | `/users/{id}` | `update` |
| PATCH | `/users/{id}/password` | `changePassword` |
| PATCH | `/users/{id}/activate` | `activate` |
| DELETE | `/users/{id}` | `deactivate` |

Filtro opcional em `GET /cursos?instituicaoId=1`. `GET /planos-intercambio` lista
os planos do usuário conectado. Não há GET público de catálogos sem Bearer.
A desativação de modelo usa `PATCH /modelos-tarefa/{idModelo}` sem `/desativar`.
Não há endpoint de criação de ADMIN, catálogo de bolsas ou associação de áreas ao plano.

## Exemplos de requests

### Cadastro e login

```json
{"nome":"Ana","email":"ana@example.com","password":"senha-de-exemplo"}
```

Login recebe apenas email/password; retorna `{ "token": "...", "usuario": { ... } }`.
Não enviar `role`: o cadastro público cria USER. Senha nova: mínimo oito caracteres,
máximo 72 bytes UTF-8. A API não retorna senha/hash. O login não exige oito caracteres
para comparar senha existente, mas rejeita texto acima do limite do BCrypt.

### Plano sem curso

```json
{"titulo":"Meu intercâmbio","tipoIntercambio":"CURSO_IDIOMA","status":"RASCUNHO","moeda":"BRL","inicioPrevisto":"2027-07-01","duracaoMeses":6,"dependeBolsa":false}
```

Para USER, usuarioId pode ser omitido e assume o dono do token. Informar outro dono
é proibido. Curso é escolhido por PATCH `/planos-intercambio/{idPlano}/curso`
com `{ "cursoId": 1 }`. Status do plano usa `{ "status": "EM_ANDAMENTO" }`.
Datas usam ISO `yyyy-MM-dd`, não o formato visual brasileiro.

### Temas e tarefa própria

POST `/planos-intercambio/{idPlano}/tarefas/gerar-temas`:

```json
{"temas":["MORADIA","DOCUMENTOS"]}
```

POST ou PUT de conteúdo da tarefa:

```json
{"titulo":"Comparar apartamentos","descricao":"Visitar duas opções","categoria":"OUTROS","prazo":"2027-03-01"}
```

Prazo pode ser null. PUT de conteúdo não muda o status. PATCH `/status` recebe
`{ "status": "EM_ANDAMENTO" }`; PATCH `/prazo` recebe `{ "prazo": null }`.
Gerar tarefa de modelo específico usa `/gerar` com `{ "modeloOrigemId": 1 }`;
`/gerar-checklist` usa os modelos ativos do curso escolhido e não recebe corpo.

### Caderno da tarefa

GET/PUT `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/anotacoes`:

```json
{"texto":"Moradia A: 600 EUR/mês\nVantagem: perto do curso"}
```

PUT aceita até 10.000 caracteres, preservando quebras de linha. Texto vazio apaga o
conteúdo; null não é aceito. Resposta: `{ tarefaId, texto, atualizadoEm }`.
O caderno não aparece dentro de TarefaPlanoResponse da listagem.

### Finanças

PUT `/planos-intercambio/{idPlano}/financeiro`:

```json
{"metaTotal":20000.00,"valorGuardado":5000.00,"aporteMensal":1000.00}
```

Todos os valores são obrigatórios e não negativos. A moeda vem do plano. GET/PUT
retornam os valores, o restante, meses disponíveis, aporte necessário, projeção e
situação. Números JSON usam ponto decimal. Não há cotação automática.

### Preparação de idioma

```json
{"idioma":"en","nivelAlvo":"B2","dataMeta":"2027-04-01","minutosSemanaisPlanejados":180}
```

Não podem existir duas preparações em aberto do mesmo idioma no plano. Editar uma
preparação não reinicia seu status. As ações iniciar/concluir usam PATCH sem corpo.

## Campos exatos dos DTOs

As declarações abaixo foram conferidas no código. As anotações informam validação;
os tipos são Java. Records internos também aparecem com seus nomes para distinguir
payloads de criação, status, prazo e finanças. Validações de domínio adicionais estão
nas entidades e no guia. `de` e `from` são conversões, não rotas.

### AreaEstudo/AreaEstudoRequest.java

```java
public record AreaEstudoRequest(
        String nome,
        String descricao
)
```

### auth/dto/AuthResponse.java

```java
public record AuthResponse(String token, UserResponse usuario)
```

### auth/dto/LoginRequest.java

```java
public record LoginRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank  String password
)
```

### auth/dto/RegisterRequest.java

```java
public record RegisterRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8)  String password
)
```

### Curso/CursoRequest.java

```java
public record CursoRequest(
        Long instituicaoId,
        String nome,
        String descricao,
        TipoIntercambio tipoIntercambio,
        String idioma,
        Integer duracaoMeses,
        Long areaEstudoId,
        String areaEstudoNome
)
```

### Curso/CursoResponse.java

```java
public record CursoResponse(
        Long id,
        Long instituicaoId,
        String instituicaoNome,
        String nome,
        String descricao,
        TipoIntercambio tipoIntercambio,
        String idioma,
        Integer duracaoMeses,
        boolean ativo,
        boolean pessoal,
        java.util.List<EtiquetaCurso> etiquetas,
        java.time.LocalDate inicioPrevisto,
        java.time.LocalDate fimPrevisto,
        String link,
        Long areaEstudoId,
        String areaEstudoNome
)
```

### exception/ErroResponse.java

```java
public record ErroResponse(int status, String detail, Map<String, String> errors)
```

### Insituicao/InstituicaoRequest.java

```java
public record InstituicaoRequest(String nome, String descricao, String siteOficial)
```

### ModeloTarefa/ModeloTarefaRequest.java

```java
public record ModeloTarefaRequest(
        String titulo,
        String descricao,
        CategoriaTarefa categoria
)
```

### perfilIntercambista/PerfilRequest.java

```java
public record PerfilRequest(
        @NotNull @Past LocalDate nascimento,
        @NotEmpty Set<@NotBlank @Size(max = 100) String> nacionalidades,
        @NotBlank @Size(max = 100) String paisResidencia,
        @NotNull List<@NotNull @Valid ProficienciaRequest> proficienciaIdioma,
        @NotNull SituacaoEscolaridade situacaoEscolaridade,
        @NotNull NivelEscolaridade nivelEscolaridade)

public record ProficienciaRequest(
            @NotBlank @Pattern(regexp = "(?i)[a-z]{2}(-[a-z]{2})?") String idioma,
            @NotNull NivelProficiencia nivel)
```

### perfilIntercambista/PerfilResponse.java

```java
public record PerfilResponse(Long id, LocalDate nascimento, Set<String> nacionalidades,
        String paisResidencia, List<PerfilRequest.ProficienciaRequest> proficienciaIdioma,
        SituacaoEscolaridade situacaoEscolaridade, NivelEscolaridade nivelEscolaridade)
```

### PlanoIntercambio/PlanejamentoFinanceiro.java

```java
public record PlanejamentoFinanceiro(
        BigDecimal metaTotal, String moeda, BigDecimal valorGuardado, BigDecimal aporteMensal,
        BigDecimal valorRestante, Long mesesAteInicio, BigDecimal aporteNecessario,
        Long mesesParaMeta, BigDecimal previsaoNoInicio, String situacao)

public record Request(BigDecimal metaTotal, BigDecimal valorGuardado, BigDecimal aporteMensal)
```

### PlanoIntercambio/PlanoIntercambioRequest.java

```java
public record PlanoIntercambioRequest(
        Long usuarioId,
        String titulo,
        TipoIntercambio tipoIntercambio,
        StatusPlano status,
        BigDecimal orcamentoTotal,
        String moeda,
        LocalDate inicioPrevisto,
        Integer duracaoMeses,
        Boolean dependeBolsa
)

public record CursoRequest(Long cursoId)

public record StatusRequest(StatusPlano status)
```

### PlanoIntercambio/PlanoIntercambioResponse.java

```java
public record PlanoIntercambioResponse(
        Long id,
        String titulo,
        TipoIntercambio tipoIntercambio,
        StatusPlano status,
        BigDecimal orcamentoTotal,
        String moeda,
        LocalDate inicioPrevisto,
        Integer duracaoMeses,
        Boolean dependeBolsa,
        boolean cursoSelecionado,
        Long cursoId,
        String cursoNome
)
```

### PreparacaoIdioma/PreparacaoIdiomaRequest.java

```java
public record PreparacaoIdiomaRequest(
        String idioma,
        NivelProficiencia nivelAlvo,
        LocalDate dataMeta,
        Integer minutosSemanaisPlanejados
)
```

### PreparacaoIdioma/PreparacaoIdiomaResponse.java

```java
public record PreparacaoIdiomaResponse(
        Long id,
        Long planoId,
        String idioma,
        NivelProficiencia nivelAlvo,
        LocalDate dataMeta,
        Integer minutosSemanaisPlanejados,
        StatusPreparacao status
)
```

### TarefaPlano/AnotacoesTarefaResponse.java

```java
public record AnotacoesTarefaResponse(Long tarefaId, String texto, Instant atualizadoEm)
```

### TarefaPlano/TarefaPlanoRequest.java

```java
public record TarefaPlanoRequest(Long modeloOrigemId)

public record CursoRequest(Long cursoId)

public record AnotacoesRequest(String texto)

public record StatusRequest(StatusTarefa status)

public record ConteudoRequest(String titulo, String descricao,
            com.berxy.projeto.ModeloTarefa.CategoriaTarefa categoria, LocalDate prazo)

public record TemasRequest(java.util.List<TemaChecklist> temas)

public record PrazoRequest(LocalDate prazo)
```

### TarefaPlano/TarefaPlanoResponse.java

```java
public record TarefaPlanoResponse(
        Long id,
        Long planoId,
        Long modeloOrigemId,
        String titulo,
        String descricao,
        StatusTarefa status,
        LocalDate prazo,
        Instant concluidaEm,
        com.berxy.projeto.ModeloTarefa.CategoriaTarefa categoria,
        TemaChecklist tema,
        boolean personalizada,
        Long cursoApoioId,
        String cursoApoioNome
)
```

### TarefaPlano/TemaChecklist.java

```java
public record Sugestao(String chave, String titulo, String descricao)

public record Resposta(TemaChecklist tema, String titulo, CategoriaTarefa categoria, int quantidade)
```

### user/dto/ChangePasswordRequest.java

```java
public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8)  String newPassword
)
```

### user/dto/CreateUserRequest.java

```java
public record CreateUserRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8)  String password
)
```

### user/dto/UpdateUserRequest.java

```java
public record UpdateUserRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 254) String email
)
```

### user/dto/UserResponse.java

```java
public record UserResponse(
        Long id,
        String nome,
        String email,
        Role role,
        boolean active,
        Instant createdAt,
        Instant updatedAt
)
```

## Erros

| HTTP | Significado | Reação da interface |
|---|---|---|
| 400 | JSON/tipo/campo inválido | Corrigir os dados sem perder o formulário |
| 401 | Sem token válido ou credenciais incorretas | Exibir erro no login ou solicitar novo login |
| 403 | Acesso a outro usuário/plano ou ação administrativa | Informar falta de permissão |
| 404 | Recurso/rota inexistente | Exibir mensagem e permitir retornar |
| 409 | Email reservado, regra conflitante ou restrição única | Recarregar/conferir dados antes de repetir |

O DTO comum retorna `{ "status": 400, "detail": "Dados inválidos", "errors": { ... } }`.
Handlers locais existentes podem retornar `{ "mensagem": "..." }` ou texto.
Erros do filtro de segurança podem usar o corpo padrão do Spring. O cliente HTTP
aceita esses formatos e 204 sem corpo. Não depender de campos RFC como type/instance:
a aplicação usa DTO simples após a revisão de escopo.


## Cursos pessoais

POST /cursos/pessoais cria para o dono do token; PUT /cursos/pessoais/{id} edita somente
o próprio cadastro. GET /cursos lista catálogo e pessoais da conta; GET /cursos/{id}
busca com a mesma verificação. Cursos pessoais não exigem instituição previamente cadastrada.

```json
{"nome":"Inglês para entrevistas","descricao":"Praticar entrevistas","instituicaoNome":"Minha plataforma","idioma":"en","inicioPrevisto":"2027-01-10","fimPrevisto":"2027-03-10","link":"https://example.com/curso","etiquetas":["LINGUISTICO","PROFISSIONAL"]}
```

Nome e idioma são obrigatórios; selecionar ao menos uma etiqueta. Datas, instituição,
descrição e link são opcionais. Etiquetas válidas: FINANCEIRO, LINGUISTICO, ACADEMICO,
PROFISSIONAL, CULTURAL, OUTROS. Repetições são normalizadas. Datas usam ISO; término
não pode anteceder início. O proprietário nunca é recebido do cliente.

Resposta usa CursoResponse com pessoal=true, instituicaoId=null, tipoIntercambio=null,
etiquetas como array e datas/link. O catálogo continua com pessoal=false. Não enviar
esse curso como curso principal pelo PATCH /planos-intercambio/{id}/curso.

Adicionar ao cronograma: POST /planos-intercambio/{idPlano}/tarefas/curso com
`{ "cursoId": 1 }`. Retorna TarefaPlanoResponse e 200, inclusive quando já existe.
A resposta informa cursoApoioId e cursoApoioNome. O término vira prazo inicial;
edições posteriores do curso não sobrescrevem a tarefa. Plano e curso precisam
pertencer ao mesmo usuário. Não é necessário escolher curso principal no plano.

```java
public record CursoPessoalRequest(String nome, String descricao, String instituicaoNome,
        String idioma, LocalDate inicioPrevisto, LocalDate fimPrevisto, String link,
        List<EtiquetaCurso> etiquetas, Long areaEstudoId, String areaEstudoNome) { }
```


## Área de estudo nos cursos

POST/PUT de curso, tanto pessoal quanto catálogo, aceitam areaEstudoId ou
areaEstudoNome. Ambos são opcionais para manter cursos existentes compatíveis;
se ambos vierem preenchidos, retorna 400. ID inexistente retorna 404. Informar nome
reutiliza uma área com o mesmo nome, ignorando caixa, ou cria AreaEstudo dentro da
transação. O nome obedece ao limite de 100 caracteres da entidade existente.

```json
{"nome":"Finanças para intercâmbio","idioma":"pt-BR","etiquetas":["FINANCEIRO"],"areaEstudoNome":"Economia"}
```

Para escolher uma já cadastrada, enviar areaEstudoId e omitir areaEstudoNome.
GET /area-estudo lista as opções. CursoResponse acrescenta areaEstudoId e
areaEstudoNome. Null em ambos no PUT remove a classificação, sem excluir a área.
A área é compartilhada e não expõe o curso pessoal na listagem de outros usuários.
