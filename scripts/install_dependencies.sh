#!/bin/bash

# Script de instalação de dependências para o pipeline CI/CD
# Este script instala todas as ferramentas necessárias para execução do pipeline

set -e  # Para o script se algum comando falhar

echo "🚀 Iniciando instalação de dependências para o pipeline CI/CD..."

# Função para verificar se um comando existe
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Função para instalar Java
install_java() {
    echo "☕ Verificando instalação do Java..."
    
    if command_exists java; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        echo "✅ Java já instalado (versão $JAVA_VERSION)"
        
        if [ "$JAVA_VERSION" -ge 11 ]; then
            echo "✅ Versão do Java compatível (>= 11)"
        else
            echo "⚠️  Versão do Java muito antiga. Recomendado: Java 11+"
        fi
    else
        echo "❌ Java não encontrado. Instalando..."
        
        # Detectar sistema operacional
        if [[ "$OSTYPE" == "linux-gnu"* ]]; then
            # Linux
            if command_exists apt-get; then
                # Ubuntu/Debian
                sudo apt-get update
                sudo apt-get install -y openjdk-11-jdk
            elif command_exists yum; then
                # CentOS/RHEL
                sudo yum install -y java-11-openjdk-devel
            elif command_exists dnf; then
                # Fedora
                sudo dnf install -y java-11-openjdk-devel
            else
                echo "❌ Gerenciador de pacotes não suportado"
                exit 1
            fi
        elif [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            if command_exists brew; then
                brew install openjdk@11
            else
                echo "❌ Homebrew não encontrado. Instale o Homebrew primeiro."
                exit 1
            fi
        else
            echo "❌ Sistema operacional não suportado: $OSTYPE"
            exit 1
        fi
        
        echo "✅ Java instalado com sucesso!"
    fi
}

# Função para instalar Maven
install_maven() {
    echo "📦 Verificando instalação do Maven..."
    
    if command_exists mvn; then
        MAVEN_VERSION=$(mvn -version | head -n 1 | cut -d' ' -f3)
        echo "✅ Maven já instalado (versão $MAVEN_VERSION)"
    else
        echo "❌ Maven não encontrado. Instalando..."
        
        # Detectar sistema operacional
        if [[ "$OSTYPE" == "linux-gnu"* ]]; then
            # Linux
            if command_exists apt-get; then
                # Ubuntu/Debian
                sudo apt-get update
                sudo apt-get install -y maven
            elif command_exists yum; then
                # CentOS/RHEL
                sudo yum install -y maven
            elif command_exists dnf; then
                # Fedora
                sudo dnf install -y maven
            else
                echo "❌ Gerenciador de pacotes não suportado"
                exit 1
            fi
        elif [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            if command_exists brew; then
                brew install maven
            else
                echo "❌ Homebrew não encontrado. Instale o Homebrew primeiro."
                exit 1
            fi
        else
            echo "❌ Sistema operacional não suportado: $OSTYPE"
            exit 1
        fi
        
        echo "✅ Maven instalado com sucesso!"
    fi
}

# Função para instalar Python
install_python() {
    echo "🐍 Verificando instalação do Python..."
    
    if command_exists python3; then
        PYTHON_VERSION=$(python3 --version | cut -d' ' -f2)
        echo "✅ Python já instalado (versão $PYTHON_VERSION)"
        
        # Verificar se a versão é >= 3.7
        PYTHON_MAJOR=$(echo $PYTHON_VERSION | cut -d'.' -f1)
        PYTHON_MINOR=$(echo $PYTHON_VERSION | cut -d'.' -f2)
        
        if [ "$PYTHON_MAJOR" -eq 3 ] && [ "$PYTHON_MINOR" -ge 7 ]; then
            echo "✅ Versão do Python compatível (>= 3.7)"
        else
            echo "⚠️  Versão do Python muito antiga. Recomendado: Python 3.7+"
        fi
    else
        echo "❌ Python3 não encontrado. Instalando..."
        
        # Detectar sistema operacional
        if [[ "$OSTYPE" == "linux-gnu"* ]]; then
            # Linux
            if command_exists apt-get; then
                # Ubuntu/Debian
                sudo apt-get update
                sudo apt-get install -y python3 python3-pip
            elif command_exists yum; then
                # CentOS/RHEL
                sudo yum install -y python3 python3-pip
            elif command_exists dnf; then
                # Fedora
                sudo dnf install -y python3 python3-pip
            else
                echo "❌ Gerenciador de pacotes não suportado"
                exit 1
            fi
        elif [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            if command_exists brew; then
                brew install python@3.9
            else
                echo "❌ Homebrew não encontrado. Instale o Homebrew primeiro."
                exit 1
            fi
        else
            echo "❌ Sistema operacional não suportado: $OSTYPE"
            exit 1
        fi
        
        echo "✅ Python instalado com sucesso!"
    fi
}

# Função para verificar Git
check_git() {
    echo "📋 Verificando instalação do Git..."
    
    if command_exists git; then
        GIT_VERSION=$(git --version | cut -d' ' -f3)
        echo "✅ Git já instalado (versão $GIT_VERSION)"
    else
        echo "❌ Git não encontrado. Instalando..."
        
        # Detectar sistema operacional
        if [[ "$OSTYPE" == "linux-gnu"* ]]; then
            # Linux
            if command_exists apt-get; then
                # Ubuntu/Debian
                sudo apt-get update
                sudo apt-get install -y git
            elif command_exists yum; then
                # CentOS/RHEL
                sudo yum install -y git
            elif command_exists dnf; then
                # Fedora
                sudo dnf install -y git
            else
                echo "❌ Gerenciador de pacotes não suportado"
                exit 1
            fi
        elif [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            if command_exists brew; then
                brew install git
            else
                echo "❌ Homebrew não encontrado. Instale o Homebrew primeiro."
                exit 1
            fi
        else
            echo "❌ Sistema operacional não suportado: $OSTYPE"
            exit 1
        fi
        
        echo "✅ Git instalado com sucesso!"
    fi
}

# Função para configurar variáveis de ambiente
setup_environment() {
    echo "🔧 Configurando variáveis de ambiente..."
    
    # Verificar JAVA_HOME
    if [ -z "$JAVA_HOME" ]; then
        if command_exists java; then
            JAVA_PATH=$(which java)
            JAVA_HOME=$(dirname $(dirname $JAVA_PATH))
            echo "export JAVA_HOME=$JAVA_HOME" >> ~/.bashrc
            echo "✅ JAVA_HOME configurado: $JAVA_HOME"
        else
            echo "⚠️  JAVA_HOME não configurado (Java não encontrado)"
        fi
    else
        echo "✅ JAVA_HOME já configurado: $JAVA_HOME"
    fi
    
    # Verificar MAVEN_HOME
    if [ -z "$MAVEN_HOME" ]; then
        if command_exists mvn; then
            MAVEN_PATH=$(which mvn)
            MAVEN_HOME=$(dirname $(dirname $MAVEN_PATH))
            echo "export MAVEN_HOME=$MAVEN_HOME" >> ~/.bashrc
            echo "✅ MAVEN_HOME configurado: $MAVEN_HOME"
        else
            echo "⚠️  MAVEN_HOME não configurado (Maven não encontrado)"
        fi
    else
        echo "✅ MAVEN_HOME já configurado: $MAVEN_HOME"
    fi
}

# Função para testar instalações
test_installations() {
    echo "🧪 Testando instalações..."
    
    # Testar Java
    if command_exists java; then
        echo "✅ Java funcionando: $(java -version 2>&1 | head -n 1)"
    else
        echo "❌ Java não está funcionando"
        exit 1
    fi
    
    # Testar Maven
    if command_exists mvn; then
        echo "✅ Maven funcionando: $(mvn -version | head -n 1)"
    else
        echo "❌ Maven não está funcionando"
        exit 1
    fi
    
    # Testar Python
    if command_exists python3; then
        echo "✅ Python funcionando: $(python3 --version)"
    else
        echo "❌ Python não está funcionando"
        exit 1
    fi
    
    # Testar Git
    if command_exists git; then
        echo "✅ Git funcionando: $(git --version)"
    else
        echo "❌ Git não está funcionando"
        exit 1
    fi
}

# Função principal
main() {
    echo "🎯 Sistema: $OSTYPE"
    echo "📅 Data: $(date)"
    echo ""
    
    # Instalar dependências
    install_java
    echo ""
    
    install_maven
    echo ""
    
    install_python
    echo ""
    
    check_git
    echo ""
    
    setup_environment
    echo ""
    
    test_installations
    echo ""
    
    echo "🎉 Instalação de dependências concluída com sucesso!"
    echo ""
    echo "📋 Resumo das ferramentas instaladas:"
    echo "• Java: $(java -version 2>&1 | head -n 1)"
    echo "• Maven: $(mvn -version | head -n 1)"
    echo "• Python: $(python3 --version)"
    echo "• Git: $(git --version)"
    echo ""
    echo "🚀 O pipeline CI/CD está pronto para ser executado!"
}

# Executar função principal
main "$@"


