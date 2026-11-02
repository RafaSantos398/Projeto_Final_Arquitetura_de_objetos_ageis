param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[a-zA-Z0-9.-]+$')][string]$DatabaseHost,
    [ValidatePattern('^[a-zA-Z_][a-zA-Z0-9_]*$')][string]$DatabaseName = 'projeto_intercambio',
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[a-zA-Z_][a-zA-Z0-9_]*$')][string]$DatabaseUser,
    [ValidateRange(1, 65535)][int]$DatabasePort = 5432
)

$ErrorActionPreference = 'Stop'
$migration = Join-Path (Split-Path -Parent $PSScriptRoot) 'database\migrations\20261102-checklists-financas.sql'
$command = Get-Command psql.exe -ErrorAction SilentlyContinue
$psql = if ($command) { $command.Source } else { 'C:\Program Files\PostgreSQL\18\bin\psql.exe' }
if (-not (Test-Path -LiteralPath $psql)) { throw 'psql.exe nao encontrado.' }
if (-not (Test-Path -LiteralPath $migration)) { throw 'Migracao nao encontrada.' }
$connection = "host=$DatabaseHost port=$DatabasePort dbname=$DatabaseName user=$DatabaseUser sslmode=require connect_timeout=15 options='-c search_path=public -c lock_timeout=10000 -c statement_timeout=60000'"
Write-Host "Atualizando apenas a estrutura para checklists e financas: $DatabaseHost / $DatabaseName"
Write-Host 'Informe a senha atual apenas no prompt do PostgreSQL. As tarefas e os valores existentes serao preservados.'
# O psql solicita a senha no console, sem coloca-la no comando ou em arquivos.
$ErrorActionPreference = 'Continue'
& $psql -X -W -v ON_ERROR_STOP=1 --dbname $connection -f $migration
$migrationExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
if ($migrationExit -ne 0) { throw 'Migracao falhou; a transacao nao foi concluida. Confira a mensagem do PostgreSQL.' }
Write-Host 'Estrutura atualizada. Agora publique primeiro o backend e depois o frontend.' -ForegroundColor Green
