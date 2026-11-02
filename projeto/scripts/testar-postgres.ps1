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
$testSchema = 'codex_test_' + [guid]::NewGuid().ToString('N').Substring(0, 12)
$testLog = Join-Path ([IO.Path]::GetTempPath()) ($testSchema + '.log')
$testSql = Join-Path ([IO.Path]::GetTempPath()) ($testSchema + '.sql')
$oldDirectory = (Get-Location).Path
$environmentNames = @('PGPASSWORD', 'DB_URL', 'DB_USERNAME', 'DB_PASSWORD', 'JWT_SECRET')
$previousEnvironment = @{}
foreach ($environmentName in $environmentNames) {
    $previousEnvironment[$environmentName] = [Environment]::GetEnvironmentVariable($environmentName, 'Process')
}
$schemaCreated = $false
$testExit = 1
$publicUnchanged = $false
$schemaRemoved = $false
$testSummary = @()

function Write-TestState([string]$State, [string]$Message) {
    if ($StatusFile) {
        [ordered]@{ state = $State; message = $Message; schema = $testSchema;
            log = $testLog; publicRowCountsUnchanged = $publicUnchanged;
            schemaRemoved = $schemaRemoved; tests = $testSummary } |
            ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $StatusFile -Encoding UTF8
    }
}

function Invoke-DatabaseQuery([string]$Sql) {
    $previousErrorAction = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    $queryOutput = & $psqlPath -X -w -A -t -v ON_ERROR_STOP=1 --dbname $connection -c $Sql 2>&1
    $queryExit = $LASTEXITCODE
    $ErrorActionPreference = $previousErrorAction
    if ($queryExit -ne 0) { throw ('Falha PostgreSQL: ' + ($queryOutput -join ' ')) }
    return (($queryOutput | ForEach-Object { $_.ToString() }) -join "`n").Trim()
}

try {
    $schemaText = Get-Content -LiteralPath $schemaFile -Raw
    if ($schemaText -match '(?im)^\s*(drop|truncate|delete|update|insert)\b') {
        throw 'O SQL inicial precisa conter somente a criacao das tabelas e restricoes.'
    }
    $psqlCommand = Get-Command psql.exe -ErrorAction SilentlyContinue
    $psqlPath = if ($psqlCommand) { $psqlCommand.Source } else { 'C:\Program Files\PostgreSQL\18\bin\psql.exe' }
    if (-not (Test-Path -LiteralPath $psqlPath)) { throw 'psql.exe nao encontrado.' }
    Write-TestState 'awaiting_credentials' 'Digite usuario e senha atuais na janela local.'
    if ([string]::IsNullOrWhiteSpace($DatabaseUser)) {
        $DatabaseUser = Read-Host 'Usuario atual do PostgreSQL mostrado no Render'
    }
    if ($DatabaseUser -notmatch '^[a-zA-Z_][a-zA-Z0-9_]*$') { throw 'Usuario PostgreSQL invalido.' }
    Write-Host "Banco: $DatabaseName | Servidor: $DatabaseHost"
    Write-Host 'Os testes usam um esquema temporario separado; public nao recebe escritas.'
    Write-TestState 'awaiting_password' 'Aguardando a senha na janela local.'
    $securePassword = Read-Host 'Senha atual do PostgreSQL' -AsSecureString
    if ($securePassword.Length -eq 0) { throw 'A senha nao pode ficar vazia.' }
    $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    try {
        $env:PGPASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
        $securePassword.Dispose()
    }
    $connection = "host=$DatabaseHost port=$DatabasePort dbname=$DatabaseName user=$DatabaseUser sslmode=require connect_timeout=15"
    Write-TestState 'creating_schema' 'Criando esquema temporario para os testes.'
    # Apenas contagens agregadas das tabelas da aplicacao; nenhum dado pessoal e exibido.
    $tableNames = [regex]::Matches($schemaText, '(?im)^\s*create table ([a-z_]+)\s*\(') |
        ForEach-Object { $_.Groups[1].Value } | Sort-Object -Unique
    if ($tableNames.Count -ne 13) { throw 'O esquema esperado deve conter as 13 tabelas atuais.' }
    $countSql = ($tableNames | ForEach-Object { "select '$_' as tabela, count(*) as registros from public.$_" }) -join ' union all '
    $countSql += ' order by tabela;'
    $publicBefore = Invoke-DatabaseQuery $countSql
    # Nome gerado localmente, nunca derivado de entrada do usuario.
    if ($testSchema -notmatch '^codex_test_[a-f0-9]{12}$') { throw 'Nome temporario invalido.' }
    $null = Invoke-DatabaseQuery "create schema $testSchema;"
    $schemaCreated = $true
    [IO.File]::WriteAllText($testSql, "set search_path to $testSchema;`n" + $schemaText, [Text.UTF8Encoding]::new($false))
    $ErrorActionPreference = 'Continue'
    & $psqlPath -X -w -v ON_ERROR_STOP=1 --single-transaction --dbname $connection -f $testSql *> $testLog
    $schemaExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    if ($schemaExit -ne 0) { throw "Falha na criacao das tabelas temporarias. Log: $testLog" }
    $env:DB_URL = "jdbc:postgresql://${DatabaseHost}:${DatabasePort}/${DatabaseName}?sslmode=require&currentSchema=$testSchema"
    $env:DB_USERNAME = $DatabaseUser
    $env:DB_PASSWORD = $env:PGPASSWORD
    # Chave exclusiva desta execução de testes, nunca salva no projeto.
    $randomKey = New-Object byte[] 32
    $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
    $rng.GetBytes($randomKey)
    $rng.Dispose()
    $env:JWT_SECRET = [Convert]::ToBase64String($randomKey)
    Set-Location -LiteralPath $backendRoot
    Write-TestState 'testing' 'Executando a suite Spring completa no PostgreSQL isolado.'
    $ErrorActionPreference = 'Continue'
    & .\mvnw.cmd test -q '-Dspring.profiles.active=prod' '-Dspring.jpa.hibernate.ddl-auto=validate' *>> $testLog
    $testExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    $testSummary = @(Get-ChildItem -LiteralPath (Join-Path $backendRoot 'target\surefire-reports') -Filter 'TEST-*.xml' | ForEach-Object {
        [xml]$report = Get-Content -LiteralPath $_.FullName
        [ordered]@{ name = $report.testsuite.name; tests = [int]$report.testsuite.tests;
            failures = [int]$report.testsuite.failures; errors = [int]$report.testsuite.errors }
    })
    if ($testExit -ne 0) { throw "Ha falhas nos testes. Consulte $testLog" }
} catch {
    $testExit = 1
    $failureMessage = $_.Exception.Message
    Write-Host $failureMessage -ForegroundColor Red
} finally {
    if ($schemaCreated) {
        try {
            if ($testSchema -notmatch '^codex_test_[a-f0-9]{12}$') { throw 'Limpeza recusada: esquema invalido.' }
            $null = Invoke-DatabaseQuery "drop schema $testSchema cascade;"
            $schemaRemoved = $true
            $publicAfter = Invoke-DatabaseQuery $countSql
            $publicUnchanged = $publicBefore -eq $publicAfter
            if (-not $publicUnchanged) { throw 'As contagens de public mudaram durante a execucao; confira escritas concorrentes.' }
        } catch {
            $testExit = 1
            $failureMessage = $_.Exception.Message
        }
    }
    if (Test-Path -LiteralPath $testSql) { Remove-Item -LiteralPath $testSql }
    foreach ($environmentName in $environmentNames) {
        [Environment]::SetEnvironmentVariable($environmentName, $previousEnvironment[$environmentName], 'Process')
    }
    Set-Location -LiteralPath $oldDirectory
}
if ($testExit -eq 0) {
    Write-TestState 'success' 'Suite PostgreSQL aprovada; esquema temporario removido e contagens de public preservadas.'
    Write-Host 'Testes aprovados. Esquema temporario removido; contagens de public preservadas.' -ForegroundColor Green
} else {
    Write-TestState 'failed' $failureMessage
}
Write-Host "Log: $testLog"
if ($PauseWindow) { $null = Read-Host 'Pressione Enter para fechar' }
exit $testExit
