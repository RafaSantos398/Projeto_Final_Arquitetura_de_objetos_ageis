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
