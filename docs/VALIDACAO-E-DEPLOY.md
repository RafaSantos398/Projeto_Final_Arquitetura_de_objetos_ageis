# Validação e operação do Berxy

Revisão de 02/11/2026. URLs públicas: https://berxy.vercel.app e
https://berxy-api.onrender.com. O backend usa Docker Free no Render, Oregon,
e PostgreSQL existente. O frontend usa Vercel, com VITE_API_URL apontando para a API.

## Resultado local desta revisão

- 72 testes do backend aprovados no build Docker Linux, sem falhas ou erros.
- 8 testes do cliente HTTP do frontend aprovados; build React/Vite aprovado.
- Containers H2 e frontend isolados iniciaram saudáveis na porta 3001.
- Seis chamadas simultâneas de geração por tema retornaram somente 200/409;
  a consulta final confirmou quatro tarefas, sem duplicação.
- Checks HTTP pelo proxy validaram login inválido, senha multibyte, erros por campo,
  anotação salva/reconsultada e ID de catálogo inválido em 400.
- Não houve alteração no esquema de banco nesta revisão de escopo.
- As duas migrações anteriores já estão aplicadas ao PostgreSQL publicado.

Os testes atuais do backend usam H2 em memória. A execução prévia de suítes anteriores
no PostgreSQL não equivale a repetir a suíte de 72 testes nesse banco. No deploy,
validate confere o esquema PostgreSQL e deve permitir o startup sem recriação.
Não inserir fixtures de testes na base compartilhada do grupo.

## Execução Java local

Da pasta projeto, com JDK 26 e Maven Wrapper:

```powershell
.\mvnw.cmd test -Dspring.profiles.active=dev
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

A API atende a porta 8080, salvo PORT informado. O perfil padrão é dev. H2 é
memória e create-drop; não usar dev como garantia de persistência após reiniciar.
No frontend, usar npm ci, npm test, npm run build e npm run dev, conforme seu README.

## Conjunto Docker isolado

Na pasta do frontend:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\iniciar-docker.ps1 -BackendPath "CAMINHO-DO-BACKEND\projeto" -Local -FrontendPort 3001
```

Usar a pasta que contém pom.xml como BackendPath. O modo Local cria berxy-local,
com H2 e chave JWT pública somente para testes. O build executa os testes. Os dados
somem ao reiniciar o backend. O modo normal, sem Local, usa PostgreSQL e pede as
credenciais na janela; sua porta usual é 3000. Consultar DOCKER.md do frontend.

Para encerrar somente o ambiente isolado:

```powershell
docker compose -p berxy-local -f compose.yaml -f compose.local.yaml down
```

Não usar prune nem remover outros projetos Docker para atualizar este sistema.

## Banco novo e atualizações

O arquivo projeto/database/schema-postgresql.sql contém o esquema atual completo.
O inicializador só o aplica se o banco estiver vazio. Para banco existente antigo,
aplicar as migrações em ordem: 20261102-checklists-financas.sql e depois
20261102-anotacoes-tarefas.sql. As duas são aditivas e já foram aplicadas ao Render.
A revisão de handlers/senha/repository não exige nova migração.

Nunca trocar validate por create/create-drop em produção para resolver coluna ausente.
Credenciais são variáveis do processo/serviço, não valores no Git. DB_URL deve ser JDBC,
com sslmode=require; usuário e senha ficam em DB_USERNAME/DB_PASSWORD separados.
JWT_SECRET deve ter pelo menos 32 bytes e permanecer estável entre reinicializações.

## Atualizar os serviços existentes

1. Testar e enviar os commits para main de ambos os repositórios.
2. No Render, conferir que o serviço berxy-api acompanha main e selecionar
   Manual Deploy → Deploy latest commit. Não criar outro serviço/Blueprint.
3. Conferir o commit, o resultado dos testes do build, perfil prod, validação JPA
   e status Live. Nunca copiar segredos do painel para a documentação.
4. A Vercel acompanha main do frontend e publica automaticamente. Conferir Ready
   e o commit correspondente no domínio berxy.vercel.app.
5. Conferir GET privado sem token em 401, login inválido em 401 com detail,
   cadastro vazio em 400 e OPTIONS permitido para a origem da Vercel.
6. Validar fluxos autenticados com conta de demonstração autorizada e dados coerentes.

Esta revisão alinha render.yaml e o serviço publicado à main. FRONTEND_ORIGINS deve
incluir https://berxy.vercel.app; não liberar qualquer origem para contornar CORS.
Alterar VITE_API_URL exige novo build do frontend; VITE_* é público no pacote JavaScript.
Nunca colocar DB_PASSWORD ou JWT_SECRET nessas variáveis.

## Recuperação e limites

O Git mantém as versões anteriores; o Render permite republicar um commit anterior.
Migrações aditivas anteriores preservam campos novos ao reverter apenas o código.
Não executar rollback de dados ou exclusão de tabelas para desfazer mudança de interface.
O plano Free pode suspender a API após inatividade: aquecer antes da apresentação.
Recarregar o frontend exige login porque o Bearer fica em memória. O frontend não
possui modo offline, refresh token ou mesclagem de cadernos editados em duas abas.
