# Inicialização do PostgreSQL

`schema-postgresql.sql` foi gerado pelo Hibernate a partir das entidades atuais,
usando o dialeto PostgreSQL e a estratégia de nomes da aplicação. Ele define
13 tabelas, 13 chaves estrangeiras, índices únicos e restrições de enums.
O SQL não contém comandos de exclusão de tabelas ou dados.

## Histórico de validação em 02/11/2026

O inicializador concluiu a conexão com o PostgreSQL do Render: 13 tabelas
presentes e `ProjetoApplicationTests` aprovado no perfil `prod`, com
`ddl-auto=validate`. Isso confirma conexão, carregamento do contexto e
compatibilidade do esquema com as entidades.

A auditoria posterior executou a suíte completa no PostgreSQL 18.6 do Render:
**55 testes aprovados, sem falhas, erros ou testes ignorados**, no perfil `prod`
e com `ddl-auto=validate`. A execução usou o esquema temporário
`codex_test_0aee6f989e08`, removido ao final. As contagens das 13 tabelas de
`public` foram preservadas. Também passaram os mesmos 55 testes em H2 e o
empacotamento Maven. O JAR no perfil `dev` passou por 19 chamadas HTTP de
autenticação JWT/Bearer, permissões e consultas do próprio plano, tarefas e
preparações de idioma. A suíte inclui 12 testes de cadastro, login, tokens
adulterados/expirados, permissões, desativação e troca de senha. A implementação
segue o roteiro JWT da Aula 14; as verificações anteriores com sessão foram
substituídas pelas verificações do fluxo Bearer.

## Criar o esquema e validar o Spring

Na pasta do `pom.xml`, execute no PowerShell, substituindo o host pelo endereço
externo do seu banco no Render:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\inicializar-postgres.ps1 -DatabaseHost "SEU-HOST.oregon-postgres.render.com"
```

O usuário atual é solicitado na janela local. O banco padrão é
`projeto_intercambio` e a porta é `5432`. Para informar esses valores na chamada,
use `-DatabaseUser`, `-DatabaseName` e `-DatabasePort`. É necessário ter o cliente
`psql` instalado.

A senha é solicitada de forma oculta na janela local. Ela fica somente nas
variáveis do processo durante a execução, sem ser salva no repositório.

O inicializador:

1. Conecta ao servidor externo com SSL obrigatório.
2. Confere as tabelas do esquema `public`.
3. Se o banco estiver vazio, executa todo o SQL em uma única transação, com
   interrupção e rollback se qualquer comando falhar.
4. Se já houver tabelas, não as recria nem altera seus dados.
5. Executa `ProjetoApplicationTests` no perfil `prod`, com
   `spring.jpa.hibernate.ddl-auto=validate`, para conferir o esquema real.
6. Mostra o resultado e restaura as variáveis de ambiente do processo.

O log da validação fica na pasta temporária do Windows, no caminho exibido pelo
script. Não há senha nos argumentos dos comandos ou no SQL.

## Execuções posteriores

### Testar todos os fluxos sem usar os dados de public

Na pasta do `pom.xml`:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\testar-postgres.ps1 -DatabaseHost "SEU-HOST.oregon-postgres.render.com"
```

O usuário e a senha são solicitados localmente. O script cria um esquema
`codex_test_` com sufixo aleatório, aplica o SQL somente nele e executa toda a
suíte no perfil `prod`, com `currentSchema` e `ddl-auto=validate`. Ao terminar,
remove apenas o esquema criado nesta execução e compara as contagens das 13
tabelas de `public`. Escritas concorrentes de outras aplicações podem mudar
essas contagens. O usuário precisa de permissão para criar esquemas.

Os testes de API recusam PostgreSQL fora de um esquema temporário com esse
formato. A limpeza de `FluxosPlanejamentoTest` apaga os dados de suas tabelas;
os demais testes de API usam rollback. Não execute a suíte diretamente sobre
`public`.

### Executar a aplicação

O perfil `prod` continua com `ddl-auto=validate`: ele não cria nem apaga tabelas.
Configure `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` nas variáveis de ambiente da
execução do backend. A URL JDBC deve usar SSL, por exemplo:

```text
jdbc:postgresql://SEU-HOST.oregon-postgres.render.com:5432/projeto_intercambio?sslmode=require
```

O login JWT segue a Aula 14. Em produção, configure também `JWT_SECRET`
com pelo menos 32 bytes em UTF-8, mantida fora do repositório. Os scripts de
inicialização e testes geram uma chave temporária apenas para sua validação
e restauram o ambiente ao terminar. Essa chave não configura a execução
posterior da aplicação: o backend precisa de sua própria variável persistente.

O script inicial serve para um banco novo. Futuras alterações nas entidades
podem exigir alterações correspondentes no esquema antes de executar `validate`.


## Estado atual e migrações

O esquema completo inclui checklists por tema, finanças e cadernos das tarefas.
As migrações abaixo já foram aplicadas ao banco publicado, com preservação dos dados:

1. migrations/20261102-checklists-financas.sql
2. migrations/20261102-anotacoes-tarefas.sql

Para um banco existente anterior, aplicar em ordem antes de publicar a versão que usa
os campos. Para banco vazio, o schema-postgresql.sql atual já contém esses campos.
A revisão de escopo de 02/11/2026 altera somente código e documentação, sem migração.
72 testes atuais passaram em H2; os resultados PostgreSQL acima são históricos,
não uma afirmação de que a suíte atual inteira rodou nesse banco.


## Cursos pessoais

A migração migrations/20261103-cursos-pessoais.sql inclui proprietário, etiquetas,
datas/link e origem de apoio nas tarefas. Curso de catálogo continua exigindo instituição
na regra de domínio; curso pessoal aceita instituição livre e não tem tipo de intercâmbio.
Para banco novo, o schema-postgresql.sql atualizado já inclui essas colunas e FKs.

Área no curso: aplicar migrations/20261103-area-do-curso.sql depois de cursos pessoais; FK opcional preserva registros anteriores. Banco novo já usa schema completo atualizado.
