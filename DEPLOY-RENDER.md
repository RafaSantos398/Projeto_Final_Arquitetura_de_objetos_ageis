# Backend Berxy no Render

## Estado

Configuração preparada em render.yaml. Criar esse arquivo localmente não
publica o serviço: ele e o código atual precisam estar no GitHub antes do deploy.
O serviço usará o PostgreSQL existente; não há criação de outro banco no Blueprint.

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
- Frontend: definir VITE_API_URL=https://URL-DO-SERVICO, sem /api, e gerar novo build.
- Quando houver URL da Vercel, adicioná-la a FRONTEND_ORIGINS no backend.

O plano Free pode suspender o serviço por inatividade e o primeiro acesso pode
demorar. A criação de um serviço pago depende de uma escolha explícita do usuário.

Referências: [Docker](https://render.com/docs/docker),
[Blueprint](https://render.com/docs/blueprint-spec),
[healthchecks](https://render.com/docs/health-checks),
[plano Free](https://render.com/docs/free).
