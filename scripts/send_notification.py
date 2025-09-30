#!/usr/bin/env python3
"""
Script de notificação por email para o pipeline CI/CD do projeto Pokemon.
Este script envia um email com informações sobre a execução do pipeline.
"""

import os
import sys
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from datetime import datetime
import json

def get_pipeline_status():
    """Obtém o status do pipeline baseado nas variáveis de ambiente do GitHub Actions."""
    # Verifica se está rodando no GitHub Actions
    if os.getenv('GITHUB_ACTIONS'):
        # Status baseado no resultado dos jobs anteriores
        test_status = os.getenv('TEST_STATUS', 'UNKNOWN')
        build_status = os.getenv('BUILD_STATUS', 'UNKNOWN')
        
        if test_status == 'SUCCESS' and build_status == 'SUCCESS':
            return 'SUCCESS', 'Pipeline executado com sucesso!'
        elif test_status == 'FAILURE' or build_status == 'FAILURE':
            return 'FAILURE', 'Pipeline falhou!'
        else:
            return 'UNKNOWN', 'Status do pipeline desconhecido.'
    else:
        # Para execução local
        return 'SUCCESS', 'Pipeline executado! (execução local)'

def get_pipeline_info():
    """Coleta informações sobre o pipeline."""
    info = {
        'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
        'repository': os.getenv('GITHUB_REPOSITORY', 'Pokemon Project'),
        'workflow': os.getenv('GITHUB_WORKFLOW', 'CI/CD Pipeline'),
        'run_id': os.getenv('GITHUB_RUN_ID', 'local'),
        'actor': os.getenv('GITHUB_ACTOR', 'Local User'),
        'ref': os.getenv('GITHUB_REF', 'main'),
        'sha': os.getenv('GITHUB_SHA', 'local')[:8] if os.getenv('GITHUB_SHA') else 'local'
    }
    return info

def create_email_content(status, message, info):
    """Cria o conteúdo do email."""
    status_emoji = "✅" if status == 'SUCCESS' else "❌" if status == 'FAILURE' else "⚠️"
    
    subject = f"{status_emoji} {message} - {info['repository']}"
    
    body = f"""
{status_emoji} {message}

📊 Informações do Pipeline:
• Repositório: {info['repository']}
• Workflow: {info['workflow']}
• Branch: {info['ref']}
• Commit: {info['sha']}
• Executado por: {info['actor']}
• Timestamp: {info['timestamp']}
• Run ID: {info['run_id']}

🔧 Detalhes Técnicos:
• Status dos Testes: {os.getenv('TEST_STATUS', 'N/A')}
• Status do Build: {os.getenv('BUILD_STATUS', 'N/A')}
• Número de Testes: {os.getenv('TEST_COUNT', 'N/A')}
• Testes Passaram: {os.getenv('TEST_PASSED', 'N/A')}
• Testes Falharam: {os.getenv('TEST_FAILED', 'N/A')}

📦 Artefatos Gerados:
• JAR Executável: pokemon-do-dia-{os.getenv('POM_VERSION', '2.0.0')}.jar
• Relatório de Testes: surefire-reports/

🚀 Próximos Passos:
{'Pipeline concluído com sucesso! O projeto está pronto para deploy.' if status == 'SUCCESS' else 'Verifique os logs do pipeline para identificar e corrigir os problemas.'}

---
Enviado automaticamente pelo sistema de CI/CD
Projeto Pokemon - Sistema de Gerenciamento de Dependências
    """
    
    return subject, body

def send_email(recipient_email, subject, body):
    """Envia o email usando SMTP."""
    try:
        # Configurações do servidor SMTP (usando Gmail como exemplo)
        smtp_server = os.getenv('SMTP_SERVER', 'smtp.gmail.com')
        smtp_port = int(os.getenv('SMTP_PORT', '587'))
        sender_email = os.getenv('SENDER_EMAIL')
        sender_password = os.getenv('SENDER_PASSWORD')
        
        if not sender_email or not sender_password:
            print("❌ Erro: SENDER_EMAIL e SENDER_PASSWORD devem ser definidos como variáveis de ambiente")
            return False
        
        # Criar mensagem
        msg = MIMEMultipart()
        msg['From'] = sender_email
        msg['To'] = recipient_email
        msg['Subject'] = subject
        
        # Adicionar corpo do email
        msg.attach(MIMEText(body, 'plain', 'utf-8'))
        
        # Conectar e enviar
        with smtplib.SMTP(smtp_server, smtp_port) as server:
            server.starttls()
            server.login(sender_email, sender_password)
            server.send_message(msg)
        
        print(f"✅ Email enviado com sucesso para: {recipient_email}")
        return True
        
    except Exception as e:
        print(f"❌ Erro ao enviar email: {str(e)}")
        return False

def main():
    """Função principal do script."""
    print("🚀 Iniciando script de notificação...")
    
    # Obter email do destinatário
    recipient_email = os.getenv('NOTIFICATION_EMAIL')
    if not recipient_email:
        print("❌ Erro: NOTIFICATION_EMAIL deve ser definido como variável de ambiente")
        sys.exit(1)
    
    # Obter status e informações do pipeline
    status, message = get_pipeline_status()
    info = get_pipeline_info()
    
    print(f"📊 Status do Pipeline: {status}")
    print(f"📧 Enviando notificação para: {recipient_email}")
    
    # Criar conteúdo do email
    subject, body = create_email_content(status, message, info)
    
    # Enviar email
    success = send_email(recipient_email, subject, body)
    
    if success:
        print("✅ Notificação enviada com sucesso!")
        sys.exit(0)
    else:
        print("❌ Falha ao enviar notificação!")
        sys.exit(1)

if __name__ == "__main__":
    main()


