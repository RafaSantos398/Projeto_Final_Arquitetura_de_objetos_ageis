# Decisões técnicas do Berxy

## Organização das classes

Os pacotes agrupam funcionalidades. Controllers recebem requisições HTTP e retornam
DTOs; services coordenam transações e consultas; entidades validam seu estado e
regras; repositories acessam a persistência com Spring Data JPA.

O domínio contém nove entidades e o objeto de valor ProficienciaIdioma, que compõe
o perfil. As responsabilidades e relações estão no [guia do grupo](GUIA-DO-GRUPO.md)
e nos [diagramas](DIAGRAMAS.md).

## Persistência e relacionamentos

- User possui perfil e planos; cursos pessoais referenciam seu proprietário.
- PerfilINtercambista usa OneToOne/MapsId para compartilhar a identidade da conta.
- ProficienciaIdioma usa Embeddable/ElementCollection para persistir idioma e nível.
- Curso referencia Instituicao e AreaEstudo por ManyToOne e modelos por ManyToMany.
- PlanoIntercambio organiza tarefas e metas de idioma; seu curso principal é opcional.
- TarefaPlano pode referenciar um modelo de origem ou um curso de apoio.

PostgreSQL armazena os dados em produção. O perfil dev e os testes usam H2 isolado.
Produção usa ddl-auto=validate; alterações estruturais usam as migrações SQL do
repositório. Services usam transações e a configuração mantém open-in-view=false.

## Regras e contratos

- DTOs usam records Java. Os contratos estão em [API.md](API.md).
- Erros usam ErroResponse e ControllerAdvice/ExceptionHandler, com detail/status/errors.
- Senhas usam BCrypt, com limite de 72 bytes UTF-8 validado no service.
- Login emite JWT; as rotas privadas recebem Authorization: Bearer TOKEN.
- Permissões e verificações de propriedade protegem os dados de cada conta.
- Restrições únicas evitam tarefas duplicadas. Requisições simultâneas podem retornar 409.
- BigDecimal representa valores monetários; datas, enums e coleções usam Java padrão.

## Cursos e áreas

Cursos pessoais reutilizam Curso e pertencem à conta autenticada. Etiquetas são
convertidas pela entidade para uma coluna textual. Datas e link apoiam o planejamento.
Adicionar um curso ao cronograma cria uma tarefa; repetir preserva a etapa existente.

AreaEstudo é uma classificação compartilhada. O cadastro aceita o ID de uma área
existente ou o nome de uma nova; nomes existentes são reutilizados sem diferenciar
maiúsculas e minúsculas. O vínculo é opcional e usa curso.area_estudo_id.
Etiquetas indicam contribuições, enquanto a área classifica o assunto do curso.

## Interface e publicação

React com JavaScript e CSS usa fetch, formulários e arraste nativo. Vite compila o
frontend. A API é publicada com Docker no Render; a interface usa Vercel. CORS
configura as origens autorizadas. No Compose, Nginx encaminha /api; a Vercel chama
a API HTTPS diretamente.

## Verificação e operação

JUnit/MockMvc verificam regras, contratos e permissões. Os testes do frontend
verificam o cliente HTTP. Evidências, variáveis de ambiente, migrações e comandos
estão em [Validação e operação](VALIDACAO-E-DEPLOY.md).
