param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[a-zA-Z0-9.-]+$')]
    [string]$DatabaseHost,
    [ValidatePattern('^[a-zA-Z_][a-zA-Z0-9_]*$')]
    [string]$DatabaseName = 'projeto_intercambio',
    [string]$DatabaseUser,
    [ValidateRange(1, 65535)]
    [int]$DatabasePort = 5432,
    [string]$StatusFile,
    [switch]$PauseWindow
)

$ErrorActionPreference = 'Stop'
$backendRoot = Split-Path -Parent $PSScriptRoot
$schemaFile = Join-Path $backendRoot 'database\schema-postgresql.sql'
$validationLog = Join-Path ([System.IO.Path]::GetTempPath()) ('intercambio-postgres-validacao-' + [guid]::NewGuid().ToString('N') + '.log')
$oldDirectory = (Get-Location).Path
$environmentNames = @('PGPASSWORD', 'DB_URL', 'DB_USERNAME', 'DB_PASSWORD', 'JWT_SECRET')
$previousEnvironment = @{}
foreach ($environmentName in $environmentNames) {
    $previousEnvironment[$environmentName] = [Environment]::GetEnvironmentVariable($environmentName, 'Process')
}

function Write-SetupState([string]$State, [string]$Message, [int]$TableCount = 0) {
    if ($StatusFile) {
        [ordered]@{ state = $State; message = $Message; tables = $TableCount; log = $validationLog } |
            ConvertTo-Json | Set-Content -LiteralPath $StatusFile -Encoding UTF8
    }
}

$setupExit = 0
try {
    if (-not (Test-Path -LiteralPath $schemaFile)) {
        throw 'O arquivo database\schema-postgresql.sql nao foi encontrado.'
    }
    $schemaText = Get-Content -LiteralPath $schemaFile -Raw
    if ($schemaText -match '(?im)^\s*(drop|truncate|delete|update|insert)\b') {
        throw 'O SQL inicial deve conter apenas a criacao do esquema, sem alteracao de dados existentes.'
    }

    $psqlCommand = Get-Command psql.exe -ErrorAction SilentlyContinue
    if ($psqlCommand) {
        $psqlPath = $psqlCommand.Source
    } else {
        $psqlPath = 'C:\Program Files\PostgreSQL\18\bin\psql.exe'
    }
    if (-not (Test-Path -LiteralPath $psqlPath)) {
        throw 'psql.exe nao encontrado. Adicione a pasta bin do PostgreSQL ao PATH.'
    }

    Write-SetupState 'awaiting_credentials' 'Aguardando o usuario e a senha atuais do Render na janela local.'
    if ([string]::IsNullOrWhiteSpace($DatabaseUser)) {
        $DatabaseUser = Read-Host 'Usuario atual do PostgreSQL mostrado no Render'
    }
    if ($DatabaseUser -notmatch '^[a-zA-Z_][a-zA-Z0-9_]*$') {
        throw 'Informe o nome do usuario PostgreSQL mostrado no Render.'
    }
    Write-Host "Servidor: $DatabaseHost`:$DatabasePort"
    Write-Host "Banco: $DatabaseName | Usuario: $DatabaseUser"
    Write-Host 'Use a nova senha do Render. A senha nao sera exibida nem salva em arquivo.'
    Write-SetupState 'awaiting_password' 'Aguardando a senha digitada na janela local.'
    $securePassword = Read-Host 'Senha do PostgreSQL' -AsSecureString
    if ($securePassword.Length -eq 0) { throw 'A senha nao pode ficar vazia.' }
    $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    try {
        $env:PGPASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
        $securePassword.Dispose()
    }

    $connection = "host=$DatabaseHost port=$DatabasePort dbname=$DatabaseName user=$DatabaseUser sslmode=require connect_timeout=15"
    Write-SetupState 'connecting' 'Conferindo as tabelas existentes no PostgreSQL.'
    $ErrorActionPreference = 'Continue'
    $tableOutput = & $psqlPath -X -w -A -t -v ON_ERROR_STOP=1 --dbname $connection -c "select count(*) from pg_catalog.pg_tables where schemaname = 'public';" 2>&1
    $psqlExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    if ($psqlExit -ne 0) {
        throw ('Falha na conexao: ' + (($tableOutput | ForEach-Object { $_.ToString() }) -join ' '))
    }
    $tableCount = [int](($tableOutput -join '').Trim())

    if ($tableCount -eq 0) {
        Write-SetupState 'creating_schema' 'Criando tabelas e relacionamentos numa transacao.'
        $ErrorActionPreference = 'Continue'
        & $psqlPath -X -w -v ON_ERROR_STOP=1 --single-transaction --dbname $connection -f $schemaFile
        $psqlExit = $LASTEXITCODE
        $ErrorActionPreference = 'Stop'
        if ($psqlExit -ne 0) { throw 'A criacao do esquema falhou e a transacao foi revertida.' }
    } else {
        Write-Host "O banco ja tem $tableCount tabelas. Nenhuma sera recriada; vamos apenas validar o esquema."
    }

    $env:DB_URL = "jdbc:postgresql://${DatabaseHost}:${DatabasePort}/${DatabaseName}?sslmode=require"
    $env:DB_USERNAME = $DatabaseUser
    $env:DB_PASSWORD = $env:PGPASSWORD
    # Usada apenas no contexto de validação; produção configura JWT_SECRET separadamente.
    $randomKey = New-Object byte[] 32
    $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
    $rng.GetBytes($randomKey)
    $rng.Dispose()
    $env:JWT_SECRET = [Convert]::ToBase64String($randomKey)
    Write-SetupState 'validating' 'Validando o esquema real pelo contexto Spring com ddl-auto=validate.'
    Set-Location -LiteralPath $backendRoot
    $ErrorActionPreference = 'Continue'
    & .\mvnw.cmd test -q '-Dtest=ProjetoApplicationTests' '-Dspring.profiles.active=prod' '-Dspring.jpa.hibernate.ddl-auto=validate' *> $validationLog
    $validationExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    if ($validationExit -ne 0) {
        throw "O Spring nao validou o esquema. Os detalhes estao no log local: $validationLog"
    }

    $ErrorActionPreference = 'Continue'
    $tableOutput = & $psqlPath -X -w -A -t -v ON_ERROR_STOP=1 --dbname $connection -c "select count(*) from pg_catalog.pg_tables where schemaname = 'public';" 2>&1
    $psqlExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    if ($psqlExit -ne 0) { throw 'O esquema foi validado, mas a consulta final de tabelas falhou.' }
    $tableCount = [int](($tableOutput -join '').Trim())
    Write-SetupState 'success' 'Esquema PostgreSQL criado ou existente e validado pelo Spring.' $tableCount
    Write-Host "Concluido: $tableCount tabelas presentes e esquema validado pelo Spring." -ForegroundColor Green
    Write-Host "Log de validacao: $validationLog"
} catch {
    $setupExit = 1
    Write-SetupState 'failed' $_.Exception.Message
    Write-Host $_.Exception.Message -ForegroundColor Red
} finally {
    foreach ($environmentName in $environmentNames) {
        [Environment]::SetEnvironmentVariable($environmentName, $previousEnvironment[$environmentName], 'Process')
    }
    Set-Location -LiteralPath $oldDirectory
}

if ($PauseWindow) { $null = Read-Host 'Pressione Enter para fechar esta janela' }
exit $setupExit
