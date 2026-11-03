# Revisão do escopo da disciplina

Revisão em 02/11/2026, consultando o site indicado pelo grupo e os arquivos atuais dos
dois repositórios. A análise distingue conceitos apresentados, adaptações dos mesmos
recursos e trechos simplificados. Não é uma declaração de aprovação do professor.
A rubrica fornecida pelo grupo exige Java/Spring, React, persistência, domínio,
Docker e publicação; ela também restringe a participação de IA no backend.

## Fontes consultadas

| Aula | Conteúdo consultado | Aplicação no Berxy |
|---|---|---|
| [02](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-02/material/) | Classes, atributos, construtores e métodos | Entidades e objetos de valor |
| [03](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-03/material/) | Encapsulamento e relacionamentos | Regras nas entidades e coleções de objetos relacionados |
| [04](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-04/material/) | Estúdio de objetos | Exercício da modelagem e responsabilidade de métodos |
| [05](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-05/material/) | API REST e Spring Boot | Controllers, rotas e JSON |
| [06](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-06/material/) | Injeção de dependência e services | Colaboração entre controllers/services e módulos |
| [07](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-07/material/) | Estúdio de APIs | Organização e operações dos recursos |
| [08](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-08/material/) | Pacotes, DTOs, exceções, handlers e Swagger | DTOs e tratamento de erros simples; Swagger não foi adicionado |
| [09](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-09/material/) | Spring Security, BCrypt e UserDetailsService | Hash e carregamento de usuário |
| [10](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-10/material/) | Estúdio de API completa | Fluxos e validações entre recursos |
| [11](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-11/material/) | Herança, interfaces e abstrações | Interfaces de repository e UserDetailsService; domínio usa composição |
| [12](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-12/material/) | JPA, entidades, repositories e relações 1:N | Persistência das entidades e consultas de repository |
| [13](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-13/material/) | Relações N:N e usuários no banco | Modelos dos cursos e interesses dos planos |
| [14](https://ezambomsantana.github.io/arquitetura-objetos-site/aulas/aula-14/material/) | JWT, filtro, login e validação | JJWT 0.12.3, Bearer, validade e NotBlank/Email/Size |

Os materiais foram lidos diretamente nas URLs acima. Os exemplos didáticos usam
domínios diferentes do intercâmbio; copiar seus nomes de classes não é necessário
para aplicar os mesmos conceitos. A aula 08 apresenta Swagger como ferramenta;
a rubrica não exige sua instalação. A API foi documentada em Markdown, sem introduzir
uma dependência apenas para aumentar a entrega.

## Simplificações executadas

| Antes | Agora | Motivo e consequência |
|---|---|---|
| ProblemDetail e herança de ResponseEntityExceptionHandler | DTO ErroResponse e ControllerAdvice/ExceptionHandler | Aproxima o tratamento ao padrão da Aula 08; mantém detail/status/errors consumidos pela interface |
| MaxBytes e ConstraintValidator próprios | if no UserService e CampoInvalidoException | Evita criar anotação de validação; conserva o limite de 72 bytes UTF-8 do BCrypt em cadastro, login e troca de senha |
| LockModeType.PESSIMISTIC_WRITE e Query JPQL de bloqueio | findById, consulta de existência e restrições únicas existentes | Usa operações usuais de repository; não permite dados duplicados, mas requisições simultâneas podem receber 409 |

Não houve alteração de tabela, remoção de dados ou mudança nos caminhos dos endpoints.
Não foi adicionada biblioteca de produção. A suíte atual passou com 72 testes após
as mudanças, incluindo senhas multibyte e os fluxos de tarefas/anotações.

Os campos RFC opcionais do ProblemDetail, como type/title/instance, não fazem parte
do novo DTO comum. O frontend usa detail/errors e já aceita os handlers locais com
mensagem ou texto. A documentação da API reflete essa decisão.

## Adaptações dentro das tecnologias escolhidas

As linhas abaixo não são apresentadas como cópias literais das aulas. São aplicações
dos recursos já existentes na solução, com finalidade delimitada e documentada:

- **Records:** o material apresenta DTOs como classes; o Berxy já adotava records em
  ModeloTarefa e o usuário pediu preservar esse padrão. São DTOs Java, sem framework adicional.
- **BigDecimal, datas, enums e coleções:** biblioteca padrão Java para valores monetários,
  prazos e estados. Valores não devem ser substituídos por double para apenas reduzir código.
- **Transações de service:** necessárias para operações completas e carregamento de relações
  com open-in-view=false. A Aula 13 inclui import de Transactional, mas não desenvolve
  todos os atributos usados aqui; readOnly e limites explícitos são uma adaptação do projeto.
- **Objeto de valor e perfil:** Embeddable/ElementCollection/MapsId/OneToOne especializam o
  mapeamento JPA para dados pertencentes ao perfil e identidade compartilhada. Essas anotações
  não foram localizadas nos roteiros lidos; não se afirma que foram ensinadas explicitamente.
  Mantêm a composição e a persistência JPA já adotadas, sem criar mecanismo externo. O grupo
  precisa conseguir explicar seu uso e alinhar essa interpretação de escopo com o professor.
- **Consultas derivadas e unicidade:** repositories consultam por dono/plano/origem; constraints
  do banco impedem duplicações. São aplicações de persistência, não um novo subsistema.
- **CORS, ambiente e permissões:** configuração do próprio Spring para integrar duas origens
  publicadas e impedir acesso a dados de outra conta. Não remover essas proteções para reduzir
  a quantidade de classes ou aproximar exemplos locais.
- **Testes:** JUnit/MockMvc verificam a aplicação; não fazem parte dos fluxos de produção.
- **Docker, Render, Vercel e PostgreSQL:** infraestrutura da entrega; Docker/Render/Vercel
  constam da rubrica, e PostgreSQL foi a escolha do grupo para persistência durável.
- **React/JavaScript/CSS:** atende ao frontend da rubrica; hooks, fetch, FormData e arraste
  nativo não exigem biblioteca de UI. Vite compila o frontend; Nginx só atende o Compose.

Por isso, a conclusão é de aderência às tecnologias e conceitos gerais escolhidos,
com três simplificações concretas e as especializações acima declaradas. Não seria
correto garantir aderência literal a cada anotação de código ou aprovação acadêmica.
Se o professor adotar um limite literal que exclua especializações JPA, será necessário
replanejar o perfil e migrar dados; isso não deve ser feito silenciosamente como se
fosse apenas alteração de estilo. A implementação existente foi preservada nesse ponto.

## Requisitos da rubrica e evidências

| Requisito | Evidência |
|---|---|
| Java com Spring Boot | pom.xml e código em projeto/src/main/java |
| Dez classes de domínio | Nove entidades e ProficienciaIdioma como objeto de valor; não contar DTOs/enums |
| Encapsulamento e composição | Estado privado, métodos de domínio e relações do diagrama |
| Camadas e API REST | Controllers, services e repositories por funcionalidade |
| Persistência | PostgreSQL publicado com validate; H2 somente dev/teste |
| React integrado | Frontend chama a API com fetch/Bearer |
| Docker, Render e Vercel | Dockerfile, serviço berxy-api e domínio berxy.vercel.app |
| Domínio do grupo | Manual, catálogo de arquivos e roteiro; depende da compreensão real de cada integrante |
| Autoria do backend | Ponto a alinhar: houve assistência de IA, inclusive implementação |

## Autoria e apresentação

A rubrica permite IA livremente no frontend e limita o backend a suporte/consulta,
exigindo código majoritariamente autoral e domínio da implementação. O histórico
inclui geração e edição de backend por IA. As simplificações não mudam esse fato.
O grupo deve apresentar o processo com transparência, esclarecer a aceitação dessa
participação e fazer suas próprias revisões. Este relatório não deve ser usado para
declarar autoria inexistente ou afirmar que estudar o código, por si só, elimina a regra.


## Cursos pessoais em 03/11/2026

Atualização usa a entidade Curso, associação ManyToOne com User/curso de apoio, enum,
campos de data e texto e operações usuais de repository. Etiquetas são convertidas
pela entidade para uma coluna textual, sem introduzir coleção JPA ou biblioteca.
React usa formulários e checkboxes nativos. Não foram adicionadas dependências.
A migração é necessária para manter persistência e distingue catálogo de curso pessoal.

A relação Curso–AreaEstudo usa ManyToOne e repository já presentes no material; não acrescenta entidade nem dependência. A contagem permanece nove entidades e um objeto de valor em uso.
