# Projeto_Final_Arquitetura_de_objetos_ageis

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

A aplicação completa ainda depende de classes e enums de outras funcionalidades,
da configuração do PostgreSQL e dos fluxos de autenticação e autorização.
O relacionamento com os cursos deve ser integrado quando `Curso` existir.
Ao implementar a segurança, restringir a manutenção de instituições conforme
o papel do usuário.

Os três testes de domínio de instituição verificam normalização de dados,
campos opcionais, validação do nome e preservação dos dados após uma atualização
inválida. Eles passaram em execução isolada; a execução completa com Spring e
PostgreSQL ainda precisa ser validada.

O enunciado limita o uso de IA no back-end a suporte. O grupo deve revisar e
adequar esta implementação às regras da disciplina, compreendendo as decisões
antes de incorporá-la à entrega.

## Perfil do intercambista (Joao)

O perfil compartilha o ID do usuario por `@MapsId`. O usuario deve existir e estar
ativo. Cada usuario possui no maximo um perfil. A resposta inclui apenas os dados
do perfil, sem serializar o usuario e sua senha.

| Metodo | Rota | Resultado |
| --- | --- | --- |
| POST | `/users/{usuarioId}/perfil` | Cria perfil (201); duplicado retorna 409 |
| GET | `/users/{usuarioId}/perfil` | Consulta perfil (200) |
| PUT | `/users/{usuarioId}/perfil` | Substitui os dados do perfil (200) |

Usuario ou perfil inexistente retorna 404; dados invalidos retornam 400.
Exemplo de corpo para POST e PUT:

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

Nascimento deve estar no passado; nacionalidades devem conter ao menos um texto.
Pais e nacionalidades aceitam ate 100 caracteres e sao aparados nas extremidades.
Proficiencias podem ser uma lista vazia; cada item exige idioma e nivel. Idiomas
usam codigos de duas letras, opcionalmente com regiao (en, fr, pt-BR), normalizados
para minusculas e sem repeticao. O nivel usa o mesmo `NivelProficiencia` de
PreparacaoIdioma: A1, A2, B1, B2, C1, C2.
Escolaridade: ENSINO_FUNDAMENTAL, ENSINO_MEDIO, GRADUACAO, POS_GRADUACAO,
MESTRADO ou DOUTORADO; situacao: CURSANDO, CONCLUIDO ou INTERROMPIDO.
PUT substitui as colecoes; todos os campos sao obrigatorios. A validacao completa
ocorre antes de modificar o perfil.

O mapeamento das nacionalidades passa a usar `perfil_nacionalidades`, coluna
`nacionalidade`. Bancos que ja tenham dados na antiga colecao implicita precisam
migrar esses dados antes de usar esta versao.

A configuracao de seguranca atual libera as rotas. A restricao ao dono do perfil
depende da entrega compartilhada de autenticacao; o ID na URL nao autentica o usuario.
Os testes `PerfilControllerTest` cobrem API, persistencia das colecoes, ID
compartilhado, atualizacao, duplicidade e entradas invalidas. Dependem da compilacao
completa do projeto e de suas entidades, incluindo Curso.

Validacao desta entrega (01/11/2026): Maven executado com JDK 26.0.2.
A compilacao completa falhou pelas dependencias ausentes `Curso` e `StatusTarefa`;
os testes nao chegaram a executar. Reexecutar `./mvnw.cmd test` apos integrar essas
entregas. A API e a persistencia do perfil ainda aguardam essa validacao integrada.
