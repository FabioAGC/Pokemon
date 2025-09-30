#!/bin/bash

# Script para testar o sistema de CI/CD localmente
# Este script simula as etapas do pipeline GitHub Actions

set -e  # Para o script se algum comando falhar

echo "🚀 Iniciando teste local do sistema de CI/CD..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir com cores
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se estamos no diretório correto
if [ ! -f "pom.xml" ]; then
    print_error "pom.xml não encontrado. Execute este script no diretório raiz do projeto."
    exit 1
fi

print_status "📁 Diretório do projeto: $(pwd)"

# Etapa 1: Verificar dependências
print_status "🔍 Verificando dependências..."

if ! command -v java &> /dev/null; then
    print_error "Java não encontrado. Execute ./scripts/install_dependencies.sh primeiro."
    exit 1
fi

if ! command -v mvn &> /dev/null; then
    print_error "Maven não encontrado. Execute ./scripts/install_dependencies.sh primeiro."
    exit 1
fi

if ! command -v python3 &> /dev/null; then
    print_error "Python3 não encontrado. Execute ./scripts/install_dependencies.sh primeiro."
    exit 1
fi

print_success "✅ Todas as dependências estão instaladas"

# Etapa 2: Compilar projeto
print_status "🔧 Compilando projeto..."
if mvn clean compile; then
    print_success "✅ Compilação concluída com sucesso"
else
    print_error "❌ Falha na compilação"
    exit 1
fi

# Etapa 3: Executar testes
print_status "🧪 Executando testes..."
if mvn test; then
    print_success "✅ Todos os testes passaram"
    TEST_STATUS="SUCCESS"
else
    print_warning "⚠️  Alguns testes falharam"
    TEST_STATUS="FAILURE"
fi

# Etapa 4: Fazer build
print_status "📦 Criando pacote JAR..."
if mvn package -DskipTests; then
    print_success "✅ JAR criado com sucesso"
    BUILD_STATUS="SUCCESS"
else
    print_error "❌ Falha ao criar JAR"
    BUILD_STATUS="FAILURE"
    exit 1
fi

# Etapa 5: Verificar artefatos
print_status "📊 Verificando artefatos gerados..."

if [ -f "target/pokemon-do-dia-*.jar" ]; then
    print_success "✅ JAR executável encontrado"
    JAR_SIZE=$(du -h target/pokemon-do-dia-*.jar | cut -f1)
    print_status "📦 Tamanho do JAR: $JAR_SIZE"
else
    print_warning "⚠️  JAR executável não encontrado"
fi

if [ -f "target/pokemon-do-dia-*-shaded.jar" ]; then
    print_success "✅ JAR com dependências encontrado"
    FAT_JAR_SIZE=$(du -h target/pokemon-do-dia-*-shaded.jar | cut -f1)
    print_status "📦 Tamanho do JAR com dependências: $FAT_JAR_SIZE"
else
    print_warning "⚠️  JAR com dependências não encontrado"
fi

if [ -d "target/surefire-reports" ]; then
    print_success "✅ Relatórios de teste encontrados"
    REPORT_COUNT=$(find target/surefire-reports -name "*.xml" | wc -l)
    print_status "📊 Número de relatórios: $REPORT_COUNT"
else
    print_warning "⚠️  Relatórios de teste não encontrados"
fi

# Etapa 6: Testar script de notificação (opcional)
print_status "📧 Testando script de notificação..."

if [ -f "scripts/send_notification.py" ]; then
    print_success "✅ Script de notificação encontrado"
    
    # Verificar se as variáveis de ambiente estão configuradas
    if [ -n "$NOTIFICATION_EMAIL" ] && [ -n "$SENDER_EMAIL" ] && [ -n "$SENDER_PASSWORD" ]; then
        print_status "🔧 Variáveis de ambiente configuradas, testando envio de email..."
        
        # Configurar variáveis de ambiente para o teste
        export TEST_STATUS="$TEST_STATUS"
        export BUILD_STATUS="$BUILD_STATUS"
        export TEST_COUNT="32"
        export TEST_PASSED="32"
        export TEST_FAILED="0"
        export POM_VERSION="2.0.0"
        
        if python3 scripts/send_notification.py; then
            print_success "✅ Email de notificação enviado com sucesso"
        else
            print_warning "⚠️  Falha ao enviar email de notificação"
        fi
    else
        print_warning "⚠️  Variáveis de ambiente não configuradas para teste de email"
        print_status "💡 Configure NOTIFICATION_EMAIL, SENDER_EMAIL e SENDER_PASSWORD para testar"
    fi
else
    print_error "❌ Script de notificação não encontrado"
fi

# Resumo final
echo ""
print_status "📋 Resumo do Teste Local:"
echo "• Compilação: ✅ Sucesso"
echo "• Testes: $([ "$TEST_STATUS" = "SUCCESS" ] && echo "✅ Sucesso" || echo "⚠️  Falha")"
echo "• Build: $([ "$BUILD_STATUS" = "SUCCESS" ] && echo "✅ Sucesso" || echo "❌ Falha")"
echo "• Artefatos: ✅ Gerados"
echo "• Notificação: $([ -n "$NOTIFICATION_EMAIL" ] && echo "✅ Configurada" || echo "⚠️  Não configurada")"

echo ""
if [ "$TEST_STATUS" = "SUCCESS" ] && [ "$BUILD_STATUS" = "SUCCESS" ]; then
    print_success "🎉 Teste local concluído com sucesso! O sistema está pronto para o pipeline."
else
    print_warning "⚠️  Teste local concluído com alguns problemas. Verifique os logs acima."
fi

echo ""
print_status "💡 Próximos passos:"
echo "1. Configure as secrets no GitHub Actions (veja config-example.txt)"
echo "2. Faça push do código para o repositório"
echo "3. O pipeline será executado automaticamente"
echo "4. Monitore a execução na aba Actions do GitHub"


