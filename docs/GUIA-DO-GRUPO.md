# Guia do grupo sobre o Berxy

Atualizado em 03/11/2026. Este manual explica o sistema que está implementado,
como as classes colaboram e o que cada integrante precisa compreender para manter
e apresentar o projeto. Os nomes dos arquivos correspondem ao repositório real.

## 1. O problema resolvido

Planejar intercâmbio exige reunir documentos, opções de curso e moradia, dinheiro,
prazos e preparação de idioma. O Berxy organiza essas etapas em planos pessoais.
O catálogo ajuda quando há cursos cadastrados, mas não bloqueia a organização:
checklists por tema, tarefas próprias, finanças e anotações funcionam sem curso.

## 2. Onde cada parte executa

| Parte | Responsabilidade | Execução |
|---|---|---|
| React | Mostrar formulários, estado visual e resultados | Navegador; arquivos publicados na Vercel |
| API Spring | Autenticar, autorizar, validar e aplicar regras | Container Docker no Render |
| PostgreSQL | Guardar usuários, planos, tarefas e demais dados | Banco existente no Render |
| H2 dev | Dados descartáveis de desenvolvimento/testes | Processo/container local isolado |
| Nginx local | Servir React e encaminhar `/api` ao backend | Somente no Compose local |

O site público funciona com o computador dos integrantes desligado. Docker empacota
a aplicação; quem a deixa acessível publicamente são os serviços de hospedagem.
O frontend da Vercel chama diretamente a API HTTPS, sem o proxy `/api` do Compose.

## 3. Classes do domínio

| Classe | Papel e regras principais |
|---|---|
| User | Identidade, email único, hash da senha, papel e desativação lógica |
| PerfilINtercambista | Dados pessoais do intercambista, nacionalidades, residência e escolaridade |
| ProficienciaIdioma | Objeto de valor com idioma e nível, pertencente ao perfil |
| Instituicao | Instituição que oferece cursos; nome, descrição e site oficial |
| AreaEstudo | Classifica cursos por relação real; nome e descrição compartilhados |
| Curso | Pertence a instituição; tipo, idioma, duração, ativo e modelos associados |
| ModeloTarefa | Modelo reutilizável de tarefa do curso, com categoria e ativação |
| PlanoIntercambio | Objetivo de um usuário; organiza curso opcional, orçamento, tarefas e idioma |
| TarefaPlano | Etapa real de um plano; título, descrição, origem, prazo, estado e anotações |
| PreparacaoIdioma | Meta de estudo vinculada ao plano, com nível, data e minutos semanais |

São nove entidades e um objeto de valor `@Embeddable`. Requests, responses,
controllers, services e enums não entram nessa contagem. `PlanejamentoFinanceiro`
é um DTO de cálculo. ProficienciaIdioma compõe o perfil e é persistido como
objeto de valor, sem identidade independente.

## 4. Relações e cardinalidades

| Relação | Significado | Mapeamento |
|---|---|---|
| User 1 → 0..1 Perfil | A conta pode ainda não preencher perfil | OneToOne + MapsId; compartilham o ID |
| Perfil 1 → 0..* ProficienciaIdioma | Idiomas são valores pertencentes ao perfil | ElementCollection de Embeddable |
| Perfil 1 → 1..* nacionalidades | Ao preencher perfil, pelo menos uma nacionalidade | ElementCollection de strings |
| User 1 → 0..* Plano | Cada plano tem um dono obrigatório | ManyToOne em Plano |
| Instituicao 0..1 → 0..* Curso | Obrigatória no catálogo; opcional no curso pessoal | ManyToOne em Curso |
| Curso * ↔ * ModeloTarefa | Um modelo pode ser reutilizado em cursos | ManyToMany; curso_modelo_tarefa |
| Plano 0..1 → Curso | Planejamento pode começar sem escolher curso | ManyToOne opcional em Plano |
| AreaEstudo 0..1 → 0..* Curso | Uma área pode classificar vários cursos | ManyToOne em Curso; cursos antigos podem não ter área |
| Plano * ↔ * AreaEstudo | Estrutura de interesses existente no domínio | ManyToMany; sem rota de associação na API atual |
| Plano 1 → 0..* Tarefa | Checklist pessoal de cada plano | OneToMany/ManyToOne; tarefa.plano é dono da FK |
| Tarefa 0..1 → ModeloTarefa | Tarefa de curso preserva referência ao modelo | ManyToOne opcional |
| Plano 1 → 0..* PreparacaoIdioma | Várias metas de idioma no mesmo plano | OneToMany/ManyToOne |

Os diagramas estão em [DIAGRAMAS.md](DIAGRAMAS.md). Associação no diagrama não
implica exclusão em cascata no banco. Não há endpoint de exclusão de plano.
ProficienciaIdioma não possui ID ou repository próprios: é parte do perfil.

## 5. O caminho de uma requisição

1. React monta um JSON e chama `api.js` com o Bearer do usuário.
2. CORS aceita somente origens configuradas. O filtro valida assinatura e validade do JWT.
3. AcessoService verifica o papel e se o recurso pertence ao usuário.
4. O controller recebe a rota e o request; validações de entrada rejeitam dados inválidos.
5. O service busca registros, coordena a transação e chama regras da entidade.
6. O repository usa JPA para consultar/persistir no banco.
7. O DTO de resposta devolve só os campos necessários e evita ciclos/exposição de hash.
8. React atualiza a tela após sucesso ou preserva o formulário e mostra o erro.

Exemplo concreto: salvar anotação não muda o status da tarefa. O controller chama
TarefaPlanoService, que procura a tarefa dentro do plano, valida seu texto na entidade
e salva. A resposta traz texto e horário; a tela atualiza a confirmação de salvamento.

## 6. Responsabilidade das camadas

- **Entidade:** estado e invariantes, como título válido, dinheiro não negativo,
  transição de status e preservação da conclusão.
- **Service:** buscar objetos relacionados, coordenar operações e transações.
- **Repository:** consultas e persistência; a interface JpaRepository é implementada pelo Spring.
- **Controller:** HTTP, códigos de resposta e conversão de requests/responses.
- **DTO:** transportar dados sem confundir o contrato da API com o grafo JPA.
- **Segurança:** identificar a conta e restringir acesso, antes das operações.

Não se deve colocar SQL/fetch em componentes para contornar a API, nem guardar
senha em entidade de resposta. Herança não foi criada artificialmente no domínio:
a composição e as associações representam melhor as relações do problema.

## 7. Login e permissões

Cadastro cria USER e guarda BCrypt, nunca senha legível. Login compara a senha com
PasswordEncoder.matches e emite JWT com email como subject, assinatura HS256 e uma hora
de validade. O segredo está no ambiente do backend. O filtro consulta novamente a conta,
portanto usuário desativado perde acesso mesmo tendo token ainda não expirado.

USER pode consultar catálogos e manter seus próprios dados. ADMIN mantém catálogos e
pode acessar registros de outros usuários conforme a política implementada. A listagem
de planos é filtrada para o usuário conectado. Não há rota pública para virar ADMIN;
a promoção é administrativa no banco, após cadastro autorizado.

O frontend guarda o JWT só em memória. Recarregar a página exige novo login. Logout
limpa a sessão do React; não há blacklist ou refresh token. Trocar senha não revoga
JWTs anteriores. Alterar email exige login novamente. Não apresentar essas limitações
como funcionalidades de revogação que não existem.

## 8. Planos e regras de curso

Tipo de intercâmbio, título e status identificam o plano. Início, duração, orçamento,
moeda e dependência de bolsa compõem o planejamento. Curso pode ser escolhido depois.
O curso selecionado deve estar ativo e ser compatível com o tipo do plano. Uma vez
geradas tarefas específicas do curso, a troca é bloqueada para preservar coerência.
Tarefas por tema ou próprias não impedem a escolha/troca de curso.

O frontend recebe cursoSelecionado, cursoId e cursoNome. O botão do catálogo não
inventa vagas, requisitos migratórios ou bolsas. Dados do catálogo dependem do cadastro
administrativo. `dependeBolsa` é apenas uma marcação; não existe entidade Bolsa.

## 9. Checklist e cadernos

Há quatro origens: modelo associado ao curso, sugestão de tema, tarefa própria e curso pessoal agendado.
As seis coleções de sugestões são DOCUMENTOS, FINANCAS, MORADIA, VIAGEM, IDIOMA e
ESTUDOS, com quatro itens cada. Gerar de novo é aditivo: não apaga edições, prazos,
anotações ou conclusão. A chave estável da sugestão e as restrições únicas do banco
impedem que uma sugestão/modelo seja duplicada no mesmo plano.

O fluxo normal é PENDENTE → EM_ANDAMENTO → CONCLUIDA. A rota `/status` permite
movimentação direta entre quaisquer desses estados, inclusive retorno. Concluir
registra Instant; concluir de novo mantém o mesmo horário; sair de concluída limpa
o horário. As rotas antigas `/iniciar`, `/concluir` e `/reabrir` também permanecem.

No quadro, os cards mostram somente título quando recolhidos. Arraste funciona no
computador; o seletor oferece a operação por teclado e celular. Detalhes incluem
prazo e ações, mas as anotações abrem uma página própria. Texto livre de até 10.000
caracteres fica no banco, separado da descrição de até 400 caracteres. Modelos de
preenchimento por tema são opcionais e só entram em caderno vazio. Salvar é explícito;
sair com mudanças pendentes exige confirmação na navegação do aplicativo.

As anotações não são uma tabela automática de custos e não alteram a meta financeira.
Duas abas editando o mesmo caderno não fazem mesclagem: prevalece o último salvamento.
Ao remover tarefa, suas anotações são removidas junto com ela.

A geração usa consulta de existência e restrição única, sem bloqueio pessimista.
Requisições concorrentes podem gerar 409; recarregar e tentar novamente é seguro.
A aplicação não promete que ambas as requisições concorrentes retornem 200.

## 10. Planejamento financeiro

Valores usam BigDecimal para evitar erro de ponto flutuante monetário. O usuário
informa meta total, valor já guardado e aporte mensal, todos não negativos e na
mesma moeda. Salvar a meta atualiza o orçamento do plano. Não há conversão de moedas.

- Restante = máximo de meta menos guardado e zero.
- Meses disponíveis = meses completos entre hoje e o início previsto, limitado a zero.
- Aporte necessário = restante dividido por meses disponíveis, arredondado para cima em centavos.
- Meses para atingir a meta = restante dividido por aporte, arredondado para cima.
- Projeção no início = guardado + aporte × meses disponíveis.

Sem prazo ou com zero meses não se divide por zero. Sem aporte e ainda faltando
reserva, não se inventa prazo para a meta. A situação informa meta atingida, caminho
compatível, necessidade de ajustar aporte ou falta de dados. Os números são estimativas
informadas pelo usuário; não há histórico de depósitos nem orçamento por itens.

## 11. Perfil e preparação de idioma

Perfil usa o mesmo ID do usuário. Nascimento deve ser passado; país e pelo menos
uma nacionalidade são exigidos. Escolaridade tem enums. Proficiências podem ser lista
vazia, mas não null, e um idioma não pode se repetir. A preparação do plano é outra
coisa: define meta de estudo, nível-alvo, data e minutos por semana.

Preparação tem estados NAO_INICIADA, EM_ANDAMENTO e CONCLUIDA. Editar uma meta
concluída preserva seu status; a verificação de idioma em aberto se aplica às não concluídas. Perfil representa o conhecimento informado; preparar
idioma representa o que o usuário pretende estudar. Uma preparação não altera o perfil
automaticamente.

## 12. Erros e persistência

400 indica dados inválidos; 401 exige login; 403 indica falta de permissão; 404 é recurso
inexistente; 409 é conflito de regra ou de unicidade. Controllers antigos podem retornar
`mensagem`; o DTO comum usa `detail`, `status` e `errors`. api.js aceita os dois formatos.
Não devolver mensagens internas do driver do banco ao usuário.

`@Transactional` delimita uma operação. Como open-in-view=false, os services montam
as respostas que precisam de relações enquanto a transação está ativa. PostgreSQL de
produção usa validate: Hibernate confere tabelas e colunas, sem recriar dados. H2 dev
usa create-drop: os dados são temporários. Não executar os testes sobre public do Render.

## 13. O que os testes demonstram

74 testes do backend passaram após a revisão de escopo; 8 testes do cliente HTTP do
frontend também passaram. Eles verificam domínio, API, JWT, permissões, isolamento,
validação, persistência de operações e cálculos financeiros. Build Docker executa a suíte.
Isso dá evidência dos casos testados, não prova ausência de qualquer defeito ou carga alta.
A suíte atual foi executada em H2; a produção valida o esquema PostgreSQL no startup.

## 14. Divisão inicial informada pelo grupo

| Pessoa | Responsabilidades informadas |
|---|---|
| Rafael | Plano, tarefas/modelos e planejamento de bolsa |
| Lucas | User |
| João | PerfilIntercambista e instituição |
| Ad | Área de estudo e instituição |
| Mateus | Curso e preparação de idioma |

Instituição apareceu para João e Ad na divisão inicial; a tabela registra a divisão
informada pelo grupo.

## 15. Roteiro sugerido para apresentação

1. Explicar o problema e mostrar o diagrama das classes.
2. Entrar como USER e criar plano sem curso, mostrando independência do catálogo.
3. Gerar um tema, editar/criar tarefa e movê-la entre estados.
4. Abrir caderno de moradia, comparar duas opções, salvar e reabrir.
5. Definir meta/reserva/aporte e explicar a projeção financeira.
6. Mostrar preparação de idioma e perfil, distinguindo suas responsabilidades.
7. Mostrar catálogos/associação de modelos com uma conta ADMIN autorizada.
8. Explicar controller → service → entidade → repository; citar um teste real.
9. Mostrar URLs públicas, Dockerfile e variáveis de ambiente sem revelar segredos.
10. Encerrar com limites conhecidos e explicar decisões de simplicidade.

Aquecer o Render antes da apresentação e preparar dados de demonstração coerentes.
Os integrantes devem conseguir explicar suas classes, relações, regras e testes.
O roteiro percorre os fluxos principais e suas regras de funcionamento.

## 16. Leitura complementar

[Catálogo de arquivos](ARQUIVOS-BACKEND.md) · [API](API.md) ·
[Decisões técnicas](REVISAO-ESCOPO.md) · [Operação](VALIDACAO-E-DEPLOY.md) ·
[Frontend](https://github.com/RafaSantos398/FrontEnd_zambas/blob/main/docs/GUIA-FRONTEND.md).


## 17. Cursos criados pelo usuário

Cursos pessoais usam a entidade Curso existente, com proprietario_id apontando para
User. Catálogo antigo continua com dono null e instituição obrigatória na regra de
cadastro administrativo. Curso pessoal tem dono obrigatório pelo método fábrica,
instituição/plataforma em texto opcional e tipoIntercambio null, pois uma formação
financeira ou cultural não é necessariamente o curso principal do intercâmbio.

Etiquetas são um enum EtiquetaCurso com seis opções. A API recebe/devolve lista;
a entidade armazena os nomes em uma coluna, sem coleção JPA extra. Há métodos que
validam e convertem esse conteúdo; não expor a string interna como contrato da API.
Datas são opcionais, mas término não pode anteceder início. Link, quando informado,
deve ser HTTP/HTTPS com host e sem credencial embutida.

CursoService identifica o usuário pelo SecurityContext e AcessoService. O JSON não
escolhe o dono. GET /cursos devolve cursos do catálogo e os pessoais do usuário. Buscar
ou editar pessoal de outro dono retorna 403; manter catálogos continua restrito a ADMIN.
A administração da interface filtra os registros pessoais, que têm formulário próprio.

POST /planos-intercambio/{idPlano}/tarefas/curso recebe cursoId. O filtro verifica o
dono do plano; o service verifica acesso ao curso e a entidade exige mesmo proprietário.
A tarefa guarda curso_apoio_id, com unicidade por plano/curso. O mesmo curso pode apoiar
mais de um plano do mesmo dono. Seu prazo inicial é o término do curso; a descrição é
copiada até o limite de 400 caracteres. A categoria deriva da primeira etiqueta na ordem
do enum (Financeiro, Linguístico, Acadêmico, Profissional, Cultural, Outros).

A etapa tem seu próprio estado, prazo e caderno. Repetir o agendamento retorna a etapa
existente e não a reinicia. Alterar curso não altera etapas existentes; remover a tarefa
permite adicioná-la de novo depois. A ação não matricula, compra nem paga um curso externo.

Relações adicionais: User 0..1 → 0..* Curso (dono opcional para distinguir catálogo) e
Curso 0..1 → 0..* TarefaPlano (origem de apoio opcional). Não foi criada entidade nova.
A migração 20261103-cursos-pessoais.sql preserva dados existentes e é exigida no Render.


## 18. Área de estudo e uso efetivo das dez classes

Curso referencia AreaEstudo por chave estrangeira. Não se trata de enum ou cópia do
nome: consultas seguintes do curso refletem renomeações feitas na área. A relação é
unidirecional; a listagem compartilhada de áreas não devolve cursos pessoais de outros
usuários. Etiquetas dizem a contribuição do curso; a área classifica seu conteúdo.

CursoService resolve areaEstudoId existente ou valida/reutiliza/cria areaEstudoNome.
Não aceita os dois ao mesmo tempo. Ao editar, null em ambos remove o vínculo sem apagar
a área. Criar uma área acontece na mesma transação do curso. As áreas continuam podendo
ser mantidas pela administração; o cadastro do curso permite acrescentar uma classificação.
Não há promessa de unicidade de nomes em requisições simultâneas: o catálogo existente
não possui essa restrição e o reaproveitamento por nome ocorre na consulta do service.

São nove entidades (User, PerfilINtercambista, Instituicao, AreaEstudo, Curso,
ModeloTarefa, PlanoIntercambio, TarefaPlano e PreparacaoIdioma) e o objeto de valor
ProficienciaIdioma. Este último é criado no método atualizar do perfil e salvo na
coleção JPA, depois convertido para a resposta da API. Todos correspondem a funcionalidades
implementadas; não se contam enums, DTOs ou classes de infraestrutura para atingir dez.
