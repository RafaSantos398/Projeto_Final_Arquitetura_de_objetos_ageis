# Berxy

Sistema de planejamento de intercâmbio desenvolvido para Arquitetura de Objetos do Insper.
O usuário organiza planos, checklists, preparação de idioma, reserva financeira e
anotações por tarefa, mesmo sem escolher um curso do catálogo.

- **Site:** https://berxy.vercel.app
- **API:** https://berxy-api.onrender.com
- **Frontend:** https://github.com/RafaSantos398/FrontEnd_zambas
- **Material da disciplina:** https://ezambomsantana.github.io/arquitetura-objetos-site/

## Documentação para o grupo

[Baixar o pacote completo de documentação](docs/berxy-documentacao-grupo.zip), com
manual, catálogo, API, guia do frontend e diagramas editáveis/SVG.

| Documento | Conteúdo |
|---|---|
| [Guia do grupo](docs/GUIA-DO-GRUPO.md) | Problema, arquitetura, regras, relações, limitações e roteiro de apresentação |
| [Diagramas](docs/DIAGRAMAS.md) | Classes e cardinalidades, camadas, autenticação e implantação |
| [Todos os arquivos do backend](docs/ARQUIVOS-BACKEND.md) | Responsabilidade, métodos, dependências, DTOs, enums, configurações e testes |
| [Contratos da API](docs/API.md) | Rotas reais, campos dos requests/responses e exemplos |
| [Revisão do escopo](docs/REVISAO-ESCOPO.md) | Correspondência com as Aulas 02–14 e simplificações realizadas |
| [Validação e operação](docs/VALIDACAO-E-DEPLOY.md) | Testes, banco, execução local e atualização dos serviços |
| [Guia do frontend](https://github.com/RafaSantos398/FrontEnd_zambas/blob/main/docs/GUIA-FRONTEND.md) | Cada arquivo React, cliente HTTP, navegação e Docker |

## Funcionalidades atuais

- Cadastro e login JWT; permissões USER/ADMIN e isolamento por dono do plano.
- Perfil, nacionalidades, proficiências, dados da conta e troca de senha.
- Planos com tipo, status, previsão de início, duração, orçamento e curso opcional.
- Cursos pessoais criados e editados pelo próprio usuário, com plataforma/instituição livre, link, datas e etiquetas.
- Etiquetas Financeiro, Linguístico, Acadêmico, Profissional, Cultural e Outros, combináveis.
- Adição de curso pessoal ao cronograma/checklist de um ou mais planos, sem duplicar a etapa.
- Catálogos administrativos de instituições, cursos principais, áreas e modelos de tarefa.
- Associação de modelos aos cursos, ativação/desativação e geração do checklist do curso.
- Seis temas independentes com 24 sugestões; geração repetida preserva tarefas e progresso.
- Tarefas próprias, edição, remoção, prazo e movimentação entre três estados.
- Quadro compacto: título recolhido, detalhes por clique e arraste nativo no computador;
  seletor de status disponível para teclado e celular.
- Finanças: meta total, valor guardado, aporte mensal, restante e projeção até o início.
- Caderno em página própria por tarefa: até 10.000 caracteres, modelos opcionais por
  tema, data do último salvamento e aviso ao sair com alterações não salvas.
- Preparação de idioma com nível-alvo, meta semanal, prazo e acompanhamento de status.

## Tecnologias e organização

Java 26, Spring Boot 4.1.1, Spring MVC, Spring Data JPA, Spring Security/BCrypt,
Bean Validation e JJWT 0.12.3. PostgreSQL em produção; H2 em memória no desenvolvimento
isolado. Frontend React com JavaScript e CSS; Vite para build. Docker no backend,
Render para a API e Vercel para a interface.

A pasta `projeto/` contém `pom.xml`, Maven Wrapper, código Java e Dockerfile.
Os pacotes são organizados por funcionalidade. Entidades concentram regras;
services coordenam transações e consultas; repositories acessam o banco;
controllers recebem HTTP e DTOs transportam dados. Não há TypeScript, Tailwind,
biblioteca de UI ou serviço externo de autenticação.

## Executar e testar

Na pasta `projeto/`, com JDK 26:

```powershell
.\mvnw.cmd test -Dspring.profiles.active=dev
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

O perfil dev usa H2 descartável; sua base é recriada ao reiniciar. Produção usa
`ddl-auto=validate` e exige `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `JWT_SECRET`.
`FRONTEND_ORIGINS` define as origens permitidas. Não colocar segredos no Git.
Para executar tudo por Docker, seguir o [guia de operação](docs/VALIDACAO-E-DEPLOY.md).

## Autenticação e contratos

`POST /auth/register` cadastra; `POST /auth/login` retorna `{ token, usuario }`.
Rotas privadas recebem `Authorization: Bearer TOKEN`. JWT válido por uma hora.
A conta USER acessa seus dados; ADMIN mantém os catálogos e pode acessar outros
registros conforme a política da API. Não existe promoção pública para ADMIN.

O frontend guarda o token somente na memória: recarregar exige novo login.
Logout descarta o token no cliente; a API não mantém lista de revogação. Alterar
senha não invalida JWTs já emitidos; desativar a conta bloqueia seu uso. Trocar
email exige novo login. Detalhes e corpos JSON: [API](docs/API.md).

## Estado da validação

Revisão de 03/11/2026: **76 testes do backend** e **8 do frontend** aprovados no
build Docker local. Os 76 incluem as validações UTF-8 após simplificação do código.
As versões anteriores passaram por validação PostgreSQL; isso não equivale a afirmar
que a suíte atual inteira foi reexecutada no PostgreSQL. Os testes atuais usam H2
isolado; a inicialização de produção verifica a compatibilidade do esquema PostgreSQL.

As migrações de checklists/finanças e anotações já foram aplicadas ao banco existente,
preservando os dados. A atualização de cursos pessoais exige a migração aditiva `20261103-cursos-pessoais.sql`, aplicada antes do deploy. Veja evidências e
limites em [Validação e operação](docs/VALIDACAO-E-DEPLOY.md).

## Escopo acadêmico

A revisão simplificou handlers para DTO e `@ExceptionHandler`, substituiu a anotação
própria de tamanho de senha por validação no service e retirou bloqueio pessimista/JPQL
personalizado da geração por tema. A restrição única do banco continua impedindo duplicação;
um conflito simultâneo pode retornar 409 e deve ser resolvido recarregando e tentando novamente.
Os records já adotados em ModeloTarefa foram preservados conforme orientação do projeto.

## Limites conhecidos

Não há catálogo completo de bolsas, recomendação automática de cursos, cotações de
preços/câmbio, anexos ou notificações. `dependeBolsa` é uma indicação de planejamento.
Anotações são texto livre; não atualizam automaticamente o orçamento. Áreas de interesse
existem no domínio, mas a API atual não expõe sua associação a planos. O Render Free pode
demorar no primeiro acesso após inatividade. O navegador precisa de conexão à API.


## Cursos pessoais e cronograma

O menu Meus cursos permite cadastrar sem depender de um administrador ou de instituição
no catálogo. Cada cadastro pertence à conta autenticada; listagem, busca e edição não
expõem cursos pessoais de outra conta, inclusive na consulta de catálogos. Um curso
pessoal não é classificado automaticamente como curso de idioma ou graduação: o tipo
de intercâmbio fica null e as etiquetas indicam sua contribuição real.

Adicionar ao cronograma cria uma tarefa do plano com prazo igual ao término previsto.
Repetir a ação devolve a etapa existente, preservando seu progresso, anotações e prazo.
Editar datas/descrição do curso não sobrescreve as tarefas já criadas. O usuário pode
ajustá-las pelo checklist. Curso principal de intercâmbio e cursos de apoio são fluxos
diferentes; a escolha de curso principal permanece opcional e recolhida dentro do plano.


## Área de estudo do curso e classes de domínio

Curso possui uma referência ManyToOne para AreaEstudo, persistida em curso.area_estudo_id.
O usuário pode escolher uma área existente ou informar uma nova no próprio cadastro;
nomes já existentes são reutilizados sem diferenciar maiúsculas/minúsculas. Área é
uma classificação compartilhada; os cursos pessoais continuam privados. Área e
etiquetas são campos distintos. Também é possível filtrar os cursos pela área.
Cursos anteriores podem permanecer sem área e ser classificados depois.

As dez classes de domínio efetivamente usadas são User, PerfilINtercambista,
ProficienciaIdioma, Instituicao, AreaEstudo, Curso, ModeloTarefa, PlanoIntercambio,
TarefaPlano e PreparacaoIdioma. São nove entidades e um objeto de valor Embeddable:
ProficienciaIdioma é instanciado, validado, persistido e retornado no perfil. DTOs,
enums, controllers e repositories não entram nessa contagem.

Atualização da relação com área: migração 20261103-area-do-curso.sql, aditiva, antes
do deploy. Os 76 testes atuais incluem associação real, renomeação, remoção do vínculo,
ID inexistente, cadastro de área e reaproveitamento de nomes. Não há entidade nova.
