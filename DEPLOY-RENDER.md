# Backend Berxy no Render

## Estado

Backend publicado em 02/11/2026: **https://berxy-api.onrender.com**.
O serviço berxy-api está Live, no plano Free, em Oregon, usando Docker e o
PostgreSQL existente. Código atual publicado: 5f69009, branch feat/tarefasplano.
A mesma versão do código também foi enviada para main.

A versão 5f69009 inclui checklists por tema, tarefas próprias e finanças.
A migração 20261102-checklists-financas.sql foi aplicada no Render em transação,
comparando os dados anteriores de planos e tarefas: preservados. Os 67 testes
passaram no build remoto e o novo contexto validou o esquema de produção.
O frontend correspondente foi publicado na Vercel a partir de b02356d.

Frontend publicado: **https://berxy.vercel.app**. Essa origem foi adicionada
a FRONTEND_ORIGINS no painel do Render e aplicada por Save and deploy, com a
imagem existente do commit 256ba59. Preflight da Vercel retorna 200, API sem
token e login inexistente retornam 401 com CORS, e origem não permitida segue
retornando 403. O formulário público recebeu a mensagem de credenciais inválidas.

O primeiro deploy foi criado pelo formulário Web Service. render.yaml mantém
uma configuração equivalente para referência; não foi aplicado como Blueprint.
Não crie um segundo serviço se a intenção for atualizar o que já está no ar:
use Manual Deploy no serviço existente. Auto-deploy está desativado.

Na publicação inicial, 58 testes passaram no build do Render. Sete checks HTTP na URL HTTPS passaram:
rotas sem token 401; token inválido 401; login inexistente 401; cadastro vazio
400; preflight permitido 200; origem não autorizada 403. Não foram criados dados
de teste no PostgreSQL. Cadastro/login bem-sucedidos e fluxos autenticados ainda
devem ser conferidos com a conta escolhida pelo usuário.

## Publicar com Blueprint

1. Enviar a versão validada para a branch feat/tarefasplano do repositório
   RafaSantos398/Projeto_Final_Arquitetura_de_objetos_ageis.
2. No Render, selecionar New → Blueprint e conectar esse repositório/branch.
3. Conferir o serviço berxy-api, Docker, Oregon e plano Free.
4. Preencher DB_URL, DB_USERNAME e DB_PASSWORD somente no painel do Render.
5. Conferir a configuração antes de iniciar a criação do serviço.

Formato de DB_URL, com o hostname do banco existente:

```text
jdbc:postgresql://HOSTNAME:5432/projeto_intercambio?sslmode=require
```

Não usar a URI postgresql:// com usuário/senha dentro como DB_URL: o backend
espera JDBC e credenciais separadas. Para a conexão interna, conferir primeiro
no Render se o banco e o serviço estão na mesma região e workspace.

JWT_SECRET será gerada pelo Render, sem ser incluída no código. O perfil prod
valida as tabelas existentes e não as recria. A JVM limita o heap a metade da
memória disponível no container; conferir o consumo e os logs no primeiro deploy.

Verificação local: a imagem iniciou saudável com limite de 512 MiB e heap a
50%, usando aproximadamente 219 MiB em repouso no perfil dev/H2. Isso confere
a inicialização nesse limite; o consumo real no Render/PostgreSQL deve ser
acompanhado durante o uso e não foi medido por esse teste.

Não configurar SERVER_PORT: a aplicação usa PORT fornecida pelo Render.
O Dockerfile executa os testes usando H2 durante o build.

## Alternativa: formulário Web Service

Se criar manualmente em New → Web Service:

| Campo | Valor |
| --- | --- |
| Branch | feat/tarefasplano |
| Language | Docker |
| Region | Oregon |
| Instance Type | Free |
| Root Directory | projeto |
| Dockerfile Path | ./Dockerfile (relativo à Root Directory) |
| Docker Build Context | . (relativo à Root Directory) |
| Docker Command | Deixar vazio |
| Health Check Path | Deixar vazio: usar verificação TCP padrão |

Adicionar as mesmas variáveis do render.yaml. JWT_SECRET deve ser aleatória,
com pelo menos 32 bytes. Não usar uma senha de usuário como chave JWT.
Não configurar /auth/me como healthcheck HTTP: sem token ela retorna 401.

## Conferir depois do deploy

- Serviço Live e logs confirmando perfil prod, conexão PostgreSQL e inicialização.
- GET https://URL-DO-SERVICO/auth/me sem token deve retornar 401.
- Não há página de frontend na raiz da API; ela será publicada separadamente.
- Cadastro/login e operações autenticadas devem ser validados antes da entrega,
  com uma conta de teste identificada e sem alterar dados de outras pessoas.
- Frontend: definir VITE_API_URL=https://berxy-api.onrender.com, sem /api, e gerar novo build.
- Quando houver URL da Vercel, adicioná-la a FRONTEND_ORIGINS no backend.

O plano Free pode suspender o serviço por inatividade e o primeiro acesso pode
demorar. A criação de um serviço pago depende de uma escolha explícita do usuário.

Referências: [Docker](https://render.com/docs/docker),
[Blueprint](https://render.com/docs/blueprint-spec),
[healthchecks](https://render.com/docs/health-checks),
[plano Free](https://render.com/docs/free).
