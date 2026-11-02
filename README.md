# Projeto_Final_Arquitetura_de_objetos_ageis

## Escopo da implementação

As alterações seguem o [material da disciplina](https://ezambomsantana.github.io/arquitetura-objetos-site/):
classes e encapsulamento (Aulas 02–04), REST e injeção de dependências (05–07),
DTOs e erros (08), Spring Security e BCrypt (09), interfaces (11), JPA e
relacionamentos (12–13), JWT e validação (14). O formato de records e a
organização já adotada em `ModeloTarefa` são preservados.

Não introduzir tecnologias ou mecanismos além desse escopo. O login segue o
roteiro da Aula 14, com `JwtService`, `JwtAuthenticationFilter`, `UserDetailsService`
e `PasswordEncoder.matches`. As dependências `jjwt-api`, `jjwt-impl` e
`jjwt-jackson` usam a versão `0.12.3` apresentada na aula. A persistência continua
no PostgreSQL já configurado para o projeto.

## Autenticação e permissões

O login devolve um JWT assinado, com validade de uma hora, como na
[Aula 14](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-14/material/).
Nas próximas requisições, envie `Authorization: Bearer SEU_TOKEN`.
O filtro confere assinatura e expiração e busca o usuário ativo no banco,
seguindo a organização das [Aulas 09](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-09/material/)
e [13](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-13/material/).

| Método | Caminho | Resultado |
| --- | --- | --- |
| POST | `/auth/register` | Cadastra usuário `USER` (201); não faz login automático |
| POST | `/auth/login` | Valida e-mail/senha e devolve `{"token":"...","usuario":{...}}` |
| GET | `/auth/me` | Consulta o usuário identificado pelo Bearer |
| POST | `/auth/logout` | Responde 204; o frontend deve descartar o token |

Cadastro: `{"nome":"Ana","email":"ana@example.com","password":"senha-segura"}`.
Login: `{"email":"ana@example.com","password":"senha-segura"}`.
`POST /users` continua disponível como alternativa de cadastro público.
O cadastro não permite escolher `ADMIN`. As respostas e o JWT não incluem
senha ou hash. A API não usa cookie para autenticar nem exige token CSRF;
a configuração desabilita CSRF conforme o roteiro da Aula 14.

```javascript
const resposta = await fetch(`${API}/auth/login`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password })
});
if (!resposta.ok) throw new Error('Não foi possível entrar');
const { token, usuario } = await resposta.json();
const planos = await fetch(`${API}/planos-intercambio`, {
  headers: { Authorization: `Bearer ${token}` }
});
```

| Recurso | USER | ADMIN |
| --- | --- | --- |
| Consultar cursos, instituições, áreas e modelos | Após login | Permitido |
| Manter catálogos e associar modelos a cursos | Bloqueado | Permitido |
| Consultar/alterar cadastro e perfil | Somente os próprios | Qualquer usuário |
| Listar usuários e reativar contas | Bloqueado | Permitido |
| Consultar/alterar planos, tarefas e preparação de idioma | Somente os próprios | Qualquer plano |

As permissões são verificações simples de usuário, dono do plano e papel,
feitas no filtro e nos serviços auxiliares. `GET /planos-intercambio` retorna
somente os planos do usuário conectado. No cadastro, `usuarioId` pode ser
omitido para assumir o dono do token; um `USER` não pode informar outra pessoa.
Sem identificação válida, a API retorna 401; sem permissão, retorna 403.
Credenciais incorretas e conta desativada recebem o mesmo erro de login.

O JWT da aula não usa uma lista de revogação: fazer logout significa descartar
o token no cliente, e trocar a senha não cancela tokens já emitidos; eles vencem
em uma hora. Desativar a conta impede seu uso porque o filtro consulta o banco.
Alterar o e-mail exige novo login, pois o e-mail é o `subject` do JWT.

Em produção, configure `JWT_SECRET` com uma chave privada de pelo menos
32 bytes em UTF-8, fora do repositório. A chave padrão de `dev` é somente um
exemplo local. Os scripts de validação geram uma chave temporária por execução.
Nenhuma tabela nova é necessária. A integração do frontend ainda precisa ser
validada. A integração com o navegador usa CORS do próprio Spring, com origens
explícitas em FRONTEND_ORIGINS e sem autenticação por cookie.

O primeiro administrador deve ser promovido pelo responsável pelo banco,
depois de cadastrar a conta, por exemplo:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'email-do-responsavel@example.com';
```

Não há senha padrão nem endpoint público para promover administradores.
Nenhuma conta do Render foi promovida automaticamente.

## Instituição

A funcionalidade segue a organização de `ModeloTarefa`: entidade com validações,
request (`record`), repository, service e controller. A injeção de dependências
é por construtor. Os services delimitam as transações.

O pacote `Insituicao` e a tabela de mesmo nome foram mantidos para preservar a
estrutura existente.

### Endpoints

| Método | Caminho | Resultado |
| --- | --- | --- |
| GET | `/instituicoes` | Lista as instituições |
| GET | `/instituicoes/{id}` | Consulta uma instituição |
| POST | `/instituicoes` | Cadastra uma instituição (201) |
| PUT | `/instituicoes/{id}` | Atualiza uma instituição |

Validações de domínio retornam 400 e registros inexistentes retornam 404.

Exemplo de corpo para cadastro ou atualização:

```json
{
  "nome": "Universidade Exemplo",
  "descricao": "Instituição com cursos de idiomas e graduação",
  "siteOficial": "https://example.org"
}
```

Nome é obrigatório (até 100 caracteres). Descrição e site são opcionais (até
500 caracteres); quando informado, o site deve ser uma URL HTTP ou HTTPS.
Os campos são validados antes de modificar a entidade.

### Execução e integração

O `pom.xml` exige JDK 26. Com Java e Maven configurados, execute em `projeto`:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

A aplicação possui um perfil `dev` com H2 em memória e um perfil `prod` para
PostgreSQL. A compilação e os testes integrados foram validados no perfil `dev`.
A persistência em PostgreSQL foi validada conforme os resultados ao final
deste documento. A manutenção de instituições exige um administrador.

Os três testes de domínio de instituição verificam normalização de dados,
campos opcionais, validação do nome e preservação dos dados após uma atualização
inválida. Eles também passaram na execução da suíte completa com Spring e H2.

O enunciado limita o uso de IA no back-end a suporte. O grupo deve revisar e
adequar esta implementação às regras da disciplina, compreendendo as decisões
antes de incorporá-la à entrega.

## Organização de modelos de tarefa, planos e tarefas

As funcionalidades de `ModeloTarefa`, `PlanoIntercambio` e `TarefaPlano` seguem
a organização abaixo. Os services usam injeção por atributo com `@Autowired`
e operações de `JpaRepository`, incluindo consultas por plano e modelo. Plano
e tarefa usam transações Spring nos services, como Curso e Preparação, para
acessar as relações JPA com `open-in-view=false` e persistir cada operação completa.

| Classe | Responsabilidade |
| --- | --- |
| Entidade | Define os dados, relacionamentos JPA e regras de domínio |
| Request | Recebe os dados enviados à API por meio de um `record` |
| Repository | Acessa os dados persistidos usando `JpaRepository` |
| Service | Busca os registros, chama os métodos da entidade e salva as alterações |
| Controller | Define as rotas HTTP e encaminha as requisições ao service |
| Response | Devolve os dados de planos e tarefas por meio de um `record` |

`ModeloTarefa` retorna a própria entidade. Os records de resposta de plano e
tarefa evitam referências circulares no JSON e a exposição do objeto `User`.
Os arquivos estão em `projeto/src/main/java/com/berxy/projeto/`, nos pacotes
`ModeloTarefa`, `PlanoIntercambio` e `TarefaPlano`.

## ModeloTarefa

Representa um modelo reutilizável, como "Providenciar passaporte", usado para
criar tarefas de um plano. O pacote contém a entidade, `CategoriaTarefa`,
`ModeloTarefaRequest`, `ModeloTarefaRepository`, `ModeloTarefaService` e
`ModeloTarefaController`.

### Regras

- Título e descrição são obrigatórios, com limites de 200 e 400 caracteres.
- Os textos têm os espaços das extremidades removidos antes de serem armazenados.
- A categoria é obrigatória: `DOCUMENTACAO`, `IDIOMA`, `FINANCEIRO`,
  `INSCRICAO` ou `OUTROS`.
- O cadastro cria o modelo com `ativo = true`.
- Desativar altera `ativo` para `false`, preservando o modelo e as tarefas existentes.
- A listagem inclui modelos ativos e inativos.
- Alterar o modelo não modifica o título e a descrição das tarefas já geradas.

### Endpoints

| Método | Caminho | Resultado |
| --- | --- | --- |
| GET | `/modelos-tarefa` | Lista os modelos (200) |
| GET | `/modelos-tarefa/{idModelo}` | Consulta um modelo (200) |
| POST | `/modelos-tarefa` | Cadastra um modelo (201) |
| PUT | `/modelos-tarefa/{idModelo}` | Atualiza título, descrição e categoria (200) |
| PATCH | `/modelos-tarefa/{idModelo}` | Desativa o modelo (200), sem corpo na requisição |

Exemplo de corpo para cadastro ou atualização:

```json
{
  "titulo": "Providenciar passaporte",
  "descricao": "Separar os documentos necessários para solicitar o passaporte.",
  "categoria": "DOCUMENTACAO"
}
```

## PlanoIntercambio

Representa o planejamento de intercâmbio de um usuário. A entidade possui
relacionamentos com usuário, curso escolhido, áreas de interesse, preparações
de idioma e tarefas. O pacote contém entidade, request, response, repository,
service e controller.

`PlanoUsuarioRepository` e `PlanoCursoRepository` são repositories auxiliares
que também estendem `JpaRepository`. Eles consultam usuário e curso por ID
sem modificar os arquivos dessas funcionalidades.

### Dados e regras

| Campo do request | Uso |
| --- | --- |
| `usuarioId` | ID de um usuário existente; utilizado apenas no cadastro |
| `titulo` | Obrigatório, com até 100 caracteres após remover espaços das extremidades |
| `tipoIntercambio` | Obrigatório; depende dos valores de `TipoIntercambio` |
| `status` | Obrigatório; depende dos valores de `StatusPlano` |
| `orcamentoTotal` | Opcional; não negativo, com até 12 dígitos inteiros e duas casas decimais |
| `moeda` | Opcional; código de três letras, normalizado para maiúsculas |
| `inicioPrevisto` | Opcional; data no formato `AAAA-MM-DD` |
| `duracaoMeses` | Opcional; quando informado, deve ser maior que zero |
| `dependeBolsa` | Opcional; aceita `true`, `false` ou `null` |

O plano pode ser cadastrado sem curso e sem os dados opcionais de planejamento.
O tipo e o status são informados explicitamente no cadastro. `TipoIntercambio`
aceita `GRADUACAO`, `POS_GRADUACAO`, `CURSO_IDIOMA`, `ENSINO_MEDIO` e `TRABALHO`.
`StatusPlano` aceita `RASCUNHO`, `EM_ANDAMENTO`, `CONCLUIDO` e `CANCELADO`.

A atualização mantém o usuário original. Os campos opcionais enviados como
`null` são removidos do planejamento. Um curso deve ser selecionado antes de
gerar tarefas, e a troca de curso fica bloqueada após a criação dessas tarefas.

As áreas de interesse podem ser adicionadas ou removidas pelos métodos da
entidade; o controller atual não expõe rotas para editá-las ou para gerenciar
as preparações de idioma.

### Endpoints

| Método | Caminho | Resultado |
| --- | --- | --- |
| GET | `/planos-intercambio` | Lista os planos (200) |
| GET | `/planos-intercambio/{idPlano}` | Consulta um plano (200) |
| POST | `/planos-intercambio` | Cadastra um plano vinculado a `usuarioId` (201) |
| PUT | `/planos-intercambio/{idPlano}` | Atualiza os dados do plano, mantendo o usuário (200) |
| PATCH | `/planos-intercambio/{idPlano}/curso` | Seleciona o curso informado em `cursoId` (200) |
| PATCH | `/planos-intercambio/{idPlano}/status` | Altera o status informado em `status` (200) |

Exemplo de corpo para selecionar um curso existente:

```json
{
  "cursoId": 1
}
```

`PlanoIntercambioResponse` devolve os dados do planejamento e o indicador
`cursoSelecionado`, `cursoId` e `cursoNome`. Os dois últimos são nulos quando
não há curso escolhido. O retorno não inclui os objetos relacionados nem as listas
de tarefas e preparações de idioma.

## TarefaPlano

Representa uma tarefa de um plano, criada a partir de um `ModeloTarefa`.
O pacote contém entidade, request, response, repository, service e controller.
O service utiliza `PlanoIntercambioService` para buscar o plano e
`ModeloTarefaRepository` para buscar o modelo de origem.

### Regras

- A criação exige um plano com curso selecionado e um modelo ativo.
- Título e descrição são copiados do modelo; a tarefa começa `PENDENTE` e sem prazo.
- A criação mantém o vínculo entre a tarefa e a lista de tarefas do plano em memória.
- Repetir a geração para o mesmo plano e modelo retorna a tarefa existente,
  preservando prazo, status e data de conclusão, inclusive se o modelo foi desativado.
- O mapeamento da tabela define uma restrição única para o par `plano_id` e `modelo_origem_id`.
- Somente uma tarefa `PENDENTE` pode ser iniciada, passando para `EM_ANDAMENTO`.
- Uma tarefa pendente ou em andamento pode ser concluída; a conclusão registra
  `concluidaEm`. Concluir novamente preserva essa data.
- Reabrir exige uma tarefa `CONCLUIDA`, retorna seu status para `PENDENTE`
  e limpa a data de conclusão.
- O prazo pode ser definido, alterado ou removido com `null`.
- A busca e a alteração conferem se a tarefa pertence ao plano informado na rota.

### Endpoints

Todas as rotas abaixo retornam 200 quando a operação é bem-sucedida.

| Método | Caminho | Resultado |
| --- | --- | --- |
| GET | `/planos-intercambio/{idPlano}/tarefas` | Lista as tarefas do plano |
| GET | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}` | Consulta uma tarefa do plano |
| POST | `/planos-intercambio/{idPlano}/tarefas/gerar` | Cria ou recupera a tarefa do modelo informado |
| POST | `/planos-intercambio/{idPlano}/tarefas/gerar-checklist` | Gera o checklist com os modelos ativos do curso escolhido, sem corpo na requisição |
| PATCH | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/iniciar` | Inicia a tarefa |
| PATCH | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/concluir` | Conclui a tarefa |
| PATCH | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/reabrir` | Reabre a tarefa |
| PATCH | `/planos-intercambio/{idPlano}/tarefas/{idTarefa}/prazo` | Altera ou remove o prazo |

Exemplo de corpo para gerar uma tarefa:

```json
{
  "modeloOrigemId": 1
}
```

O modelo informado nessa rota deve estar ativo e associado ao curso escolhido.
Se a tarefa já existe, o retorno preserva seus dados mesmo que o modelo tenha
sido desativado ou desassociado depois da criação.

Para gerar automaticamente o checklist completo, use:

```http
POST /planos-intercambio/{idPlano}/tarefas/gerar-checklist
```

Essa rota não recebe corpo e retorna uma lista de `TarefaPlanoResponse`. Ela
consulta `CursoService.listarModelosAtivos`, cria somente as tarefas ausentes e
mantém as tarefas anteriores, seus prazos e suas conclusões. Modelos desativados
não geram novas tarefas. Um curso sem modelos retorna o checklist existente,
que pode ser uma lista vazia. A ausência de curso selecionado retorna 409.

Exemplo de corpo para definir o prazo:

```json
{
  "prazo": "2026-11-20"
}
```

Para remover o prazo, envie `{"prazo": null}`. As rotas de iniciar, concluir e
reabrir não recebem corpo na requisição. Os IDs dos exemplos devem ser
substituídos pelos registros cadastrados no banco.

`TarefaPlanoResponse` devolve `id`, `planoId`, `modeloOrigemId`, `titulo`,
`descricao`, `status`, `prazo` e `concluidaEm`.

## Preparação de idioma

Foram concluídos `PreparacaoIdioma`, `NivelProficiencia` e `StatusPreparacao`,
compatíveis com o request, response, repository, service e controller existentes.

A preparação exige um plano, idioma, nível-alvo, data-meta e minutos semanais
maiores que zero. O idioma aceita códigos como `en` e `pt-BR`, normalizados antes
de salvar. Os níveis são `A1`, `A2`, `B1`, `B2`, `C1` e `C2`.

O status inicial é `NAO_INICIADA`. Iniciar altera para `EM_ANDAMENTO`; concluir
altera para `CONCLUIDA` e pode ser repetido. Atualizar os dados preserva o status.
Não é permitido manter duas preparações em aberto do mesmo idioma para um plano.
Uma preparação concluída permite cadastrar uma nova preparação daquele idioma.

| Método | Caminho | Resultado |
| --- | --- | --- |
| GET | `/planos-intercambio/{idPlano}/preparacoes-idioma` | Lista as preparações (200) |
| GET | `/planos-intercambio/{idPlano}/preparacoes-idioma/{idPreparacao}` | Consulta uma preparação daquele plano (200) |
| POST | `/planos-intercambio/{idPlano}/preparacoes-idioma` | Cadastra uma preparação (201) |
| PUT | `/planos-intercambio/{idPlano}/preparacoes-idioma/{idPreparacao}` | Atualiza os dados, preservando o progresso (200) |
| PATCH | `/planos-intercambio/{idPlano}/preparacoes-idioma/{idPreparacao}/iniciar` | Inicia a preparação (200) |
| PATCH | `/planos-intercambio/{idPlano}/preparacoes-idioma/{idPreparacao}/concluir` | Conclui a preparação (200) |

Exemplo para cadastro ou atualização:

```json
{
  "idioma": "en",
  "nivelAlvo": "B2",
  "dataMeta": "2027-02-01",
  "minutosSemanaisPlanejados": 120
}
```

## Perfil do intercambista — integração da branch de João

Os arquivos de perfil foram incorporados da branch
`origin/feat/joao-perfil-intercambista`, commit `4d32d31`, preservando a implementação
de João e os testes existentes. Essa incorporação está na cópia de trabalho;
não foi efetuado commit, push ou merge no GitHub por esta atualização.

O perfil compartilha o ID de um usuário ativo por `@MapsId`. Cada usuário pode
ter um perfil. A resposta usa um record com os dados do perfil, sem o objeto `User`.

| Método | Caminho | Resultado |
| --- | --- | --- |
| POST | `/users/{usuarioId}/perfil` | Cadastra o perfil (201); duplicado retorna 409 |
| GET | `/users/{usuarioId}/perfil` | Consulta o perfil (200) |
| PUT | `/users/{usuarioId}/perfil` | Substitui os dados do perfil (200) |

Usuário ou perfil inexistente retorna 404; dados inválidos retornam 400.
Nascimento deve estar no passado, nacionalidades devem ter ao menos um valor,
país e nacionalidades aceitam até 100 caracteres e escolaridade é obrigatória.
Proficiências podem ser uma lista vazia; cada item exige idioma e nível, sem
idiomas repetidos. O nível usa o enum `NivelProficiencia` compartilhado.

Exemplo para cadastro ou atualização:

```json
{
  "nascimento": "2000-01-01",
  "nacionalidades": ["Brasileira"],
  "paisResidencia": "Brasil",
  "proficienciaIdioma": [{"idioma": "en", "nivel": "B1"}],
  "situacaoEscolaridade": "CURSANDO",
  "nivelEscolaridade": "GRADUACAO"
}
```

As nacionalidades são persistidas em `perfil_nacionalidades`, coluna
`nacionalidade`, conforme o mapeamento da branch de João. Um banco com dados
na antiga coleção precisa migrá-los antes de utilizar esse mapeamento.

## Validação integrada — 02/11/2026

Comando executado na pasta do `pom.xml`, com JDK 26:

```powershell
.\mvnw.cmd test -Dspring.profiles.active=dev
```

Resultado após integrar o frontend: **58 testes executados, sem falhas, erros ou testes ignorados**, com
Spring Boot e H2 reais, sem substituir entidades ou repositories:

| Suíte | Testes |
| --- | --- |
| `UserControllerTest` | 21 |
| `PerfilControllerTest` | 3 |
| `InstituicaoTest` | 3 |
| `ProjetoApplicationTests` | 1 |
| `FluxosPlanejamentoTest` | 16 |
| `AuthControllerTest` | 14 |

A versão com JWT passou em **55 testes no PostgreSQL 18.6 do Render**, no perfil
`prod`, com `ddl-auto=validate`. A execução usou um esquema temporário isolado,
removido ao final; as contagens das 13 tabelas de `public` permaneceram iguais.
Isso valida os fluxos automatizados no banco real, sem comprovar implantação
do backend ou integração com o frontend.

Os 14 testes de autenticação cobrem credenciais reais, cadastro, emissão e
validação de JWT, tokens adulterados/expirados, logout no cliente, isolamento
por dono, papel de administrador, conta desativada e troca de senha. Os testes
anteriores continuam com os filtros ativos, usando JWT de um administrador
com dados de teste. O JAR com JWT passou por 19 chamadas HTTP reais de cadastro,
login, Bearer, permissões, planos e consultas de tarefas e preparação pelo dono.

Os novos testes cobrem preparação de idioma, persistência e escopo do plano,
perfil fora de uma transação do teste, checklist por curso, modelos inativos ou
de outros cursos, repetição sem duplicação, preservação dos textos e da conclusão,
prazo, transições de tarefa e bloqueio de troca de curso após gerar tarefas.
As chamadas desses testes usam o limite transacional dos services, mantendo
`spring.jpa.open-in-view=false`.

A auditoria ampliada também cobre os cadastros e consultas de modelos, áreas,
instituições, cursos e planos, associação de modelos ao curso e rejeição de
usuário desativado ao criar plano. Foram corrigidos os retornos 400/404 de
modelos e áreas, a aceitação indevida de idiomas como `pt-BR-extra` e a criação
de plano para usuário desativado.

O empacotamento Maven gerou o JAR executável, que iniciou no perfil `dev`.
A verificação HTTP local usa H2 temporário; não comprova deploy nem integração
com as telas do frontend.

## Integrações ainda pendentes

O esquema inicial do PostgreSQL está em
`projeto/database/schema-postgresql.sql`, gerado a partir das entidades JPA.
O inicializador `projeto/scripts/inicializar-postgres.ps1` solicita a senha
localmente, cria as tabelas somente em um banco vazio e valida o esquema com
o Spring no perfil `prod`. As instruções estão em
[projeto/database/README.md](projeto/database/README.md).

Em 02/11/2026, o inicializador concluiu no PostgreSQL do Render: 13 tabelas
presentes e teste de carregamento do contexto aprovado no perfil `prod`, com
`ddl-auto=validate`. A senha foi informada na janela local e não foi gravada
no projeto. Essa execução não alterou a senha do banco.

Os endpoints descritos refletem o código atual. Ainda faltam os seguintes pontos
para validar a entrega completa:

- Validar o fluxo JWT/Bearer nas URLs públicas após a implantação.
- Preparar Docker e validar a implantação do frontend e do backend.
- Validar solicitações concorrentes de geração. A geração sequencial repetida
  foi testada; o mapeamento continua definindo unicidade de plano e modelo no banco.
- Se áreas de interesse fizerem parte do fluxo final, expor sua associação ao
  plano: o relacionamento existe na entidade, mas ainda não há rota para isso.

Para testar PostgreSQL com isolamento, use `projeto/scripts/testar-postgres.ps1`
conforme o README de `database`. Ele cria um esquema temporário, aplica o SQL
nesse esquema, executa a suíte com `ddl-auto=validate` e remove o esquema ao
terminar. Também compara as contagens das tabelas de `public` antes e depois.
O perfil `dev` usa H2 em memória e `create-drop`; seus dados não são uma base
persistente de produção.

## Integração com o frontend — 02/11/2026

O frontend foi integrado em React com JavaScript e CSS, com login JWT, planos,
curso, checklist, perfil, preparação de idioma e manutenção dos catálogos.
A resposta do plano agora inclui cursoId e cursoNome, recuperáveis após novo
login ou reabertura. Essa adição não exige alteração nas tabelas.

FRONTEND_ORIGINS configura origens explícitas separadas por vírgula. O padrão
local permite http://localhost:5173, http://127.0.0.1:5173 e as mesmas origens
na porta 4173. Em produção, informar a origem real do frontend. CORS permite
Authorization, Content-Type e Accept; o login continua usando Bearer, sem cookies.

A rodada atual passou em 58 testes no H2, incluindo preflight, recusa de origem
não configurada e recuperação do curso escolhido. O frontend passou em 8 testes
do cliente HTTP e no build de produção. Os fluxos foram exercitados no navegador
com o JAR real e H2 temporário, sem escrever os dados de interface no Render.
O resultado anterior de 55 testes no PostgreSQL continua registrado como tal;
a rodada atual de 58 não foi executada nesse banco.

A próxima etapa é a implantação pública. O frontend tem VITE_API_URL público;
o backend em produção precisa de DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET
e FRONTEND_ORIGINS configurados fora do repositório.

## Checklists por tema e finanças — 02/11/2026

O plano agora funciona sem curso cadastrado: seis temas oferecem 24 sugestões
(documentação, finanças, moradia, viagem, idioma e estudos). O usuário também
pode criar, editar e remover tarefas próprias. Adicionar novamente um tema
preserva conteúdo editado, prazos e conclusões e não duplica tarefas. Uma
sugestão removida pode voltar se o tema for adicionado novamente.

Novos endpoints, sempre restritos ao dono do plano ou ADMIN:

- GET /planos-intercambio/{id}/tarefas/temas: lista de temas e quantidades.
- POST /planos-intercambio/{id}/tarefas/gerar-temas: {"temas":["DOCUMENTOS","FINANCAS"]}.
- POST /planos-intercambio/{id}/tarefas: {"titulo":"Minha tarefa","descricao":"","categoria":"OUTROS","prazo":null}.
- PUT /planos-intercambio/{id}/tarefas/{idTarefa}: atualiza esses mesmos campos sem reiniciar o status.
- DELETE /planos-intercambio/{id}/tarefas/{idTarefa}: remove a tarefa daquele plano.
- GET/PUT /planos-intercambio/{id}/financeiro: o PUT recebe metaTotal, valorGuardado e aporteMensal.

A meta financeira atualiza orcamentoTotal. O retorno inclui o valor restante,
meses completos até inicioPrevisto, aporte necessário arredondado para cima,
meses necessários no ritmo atual, previsão no início e situação. Não há
cotação automática, juros ou recomendações de investimento. Os valores são
estimativas informadas pelo usuário, na moeda do plano.

Os endpoints anteriores de geração por modelo/curso continuam disponíveis.
Somente tarefas específicas do curso bloqueiam sua troca; tarefas próprias
e por tema permitem selecionar um curso depois. As respostas de tarefa
incluem categoria, tema e personalizada; modeloOrigemId pode ser null.

Banco existente: aplicar projeto/database/migrations/20261102-checklists-financas.sql
antes de publicar o novo backend. A migração é aditiva e foi reaplicada no
PostgreSQL 18 local, preservando uma tarefa antiga concluída. O novo contexto
Spring também validou o esquema migrado. Para execução manual, há o script
projeto/scripts/atualizar-checklists-postgres.ps1, com senha solicitada pelo psql.

Validação: 67 testes aprovados no H2 e 67 no PostgreSQL 18 local isolado;
build Docker com 67 testes do backend e 8 do frontend aprovado. Navegador:
geração sem curso, repetição sem duplicação, conclusão, tarefa própria,
edição/remoção e finanças persistidas, incluindo aporte zero.

Na publicação autorizada, a migração do PostgreSQL do Render foi aplicada em
transação com comparação dos dados anteriores de planos e tarefas: preservados.

## Execução com Docker

Para a publicação do backend, consulte [DEPLOY-RENDER.md](DEPLOY-RENDER.md).
O arquivo render.yaml prepara um serviço Docker Free e reutiliza o banco existente;
a configuração pode ser usada como referência para novas instalações.

Backend publicado em 02/11/2026: https://berxy-api.onrender.com, Docker Free,
Oregon, commit 256ba59 da branch feat/tarefasplano. Os 58 testes passaram no
build remoto e sete verificações HTTP públicas de proteção, validação e CORS
passaram sem cadastrar dados no PostgreSQL. O frontend também foi publicado:
https://berxy.vercel.app, com VITE_API_URL apontando para essa API, sem /api.
FRONTEND_ORIGINS foi atualizado no Render; CORS e resposta de login inválido
foram conferidos pelo domínio público.

O Dockerfile está em projeto/Dockerfile, junto de pom.xml. O build executa
os 58 testes no perfil dev e produz uma imagem Java 26 JRE, com usuário sem
privilégios. O padrão da imagem é prod; server.port respeita a variável PORT.

O frontend contém compose.yaml, compose.local.yaml e scripts/iniciar-docker.ps1.
O guia DOCKER.md naquele repositório explica como iniciar o conjunto. Nginx
serve React em http://localhost:3000 e encaminha /api/ ao backend pela rede
interna do Docker. O PostgreSQL existente permanece no Render via SSL;
o perfil prod continua validando o esquema, sem recriar tabelas.

Builds Linux aprovados com 58 testes do backend e 8 do frontend. Outros 20
checks HTTP pelo proxy passaram em H2 descartável. O conjunto prod foi iniciado
com sucesso contra o PostgreSQL, com validação de esquema e containers saudáveis.
Não foram inseridos dados de teste no banco de produção nesta rodada Docker.
