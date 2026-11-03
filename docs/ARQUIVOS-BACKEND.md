# Catálogo dos arquivos do backend

Inventário da revisão de 02/11/2026. Caminhos são relativos à raiz do repositório.
As dependências listadas são classes do próprio projeto; dependências Spring/JPA
são explicadas no guia. A listagem inclui classes principais e records internos.

## AreaEstudo

### AreaEstudo

Arquivo: [projeto/src/main/java/com/berxy/projeto/AreaEstudo/AreaEstudo.java](../projeto/src/main/java/com/berxy/projeto/AreaEstudo/AreaEstudo.java).

Entidade do catálogo de áreas; valida nome/descrição e atualiza seu estado.

Atributos/dependências declarados: `Long id`, `String nome`, `String descricao`.

Operações públicas: `atualizarArea`. Getters dão acesso aos dados apresentados no código.

### AreaEstudoController

Arquivo: [projeto/src/main/java/com/berxy/projeto/AreaEstudo/AreaEstudoController.java](../projeto/src/main/java/com/berxy/projeto/AreaEstudo/AreaEstudoController.java).

Expõe as rotas HTTP do recurso e encaminha operações ao service. Trata validação/conflito conforme os handlers declarados.

Atributos/dependências declarados: `AreaEstudoService areaEstudoService`.

Operações públicas: `naoEncontrada`, `dadosInvalidos`, `listar`, `buscar`, `cadastrar`, `atualizar`. Getters dão acesso aos dados apresentados no código.

### AreaEstudoRepository

Arquivo: [projeto/src/main/java/com/berxy/projeto/AreaEstudo/AreaEstudoRepository.java](../projeto/src/main/java/com/berxy/projeto/AreaEstudo/AreaEstudoRepository.java).

Interface de persistência JpaRepository. Spring fornece save, findById, findAll e delete; métodos adicionais derivam consultas pelo nome.

### AreaEstudoRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/AreaEstudo/AreaEstudoRequest.java](../projeto/src/main/java/com/berxy/projeto/AreaEstudo/AreaEstudoRequest.java).

Record de entrada: descreve os dados aceitos no JSON. Não possui identidade de domínio nem persistência própria.

### AreaEstudoService

Arquivo: [projeto/src/main/java/com/berxy/projeto/AreaEstudo/AreaEstudoService.java](../projeto/src/main/java/com/berxy/projeto/AreaEstudo/AreaEstudoService.java).

Coordena consultas, regras e persistência do recurso por meio do repository e das entidades.

Atributos/dependências declarados: `AreaEstudoRepository areaEstudoRepository`.

Operações públicas: `listar`, `buscarPorId`, `cadastrar`, `atualizar`. Getters dão acesso aos dados apresentados no código.

## auth

### AuthController

Arquivo: [projeto/src/main/java/com/berxy/projeto/auth/AuthController.java](../projeto/src/main/java/com/berxy/projeto/auth/AuthController.java).

Rotas públicas de cadastro/login e privadas de consulta/logout; converte credenciais inválidas para 401.

Atributos/dependências declarados: `AuthService service`.

Operações públicas: `cadastrar`, `login`, `usuarioAtual`, `logout`, `credenciaisInvalidas`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `exception.ErroResponse`.

### AuthService

Arquivo: [projeto/src/main/java/com/berxy/projeto/auth/AuthService.java](../projeto/src/main/java/com/berxy/projeto/auth/AuthService.java).

Coordena cadastro, login, consulta da conta e emissão do JWT; não implementa autenticação por cookie.

Atributos/dependências declarados: `UserService usuarios`, `AcessoService acesso`, `JwtService jwt`, `org.springframework.security.crypto.password.PasswordEncoder encoder`.

Operações públicas: `cadastrar`, `consultar`, `login`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `auth.dto.RegisterRequest`, `auth.dto.AuthResponse`, `auth.dto.LoginRequest`, `security.JwtService`, `security.AcessoService`, `user.UserService`, `user.dto.UserResponse`.

### AuthResponse

Arquivo: [projeto/src/main/java/com/berxy/projeto/auth/dto/AuthResponse.java](../projeto/src/main/java/com/berxy/projeto/auth/dto/AuthResponse.java).

Resposta com token e usuario. Cadastro e /me podem devolver token null; somente login emite o Bearer.

Colabora diretamente com: `user.dto.UserResponse`.

### LoginRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/auth/dto/LoginRequest.java](../projeto/src/main/java/com/berxy/projeto/auth/dto/LoginRequest.java).

Entrada de login com email/senha. Não é retornada nem armazenada como texto legível.

### RegisterRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/auth/dto/RegisterRequest.java](../projeto/src/main/java/com/berxy/projeto/auth/dto/RegisterRequest.java).

Entrada de /auth/register; não aceita escolha de ADMIN. Limite UTF-8 validado no service.

## Curso

### Curso

Arquivo: [projeto/src/main/java/com/berxy/projeto/Curso/Curso.java](../projeto/src/main/java/com/berxy/projeto/Curso/Curso.java).

Entidade que pertence a instituição. Valida tipo, idioma, duração e estado ativo; associa modelos reutilizáveis.

Atributos/dependências declarados: `Long id`, `Instituicao instituicao`, `String nome`, `String descricao`, `TipoIntercambio tipoIntercambio`, `String idioma`, `Integer duracaoMeses`, `boolean ativo`, `Set<ModeloTarefa> modelosTarefa`.

Operações públicas: `atualizarConteudo`, `ativar`, `desativar`, `adicionarModeloTarefa`, `removerModeloTarefa`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `Insituicao.Instituicao`, `ModeloTarefa.ModeloTarefa`, `PlanoIntercambio.TipoIntercambio`.

### CursoController

Arquivo: [projeto/src/main/java/com/berxy/projeto/Curso/CursoController.java](../projeto/src/main/java/com/berxy/projeto/Curso/CursoController.java).

Expõe as rotas HTTP do recurso e encaminha operações ao service. Trata validação/conflito conforme os handlers declarados.

Atributos/dependências declarados: `CursoService service`.

Operações públicas: `list`, `buscar`, `salvar`, `atualizar`, `ativar`, `desativar`, `listarModelos`, `associarModelo`, `desassociarModelo`, `tratarValidacao`, `tratarConflito`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `ModeloTarefa.ModeloTarefa`.

### CursoRepository

Arquivo: [projeto/src/main/java/com/berxy/projeto/Curso/CursoRepository.java](../projeto/src/main/java/com/berxy/projeto/Curso/CursoRepository.java).

Interface de persistência JpaRepository. Spring fornece save, findById, findAll e delete; métodos adicionais derivam consultas pelo nome.

Consultas específicas: `List<Curso> findByInstituicaoId(Long instituicaoId)`.

### CursoRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/Curso/CursoRequest.java](../projeto/src/main/java/com/berxy/projeto/Curso/CursoRequest.java).

Record de entrada: descreve os dados aceitos no JSON. Não possui identidade de domínio nem persistência própria.

Colabora diretamente com: `PlanoIntercambio.TipoIntercambio`.

### CursoResponse

Arquivo: [projeto/src/main/java/com/berxy/projeto/Curso/CursoResponse.java](../projeto/src/main/java/com/berxy/projeto/Curso/CursoResponse.java).

Record de saída: recorta campos para JSON, evitando ciclos e exposição de objetos internos.

Operações públicas: `de`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `PlanoIntercambio.TipoIntercambio`.

### CursoService

Arquivo: [projeto/src/main/java/com/berxy/projeto/Curso/CursoService.java](../projeto/src/main/java/com/berxy/projeto/Curso/CursoService.java).

Busca instituição e modelos e coordena cadastro, edição, ativação e associação de modelos a cursos.

Atributos/dependências declarados: `CursoRepository repository`, `InstituicaoRepository instituicaoRepository`, `ModeloTarefaRepository modeloTarefaRepository`.

Operações públicas: `list`, `buscar`, `adicionarCurso`, `atualizarCurso`, `ativarCurso`, `desativarCurso`, `listarModelosAtivos`, `associarModelo`, `desassociarModelo`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `Insituicao.Instituicao`, `Insituicao.InstituicaoRepository`, `ModeloTarefa.ModeloTarefa`, `ModeloTarefa.ModeloTarefaRepository`.

## exception

### CampoInvalidoException

Arquivo: [projeto/src/main/java/com/berxy/projeto/exception/CampoInvalidoException.java](../projeto/src/main/java/com/berxy/projeto/exception/CampoInvalidoException.java).

Exceção com campo e mensagem, usada pelo limite de senha em UTF-8. Não contém a senha recebida.

Atributos/dependências declarados: `String campo`.

### ErroResponse

Arquivo: [projeto/src/main/java/com/berxy/projeto/exception/ErroResponse.java](../projeto/src/main/java/com/berxy/projeto/exception/ErroResponse.java).

DTO simples com status, detail e mapa errors. Compatível com os campos consumidos pelo cliente HTTP.

### GlobalExceptionHandler

Arquivo: [projeto/src/main/java/com/berxy/projeto/exception/GlobalExceptionHandler.java](../projeto/src/main/java/com/berxy/projeto/exception/GlobalExceptionHandler.java).

Traduz exceções para HTTP e DTO ErroResponse com @ControllerAdvice/@ExceptionHandler. Não expõe detalhes internos do banco.

Operações públicas: `naoEncontrado`, `emailEmUso`, `senhaIncorreta`, `campoInvalido`, `conflitoNoBanco`, `dadosInvalidos`, `formatoInvalido`, `rotaInexistente`, `statusDoServico`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `user.EmailAlreadyInUseException`, `user.InvalidPasswordException`, `user.UserNotFoundException`.

## Insituicao

### Instituicao

Arquivo: [projeto/src/main/java/com/berxy/projeto/Insituicao/Instituicao.java](../projeto/src/main/java/com/berxy/projeto/Insituicao/Instituicao.java).

Entidade de instituição: nome obrigatório e descrição/site opcionais, com validação de URL HTTP/HTTPS.

Atributos/dependências declarados: `Long id`, `String nome`, `String descricao`, `String siteOficial`.

Operações públicas: `atualizarConteudo`. Getters dão acesso aos dados apresentados no código.

### InstituicaoController

Arquivo: [projeto/src/main/java/com/berxy/projeto/Insituicao/InstituicaoController.java](../projeto/src/main/java/com/berxy/projeto/Insituicao/InstituicaoController.java).

Expõe as rotas HTTP do recurso e encaminha operações ao service. Trata validação/conflito conforme os handlers declarados.

Atributos/dependências declarados: `InstituicaoService service`.

Operações públicas: `list`, `buscar`, `salvar`, `atualizar`, `tratarValidacao`. Getters dão acesso aos dados apresentados no código.

### InstituicaoRepository

Arquivo: [projeto/src/main/java/com/berxy/projeto/Insituicao/InstituicaoRepository.java](../projeto/src/main/java/com/berxy/projeto/Insituicao/InstituicaoRepository.java).

Interface de persistência JpaRepository. Spring fornece save, findById, findAll e delete; métodos adicionais derivam consultas pelo nome.

### InstituicaoRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/Insituicao/InstituicaoRequest.java](../projeto/src/main/java/com/berxy/projeto/Insituicao/InstituicaoRequest.java).

Record de entrada: descreve os dados aceitos no JSON. Não possui identidade de domínio nem persistência própria.

### InstituicaoService

Arquivo: [projeto/src/main/java/com/berxy/projeto/Insituicao/InstituicaoService.java](../projeto/src/main/java/com/berxy/projeto/Insituicao/InstituicaoService.java).

Coordena consultas, regras e persistência do recurso por meio do repository e das entidades.

Atributos/dependências declarados: `InstituicaoRepository repository`.

Operações públicas: `list`, `buscar`, `adicionarInstituicao`, `atualizarInstituicao`. Getters dão acesso aos dados apresentados no código.

## ModeloTarefa

### CategoriaTarefa

Arquivo: [projeto/src/main/java/com/berxy/projeto/ModeloTarefa/CategoriaTarefa.java](../projeto/src/main/java/com/berxy/projeto/ModeloTarefa/CategoriaTarefa.java).

Enum com valores permitidos para este aspecto do domínio; Spring recebe o nome do valor no JSON.

Valores: `DOCUMENTACAO, IDIOMA, FINANCEIRO, INSCRICAO, OUTROS`.

### ModeloTarefa

Arquivo: [projeto/src/main/java/com/berxy/projeto/ModeloTarefa/ModeloTarefa.java](../projeto/src/main/java/com/berxy/projeto/ModeloTarefa/ModeloTarefa.java).

Modelo reutilizável com título, descrição, categoria e ativo. Editar o modelo não altera cópias já geradas no plano.

Atributos/dependências declarados: `Long id`, `String titulo`, `String descricao`, `CategoriaTarefa categoria`, `boolean ativo`.

Operações públicas: `atualizarConteudo`, `ativar`, `desativar`. Getters dão acesso aos dados apresentados no código.

### ModeloTarefaController

Arquivo: [projeto/src/main/java/com/berxy/projeto/ModeloTarefa/ModeloTarefaController.java](../projeto/src/main/java/com/berxy/projeto/ModeloTarefa/ModeloTarefaController.java).

Expõe as rotas HTTP do recurso e encaminha operações ao service. Trata validação/conflito conforme os handlers declarados.

Atributos/dependências declarados: `ModeloTarefaService modeloTarefaService`.

Operações públicas: `dadosInvalidos`, `list`, `salvar`, `buscar`, `Atualizar`, `desativar`. Getters dão acesso aos dados apresentados no código.

### ModeloTarefaRepository

Arquivo: [projeto/src/main/java/com/berxy/projeto/ModeloTarefa/ModeloTarefaRepository.java](../projeto/src/main/java/com/berxy/projeto/ModeloTarefa/ModeloTarefaRepository.java).

Interface de persistência JpaRepository. Spring fornece save, findById, findAll e delete; métodos adicionais derivam consultas pelo nome.

### ModeloTarefaRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/ModeloTarefa/ModeloTarefaRequest.java](../projeto/src/main/java/com/berxy/projeto/ModeloTarefa/ModeloTarefaRequest.java).

Record de entrada: descreve os dados aceitos no JSON. Não possui identidade de domínio nem persistência própria.

### ModeloTarefaService

Arquivo: [projeto/src/main/java/com/berxy/projeto/ModeloTarefa/ModeloTarefaService.java](../projeto/src/main/java/com/berxy/projeto/ModeloTarefa/ModeloTarefaService.java).

Coordena cadastro, busca, atualização e desativação de modelos. Mantém o padrão inicial de ModeloTarefa.

Atributos/dependências declarados: `ModeloTarefaRepository modeloTarefaRepository`.

Operações públicas: `list`, `adicionarModelo`, `GetByid`, `atualizarModelo`, `destivarModelo`. Getters dão acesso aos dados apresentados no código.

## perfilIntercambista

### NivelEscolaridade

Arquivo: [projeto/src/main/java/com/berxy/projeto/perfilIntercambista/NivelEscolaridade.java](../projeto/src/main/java/com/berxy/projeto/perfilIntercambista/NivelEscolaridade.java).

Enum com valores permitidos para este aspecto do domínio; Spring recebe o nome do valor no JSON.

Valores: `ENSINO_FUNDAMENTAL, ENSINO_MEDIO, GRADUACAO, POS_GRADUACAO, MESTRADO, DOUTORADO`.

### PerfilController

Arquivo: [projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilController.java](../projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilController.java).

Expõe as rotas HTTP do recurso e encaminha operações ao service. Trata validação/conflito conforme os handlers declarados.

Atributos/dependências declarados: `PerfilService service`.

Operações públicas: `cadastrar`, `consultar`, `atualizar`, `dadosInvalidos`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `exception.ErroResponse`.

### PerfilINtercambista

Arquivo: [projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilINtercambista.java](../projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilINtercambista.java).

Entidade do perfil com ID compartilhado com User. Valida nascimento passado, países, escolaridade e idiomas sem repetição.

Atributos/dependências declarados: `Long id`, `User user`, `LocalDate nascimento`, `Set<String> nacionalidades`, `String paisResidencia`, `List<ProficienciaIdioma> proficienciaIdioma`, `SituacaoEscolaridade situacaoEscolaridade`, `NivelEscolaridade nivelEscolaridade`.

Operações públicas: `atualizar`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `user.User`.

### PerfilRepository

Arquivo: [projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilRepository.java](../projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilRepository.java).

Interface de persistência JpaRepository. Spring fornece save, findById, findAll e delete; métodos adicionais derivam consultas pelo nome.

### PerfilRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilRequest.java](../projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilRequest.java).

Record de entrada: descreve os dados aceitos no JSON. Não possui identidade de domínio nem persistência própria.

Operações públicas: `ProficienciaRequest`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `PreparacaoIdioma.NivelProficiencia`.

### PerfilResponse

Arquivo: [projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilResponse.java](../projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilResponse.java).

Record de saída: recorta campos para JSON, evitando ciclos e exposição de objetos internos.

Operações públicas: `from`. Getters dão acesso aos dados apresentados no código.

### PerfilService

Arquivo: [projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilService.java](../projeto/src/main/java/com/berxy/projeto/perfilIntercambista/PerfilService.java).

Coordena perfil único por usuário, consulta a conta e salva alterações; mantém transação para coleções de valores.

Atributos/dependências declarados: `PerfilRepository repository`, `UserService users`.

Operações públicas: `consultar`, `cadastrar`, `atualizar`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `user.UserService`.

### ProficienciaIdioma

Arquivo: [projeto/src/main/java/com/berxy/projeto/perfilIntercambista/ProficienciaIdioma.java](../projeto/src/main/java/com/berxy/projeto/perfilIntercambista/ProficienciaIdioma.java).

Objeto de valor Embeddable com código de idioma e nível. Compõe o perfil; não tem repository nem ID próprios.

Atributos/dependências declarados: `String idioma`, `NivelProficiencia nivel`.

Colabora diretamente com: `PreparacaoIdioma.NivelProficiencia`.

### SituacaoEscolaridade

Arquivo: [projeto/src/main/java/com/berxy/projeto/perfilIntercambista/SituacaoEscolaridade.java](../projeto/src/main/java/com/berxy/projeto/perfilIntercambista/SituacaoEscolaridade.java).

Enum com valores permitidos para este aspecto do domínio; Spring recebe o nome do valor no JSON.

Valores: `CURSANDO, CONCLUIDO, INTERROMPIDO`.

## PlanoIntercambio

### PlanejamentoFinanceiro

Arquivo: [projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanejamentoFinanceiro.java](../projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanejamentoFinanceiro.java).

Record de resultado e cálculo monetário com BigDecimal. de calcula restante, meses, aporte necessário e situação; Request recebe os três valores. Não é entidade/tabela.

Operações públicas: `Request`, `de`. Getters dão acesso aos dados apresentados no código.

### PlanoCursoRepository

Arquivo: [projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoCursoRepository.java](../projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoCursoRepository.java).

Interface auxiliar para consultar Curso no módulo de planos. Acessa a mesma tabela de CursoRepository.

Colabora diretamente com: `Curso.Curso`.

### PlanoIntercambio

Arquivo: [projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambio.java](../projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambio.java).

Entidade central: dono, curso opcional, áreas, tarefas, preparações, status e finanças. Valida curso compatível e impede troca após tarefas de curso.

Atributos/dependências declarados: `Long id`, `User user`, `String titulo`, `TipoIntercambio tipoIntercambio`, `BigDecimal orcamentoTotal`, `BigDecimal valorGuardado`, `BigDecimal aporteMensal`, `String moeda`, `LocalDate inicioPrevisto`, `Integer duracaoMeses`, `Boolean dependeBolsa`, `StatusPlano status`, `Curso curso`, `Set<AreaEstudo> areasInteresse`, `List<PreparacaoIdioma> preparacoesIdioma`, `List<TarefaPlano> tarefas`.

Operações públicas: `atualizarIdentificacao`, `atualizarPlanejamento`, `alterarStatus`, `atualizarFinancas`, `selecionarCurso`, `adicionarAreaInteresse`, `removerAreaInteresse`, `gerarTarefa`, `adicionarTarefa`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `AreaEstudo.AreaEstudo`, `Curso.Curso`, `ModeloTarefa.ModeloTarefa`, `PreparacaoIdioma.PreparacaoIdioma`, `TarefaPlano.TarefaPlano`, `user.User`.

### PlanoIntercambioController

Arquivo: [projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambioController.java](../projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambioController.java).

Expõe as rotas HTTP do recurso e encaminha operações ao service. Trata validação/conflito conforme os handlers declarados.

Atributos/dependências declarados: `PlanoIntercambioService planoIntercambioService`, `com.berxy.projeto.security.AcessoService acesso`.

Operações públicas: `consultarFinancas`, `atualizarFinancas`, `list`, `salvar`, `buscar`, `atualizar`, `selecionarCurso`, `alterarStatus`, `tratarDadosInvalidos`, `tratarConflito`. Getters dão acesso aos dados apresentados no código.

### PlanoIntercambioRepository

Arquivo: [projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambioRepository.java](../projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambioRepository.java).

Interface de persistência JpaRepository. Spring fornece save, findById, findAll e delete; métodos adicionais derivam consultas pelo nome.

Consultas específicas: `java.util.List<PlanoIntercambio> findByUserIdOrderByIdAsc(Long usuarioId)`.

### PlanoIntercambioRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambioRequest.java](../projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambioRequest.java).

Record de entrada: descreve os dados aceitos no JSON. Não possui identidade de domínio nem persistência própria.

Operações públicas: `CursoRequest`, `StatusRequest`. Getters dão acesso aos dados apresentados no código.

### PlanoIntercambioResponse

Arquivo: [projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambioResponse.java](../projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambioResponse.java).

Record de saída: recorta campos para JSON, evitando ciclos e exposição de objetos internos.

Operações públicas: `de`. Getters dão acesso aos dados apresentados no código.

### PlanoIntercambioService

Arquivo: [projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambioService.java](../projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoIntercambioService.java).

Consulta/lista planos, liga usuário/curso e coordena atualização de planejamento, status e finanças.

Atributos/dependências declarados: `PlanoIntercambioRepository planoIntercambioRepository`, `PlanoUsuarioRepository planoUsuarioRepository`, `PlanoCursoRepository planoCursoRepository`.

Operações públicas: `list`, `listarDoUsuario`, `GetByid`, `adicionarPlano`, `atualizarPlano`, `selecionarCurso`, `alterarStatus`, `consultarFinancas`, `atualizarFinancas`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `Curso.Curso`, `user.User`.

### PlanoUsuarioRepository

Arquivo: [projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoUsuarioRepository.java](../projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/PlanoUsuarioRepository.java).

Interface auxiliar para consultar User no módulo de planos. Acessa a mesma tabela de UserRepository; não cria uma nova entidade.

Colabora diretamente com: `user.User`.

### StatusPlano

Arquivo: [projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/StatusPlano.java](../projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/StatusPlano.java).

Enum com valores permitidos para este aspecto do domínio; Spring recebe o nome do valor no JSON.

Valores: `RASCUNHO, EM_ANDAMENTO, CONCLUIDO, CANCELADO`.

### TipoIntercambio

Arquivo: [projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/TipoIntercambio.java](../projeto/src/main/java/com/berxy/projeto/PlanoIntercambio/TipoIntercambio.java).

Enum com valores permitidos para este aspecto do domínio; Spring recebe o nome do valor no JSON.

Valores: `GRADUACAO, POS_GRADUACAO, CURSO_IDIOMA, ENSINO_MEDIO, TRABALHO`.

## PreparacaoIdioma

### NivelProficiencia

Arquivo: [projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/NivelProficiencia.java](../projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/NivelProficiencia.java).

Enum com valores permitidos para este aspecto do domínio; Spring recebe o nome do valor no JSON.

Valores: `A1, A2, B1, B2, C1, C2`.

### PreparacaoIdioma

Arquivo: [projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdioma.java](../projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdioma.java).

Entidade de meta de estudo vinculada ao plano: idioma, nível-alvo, prazo, minutos e status. Edição mantém o progresso existente.

Atributos/dependências declarados: `Long id`, `PlanoIntercambio planoIntercambio`, `String idioma`, `NivelProficiencia nivelAlvo`, `LocalDate dataMeta`, `Integer minutosSemanaisPlanejados`, `StatusPreparacao status`.

Operações públicas: `atualizarConteudo`, `iniciar`, `concluir`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `PlanoIntercambio.PlanoIntercambio`.

### PreparacaoIdiomaController

Arquivo: [projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdiomaController.java](../projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdiomaController.java).

Expõe as rotas HTTP do recurso e encaminha operações ao service. Trata validação/conflito conforme os handlers declarados.

Atributos/dependências declarados: `PreparacaoIdiomaService service`.

Operações públicas: `list`, `buscar`, `salvar`, `atualizar`, `iniciar`, `concluir`, `tratarValidacao`, `tratarConflito`. Getters dão acesso aos dados apresentados no código.

### PreparacaoIdiomaRepository

Arquivo: [projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdiomaRepository.java](../projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdiomaRepository.java).

Interface de persistência JpaRepository. Spring fornece save, findById, findAll e delete; métodos adicionais derivam consultas pelo nome.

Consultas específicas: `List<PreparacaoIdioma> findByPlanoIntercambioId(Long planoId)`; `Optional<PreparacaoIdioma> findByIdAndPlanoIntercambioId(Long id, Long planoId)`.

### PreparacaoIdiomaRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdiomaRequest.java](../projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdiomaRequest.java).

Record de entrada: descreve os dados aceitos no JSON. Não possui identidade de domínio nem persistência própria.

### PreparacaoIdiomaResponse

Arquivo: [projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdiomaResponse.java](../projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdiomaResponse.java).

Record de saída: recorta campos para JSON, evitando ciclos e exposição de objetos internos.

Operações públicas: `de`. Getters dão acesso aos dados apresentados no código.

### PreparacaoIdiomaService

Arquivo: [projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdiomaService.java](../projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/PreparacaoIdiomaService.java).

Coordena metas de idioma e impede duas preparações em aberto do mesmo idioma no plano.

Atributos/dependências declarados: `PreparacaoIdiomaRepository repository`, `PlanoIntercambioRepository planoRepository`.

Operações públicas: `list`, `buscar`, `adicionarPreparacao`, `atualizarPreparacao`, `iniciar`, `concluir`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `PlanoIntercambio.PlanoIntercambio`, `PlanoIntercambio.PlanoIntercambioRepository`.

### StatusPreparacao

Arquivo: [projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/StatusPreparacao.java](../projeto/src/main/java/com/berxy/projeto/PreparacaoIdioma/StatusPreparacao.java).

Enum com valores permitidos para este aspecto do domínio; Spring recebe o nome do valor no JSON.

Valores: `NAO_INICIADA, EM_ANDAMENTO, CONCLUIDA`.

## Inicialização

### ProjetoApplication

Arquivo: [projeto/src/main/java/com/berxy/projeto/ProjetoApplication.java](../projeto/src/main/java/com/berxy/projeto/ProjetoApplication.java).

Ponto de entrada: inicia o contexto Spring e a descoberta dos componentes.

Operações públicas: `main`. Getters dão acesso aos dados apresentados no código.

## security

### AcessoService

Arquivo: [projeto/src/main/java/com/berxy/projeto/security/AcessoService.java](../projeto/src/main/java/com/berxy/projeto/security/AcessoService.java).

Autoriza por papel e proprietário a partir da rota HTTP; consulta User e PlanoIntercambio. Não confiar somente na visibilidade dos botões.

Atributos/dependências declarados: `UserRepository usuarios`, `PlanoIntercambioRepository planos`.

Operações públicas: `usuarioAtual`, `exigirUsuario`, `permitir`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `PlanoIntercambio.PlanoIntercambioRepository`, `user.Role`, `user.User`, `user.UserRepository`.

### JwtAuthenticationFilter

Arquivo: [projeto/src/main/java/com/berxy/projeto/security/JwtAuthenticationFilter.java](../projeto/src/main/java/com/berxy/projeto/security/JwtAuthenticationFilter.java).

Lê Bearer, valida JWT, carrega UserDetails, preenche SecurityContext e consulta AcessoService antes do controller.

Atributos/dependências declarados: `JwtService jwt`, `UserService usuarios`, `AcessoService acesso`.

Colabora diretamente com: `user.UserService`.

### JwtService

Arquivo: [projeto/src/main/java/com/berxy/projeto/security/JwtService.java](../projeto/src/main/java/com/berxy/projeto/security/JwtService.java).

Gera, verifica assinatura/expiração e extrai subject de JWT; lê segredo e validade do ambiente.

Atributos/dependências declarados: `SecretKey chave`, `long expiracao`.

Operações públicas: `generateToken`, `extractUsername`. Getters dão acesso aos dados apresentados no código.

### SecurityConfig

Arquivo: [projeto/src/main/java/com/berxy/projeto/security/SecurityConfig.java](../projeto/src/main/java/com/berxy/projeto/security/SecurityConfig.java).

Configura BCrypt, filtro JWT, rotas públicas, erros 401/403 e CORS. Console H2 é liberado somente em dev.

Operações públicas: `passwordEncoder`, `corsConfigurationSource`, `securityFilterChain`. Getters dão acesso aos dados apresentados no código.

## TarefaPlano

### AnotacoesTarefaResponse

Arquivo: [projeto/src/main/java/com/berxy/projeto/TarefaPlano/AnotacoesTarefaResponse.java](../projeto/src/main/java/com/berxy/projeto/TarefaPlano/AnotacoesTarefaResponse.java).

DTO separado do caderno com tarefaId, texto e atualizadoEm; evita enviar textos longos na listagem do quadro.

Operações públicas: `de`. Getters dão acesso aos dados apresentados no código.

### StatusTarefa

Arquivo: [projeto/src/main/java/com/berxy/projeto/TarefaPlano/StatusTarefa.java](../projeto/src/main/java/com/berxy/projeto/TarefaPlano/StatusTarefa.java).

Enum com valores permitidos para este aspecto do domínio; Spring recebe o nome do valor no JSON.

Valores: `PENDENTE, EM_ANDAMENTO, CONCLUIDA`.

### TarefaPlano

Arquivo: [projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlano.java](../projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlano.java).

Entidade da tarefa real: guarda cópia do conteúdo, origem opcional, prazo, status, conclusão e anotações. Preserva caderno ao mudar status.

Atributos/dependências declarados: `Long id`, `PlanoIntercambio plano`, `ModeloTarefa modeloOrigem`, `CategoriaTarefa categoria`, `TemaChecklist tema`, `String chaveSugestao`, `String titulo`, `String descricao`, `StatusTarefa status`, `LocalDate prazo`, `Instant concluidaEm`, `String anotacoes`, `Instant anotacoesAtualizadasEm`.

Operações públicas: `atualizarAnotacoes`, `sugerida`, `atualizarConteudo`, `iniciar`, `concluir`, `reabrir`, `alterarPrazo`, `moverPara`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `ModeloTarefa.ModeloTarefa`, `ModeloTarefa.CategoriaTarefa`, `PlanoIntercambio.PlanoIntercambio`.

### TarefaPlanoController

Arquivo: [projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlanoController.java](../projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlanoController.java).

Expõe as rotas HTTP do recurso e encaminha operações ao service. Trata validação/conflito conforme os handlers declarados.

Atributos/dependências declarados: `TarefaPlanoService tarefaPlanoService`.

Operações públicas: `anotacoes`, `salvarAnotacoes`, `mover`, `temas`, `gerarTemas`, `criar`, `atualizar`, `remover`, `list`, `buscar`, `gerar`, `gerarChecklist`, `iniciar`, `concluir`, `reabrir`, `alterarPrazo`, `tratarDadosInvalidos`, `tratarConflito`. Getters dão acesso aos dados apresentados no código.

### TarefaPlanoRepository

Arquivo: [projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlanoRepository.java](../projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlanoRepository.java).

Interface de persistência JpaRepository. Spring fornece save, findById, findAll e delete; métodos adicionais derivam consultas pelo nome.

Consultas específicas: `boolean existsByPlanoIdAndChaveSugestao(Long planoId, String chaveSugestao)`; `List<TarefaPlano> findByPlanoIdOrderByIdAsc(Long planoId)`; `Optional<TarefaPlano> findByIdAndPlanoId(Long id, Long planoId)`; `Optional<TarefaPlano> findByPlanoIdAndModeloOrigemId(Long planoId, Long modeloOrigemId)`.

### TarefaPlanoRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlanoRequest.java](../projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlanoRequest.java).

Record de entrada: descreve os dados aceitos no JSON. Não possui identidade de domínio nem persistência própria.

Operações públicas: `AnotacoesRequest`, `StatusRequest`, `ConteudoRequest`, `TemasRequest`, `PrazoRequest`. Getters dão acesso aos dados apresentados no código.

### TarefaPlanoResponse

Arquivo: [projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlanoResponse.java](../projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlanoResponse.java).

Record de saída: recorta campos para JSON, evitando ciclos e exposição de objetos internos.

Operações públicas: `de`. Getters dão acesso aos dados apresentados no código.

### TarefaPlanoService

Arquivo: [projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlanoService.java](../projeto/src/main/java/com/berxy/projeto/TarefaPlano/TarefaPlanoService.java).

Verifica plano e tarefa, gera itens sem recriar os existentes e coordena alterações. Unicidade no banco impede duplicação concorrente; pode haver 409.

Atributos/dependências declarados: `TarefaPlanoRepository tarefaPlanoRepository`, `PlanoIntercambioService planoIntercambioService`, `ModeloTarefaRepository modeloTarefaRepository`, `CursoService cursoService`.

Operações públicas: `list`, `temas`, `gerarTemas`, `criar`, `atualizar`, `remover`, `GetByid`, `atualizarAnotacoes`, `gerarTarefa`, `gerarChecklist`, `iniciar`, `mover`, `concluir`, `reabrir`, `alterarPrazo`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `Curso.CursoService`, `ModeloTarefa.ModeloTarefa`, `ModeloTarefa.ModeloTarefaRepository`, `PlanoIntercambio.PlanoIntercambio`, `PlanoIntercambio.PlanoIntercambioService`.

### TemaChecklist

Arquivo: [projeto/src/main/java/com/berxy/projeto/TarefaPlano/TemaChecklist.java](../projeto/src/main/java/com/berxy/projeto/TarefaPlano/TemaChecklist.java).

Enum com seis temas e quatro sugestões por tema. Sugestao e Resposta são records internos. Chaves estáveis identificam sugestões; não são requisitos legais de destinos.

Atributos/dependências declarados: `String titulo`, `CategoriaTarefa categoria`, `List<Sugestao> sugestoes`.

Operações públicas: `Sugestao`, `Resposta`, `de`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `ModeloTarefa.CategoriaTarefa`.

## user

### ChangePasswordRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/dto/ChangePasswordRequest.java](../projeto/src/main/java/com/berxy/projeto/user/dto/ChangePasswordRequest.java).

Recebe senha atual e nova; ambas têm limite UTF-8 validado no service, e nova tem tamanho mínimo na entrada.

### CreateUserRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/dto/CreateUserRequest.java](../projeto/src/main/java/com/berxy/projeto/user/dto/CreateUserRequest.java).

Entrada de cadastro com NotBlank, Email e Size; limite em bytes é validado em UserService.

### UpdateUserRequest

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/dto/UpdateUserRequest.java](../projeto/src/main/java/com/berxy/projeto/user/dto/UpdateUserRequest.java).

Entrada de alteração de nome/email; não altera papel, hash ou ativação.

### UserResponse

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/dto/UserResponse.java](../projeto/src/main/java/com/berxy/projeto/user/dto/UserResponse.java).

DTO público da conta sem senha ou hash. from converte User para a resposta.

Operações públicas: `from`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `user.Role`, `user.User`.

### EmailAlreadyInUseException

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/EmailAlreadyInUseException.java](../projeto/src/main/java/com/berxy/projeto/user/EmailAlreadyInUseException.java).

Indica email reservado por uma conta, inclusive desativada, traduzido para 409.

### InvalidPasswordException

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/InvalidPasswordException.java](../projeto/src/main/java/com/berxy/projeto/user/InvalidPasswordException.java).

Indica senha atual incorreta em troca de senha, traduzido para 400.

### Role

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/Role.java](../projeto/src/main/java/com/berxy/projeto/user/Role.java).

Enum USER/ADMIN; define papéis, não tipos de intercâmbio.

Valores: `USER, ADMIN`.

### User

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/User.java](../projeto/src/main/java/com/berxy/projeto/user/User.java).

Entidade da conta: identidade, email único, hash, papel, ativação e timestamps. Nunca retornar diretamente em respostas da conta.

Atributos/dependências declarados: `Long id`, `String nome`, `String email`, `String passwordHash`, `Role role`, `boolean active`, `Instant createdAt`, `Instant updatedAt`.

Operações públicas: `setNome`, `setEmail`, `setPasswordHash`, `setRole`, `activate`, `deactivate`. Getters dão acesso aos dados apresentados no código.

### UserController

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/UserController.java](../projeto/src/main/java/com/berxy/projeto/user/UserController.java).

Expõe as rotas HTTP do recurso e encaminha operações ao service. Trata validação/conflito conforme os handlers declarados.

Atributos/dependências declarados: `UserService userService`.

Operações públicas: `create`, `findAll`, `findById`, `update`, `changePassword`, `activate`, `deactivate`. Getters dão acesso aos dados apresentados no código.

Colabora diretamente com: `user.dto.ChangePasswordRequest`, `user.dto.CreateUserRequest`, `user.dto.UpdateUserRequest`, `user.dto.UserResponse`.

### UserNotFoundException

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/UserNotFoundException.java](../projeto/src/main/java/com/berxy/projeto/user/UserNotFoundException.java).

Indica usuário ausente ou desativado nas consultas, traduzido para 404.

### UserRepository

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/UserRepository.java](../projeto/src/main/java/com/berxy/projeto/user/UserRepository.java).

Interface de persistência JpaRepository. Spring fornece save, findById, findAll e delete; métodos adicionais derivam consultas pelo nome.

Consultas específicas: `boolean existsByEmail(String email)`; `Optional<User> findByEmail(String email)`; `List<User> findAllByActiveTrue()`.

### UserService

Arquivo: [projeto/src/main/java/com/berxy/projeto/user/UserService.java](../projeto/src/main/java/com/berxy/projeto/user/UserService.java).

Normaliza email, verifica duplicidade, usa BCrypt, altera/desativa contas e implementa UserDetailsService. Valida o limite de 72 bytes da senha com if.

Atributos/dependências declarados: `UserRepository userRepository`, `PasswordEncoder passwordEncoder`.

Operações públicas: `create`, `findById`, `findByEmail`, `findAll`, `loadUserByUsername`, `update`, `changePassword`, `deactivate`, `activate`, `validarLimiteSenha`. Getters dão acesso aos dados apresentados no código.

## Configuração, operação e arquivos auxiliares

| Arquivo | Responsabilidade |
|---|---|
| README.md | Entrada do projeto, funcionalidades atuais e índice da documentação |
| DEPLOY-RENDER.md | Procedimento e configuração do serviço já existente |
| render.yaml | Referência para implantação Docker; não cria automaticamente um segundo serviço |
| projeto/pom.xml | Versões Java/Spring, dependências e plugin de empacotamento Maven |
| projeto/mvnw | Maven Wrapper para Linux/macOS; usado no Docker |
| projeto/mvnw.cmd | Maven Wrapper para Windows |
| projeto/.mvn/wrapper/maven-wrapper.properties | Versão/endereço da distribuição Maven do wrapper |
| projeto/Dockerfile | Build Java 26, testes no H2 e imagem final JRE com usuário sem privilégios |
| projeto/.dockerignore | Limita o contexto aos arquivos necessários ao build |
| projeto/.gitignore | Ignora builds, dados locais e arquivos que não devem ser versionados |
| projeto/.gitattributes | Regras de finais de linha do repositório |
| projeto/src/main/resources/application.properties | Nome, porta PORT, JWT, CORS, dev padrão e open-in-view=false |
| projeto/src/main/resources/application-dev.properties | H2 temporário, console local e create-drop |
| projeto/src/main/resources/application-prod.properties | PostgreSQL, segredos do ambiente e validate |
| projeto/database/schema-postgresql.sql | Esquema completo para banco novo; não aplicar sobre tabelas já existentes |
| projeto/database/migrations/20261102-checklists-financas.sql | Migração aditiva de temas, tarefas sem modelo e valores financeiros |
| projeto/database/migrations/20261102-anotacoes-tarefas.sql | Dois campos opcionais para texto e atualização do caderno |
| projeto/database/README.md | Inicialização, migrações e cuidados de validação PostgreSQL |
| projeto/scripts/inicializar-postgres.ps1 | Cria esquema somente se vazio e valida contexto de produção |
| projeto/scripts/testar-postgres.ps1 | Testa em schema temporário codex_test_, preservando public |
| projeto/scripts/atualizar-checklists-postgres.ps1 | Aplica somente a migração de checklists/finanças com senha local |
| projeto/HELP.md | Ajuda local gerada pelo Spring Initializr; não é o manual do sistema |

Os arquivos `.idea/.gitignore`, `compiler.xml`, `encodings.xml`, `jarRepositories.xml`,
`misc.xml`, `modules.xml`, `vcs.xml`, `projeto.iml` e
`Projeto_Final_Arquitetura_de_objetos_ageis.iml` são configurações do IntelliJ.
Não implementam regras nem participam da imagem Docker; não depender deles para executar.

Os documentos em docs/ têm funções descritas no índice do README. Diagramas `.mmd`
são fontes editáveis; diagramas Mermaid também estão embutidos em DIAGRAMAS.md.

## Testes

Todos os caminhos abaixo começam em `projeto/src/test/java/com/berxy/projeto/`.

| Arquivo | Evidência que oferece |
|---|---|
| AutenticacaoTeste.java | Helper de JWT/ADMIN e proteção que exige H2 em memória ou schema PostgreSQL temporário |
| ProjetoApplicationTests.java | Contexto Spring carrega com a configuração do teste |
| FluxosPlanejamentoTest.java | Planos, curso, associação, tarefas, temas, anotações, transições, prazos e isolamento |
| PlanejamentoFinanceiroTest.java | Arredondamento, prazo, ausência de aporte, meta atingida e divisão por zero |
| Insituicao/InstituicaoTest.java | Validação e atualização atômica de dados de instituição |
| auth/AuthControllerTest.java | Login JWT, tokens inválidos/expirados, CORS, papéis, dono e UTF-8 das senhas |
| user/UserControllerTest.java | Cadastro, duplicidade, troca de senha, limites e desativação/reativação |
| perfilIntercambista/PerfilControllerTest.java | Perfil único, validação, proficiências e respostas sem vazamento da conta |

`@Test`, MockMvc e JUnit são ferramentas de verificação; não são novas camadas da API.
Não executar testes com limpeza de dados sobre o schema public de produção.


## Pacote de documentação

`docs/berxy-documentacao-grupo.zip` reúne README e os documentos/diagramas para enviar
ao grupo. `docs/GUIA-FRONTEND.md` é uma cópia do guia do repositório de frontend,
incluída para permitir leitura conjunta. Ao mudar o frontend, atualizar também essa
cópia antes de refazer o pacote. O código-fonte permanece nos dois repositórios.


## Atualização de cursos pessoais em 03/11/2026

A implementação agora tem 86 arquivos Java de produção. Complementos aos arquivos acima:

| Arquivo | Responsabilidade atualizada |
|---|---|
| projeto/src/main/java/com/berxy/projeto/Curso/EtiquetaCurso.java | Enum de contribuição e conversão para categoria da tarefa |
| projeto/src/main/java/com/berxy/projeto/Curso/CursoPessoalRequest.java | Record de nome, descrição, instituição livre, idioma, datas, link e etiquetas |
| Curso/Curso.java | Acrescenta proprietário User, instituição em texto, etiquetas internas, início/término e link; fábrica pessoal e atualizarPessoal validam antes de mudar estado |
| Curso/CursoService.java | criarPessoal/atualizarPessoal usam o usuário autenticado; list/buscar restringem dados pessoais ao dono |
| Curso/CursoController.java | Acrescenta POST /pessoais e PUT /pessoais/{id}; mantém rotas administrativas do catálogo |
| Curso/CursoResponse.java | Inclui pessoal, etiquetas, início, término e link; trata instituição null |
| security/AcessoService.java | Permite criação/edição pessoal autenticada; propriedade é conferida pelo service |
| TarefaPlano/TarefaPlano.java | Acrescenta cursoApoio e fábrica deCurso; exige mesmo dono e cria etapa com prazo inicial |
| TarefaPlano/TarefaPlanoService.java | agendarCurso valida acesso e retorna etapa existente sem reiniciar progresso |
| TarefaPlano/TarefaPlanoRepository.java | Consulta findByPlanoIdAndCursoApoioId |
| TarefaPlano/TarefaPlanoController.java | POST /curso dentro das tarefas do plano |
| TarefaPlano/TarefaPlanoRequest.java | CursoRequest interno com cursoId |
| TarefaPlano/TarefaPlanoResponse.java | Acrescenta cursoApoioId/nome; curso de apoio não é marcado como tarefa própria sem origem |
| PlanoIntercambio/PlanoIntercambio.java | Impede usar curso de apoio como curso principal do intercâmbio |
| projeto/database/migrations/20261103-cursos-pessoais.sql | Acrescenta colunas e FKs; permite instituição/tipo null no curso pessoal e unicidade da etapa por plano/curso |

FluxosPlanejamentoTest inclui isolamento entre donos, etiquetas, datas/links inválidos,
agendamento repetido e preservação de prazo/progresso após edição do curso.


## Relação Curso e AreaEstudo

Curso.java inclui areaEstudo (ManyToOne), getter e definirAreaEstudo; não cria entidade
adicional. CursoRequest/CursoPessoalRequest recebem areaEstudoId ou nome; CursoResponse
retorna ID/nome. CursoService coordena a busca/criação e AreaEstudo valida seu próprio
nome. AreaEstudoRepository acrescenta findFirstByNomeIgnoreCaseOrderByIdAsc. A migração
projeto/database/migrations/20261103-area-do-curso.sql adiciona somente a FK opcional.
Os testes de planejamento verificam persistência, renomeação, validação e remoção do vínculo.
