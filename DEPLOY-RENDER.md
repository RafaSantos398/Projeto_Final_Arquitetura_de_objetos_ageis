# Backend Berxy no Render

Serviço existente: **berxy-api**, URL **https://berxy-api.onrender.com**.
Frontend: **https://berxy.vercel.app**. PostgreSQL existente, com SSL.

## Configuração atual

| Campo | Valor |
|---|---|
| Repositório | RafaSantos398/Projeto_Final_Arquitetura_de_objetos_ageis |
| Branch de entrega | main |
| Runtime | Docker |
| Root Directory | projeto |
| Dockerfile Path | ./Dockerfile, relativo à Root Directory |
| Docker Build Context | . |
| Região / plano | Oregon / Free |
| Auto-deploy | Desativado; atualização manual no serviço existente |

O render.yaml representa configuração equivalente para referência. O serviço foi
criado pelo formulário; editar o YAML não altera sozinho a configuração no painel.
Não criar outro serviço apenas para atualizar o código.

## Variáveis do serviço

- SPRING_PROFILES_ACTIVE=prod
- DB_URL=jdbc:postgresql://HOST:5432/projeto_intercambio?sslmode=require
- DB_USERNAME e DB_PASSWORD: credenciais atuais somente no ambiente do Render.
- JWT_SECRET: chave privada de pelo menos 32 bytes, sem valor padrão de produção.
- FRONTEND_ORIGINS: https://berxy.vercel.app e origens locais explicitamente permitidas.
- JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=50.0

Não configurar SERVER_PORT fixo no Render: a aplicação usa PORT da plataforma.
O Dockerfile executa os testes no perfil dev/H2 e inicia a imagem final no perfil prod.
Produção usa ddl-auto=validate; não recria o banco. Migrações de checklists/finanças e
anotações já foram aplicadas. A revisão de escopo não exige outra migração.

## Atualizar

Enviar main; conferir a branch no painel; Manual Deploy → Deploy latest commit.
Aguardar testes/build e Live. Conferir a API sem Bearer em 401, login inválido em 401,
cadastro inválido em 400 e preflight da Vercel em 200 com a origem correta.
Depois conferir o frontend publicado pela integração Git da Vercel.

[Validações e operação completas](docs/VALIDACAO-E-DEPLOY.md) ·
[Revisão de escopo](docs/REVISAO-ESCOPO.md) · [API](docs/API.md).
