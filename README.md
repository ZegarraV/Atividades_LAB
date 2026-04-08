# 📄 Histórias de Usuário - Sistema de Aluguel

## 🆕 US01 - Cadastro de Usuário
**Como** novo usuário,  
**Quero** me cadastrar no sistema informando meus dados pessoais e rendimentos,  
**Para** poder acessar as funcionalidades de aluguel.  

**Critérios de Aceitação:**
- O sistema deve validar RG e CPF  
- O sistema deve permitir o cadastro de no máximo 3 rendimentos por cliente  
- O acesso só é liberado após o login  

---

## 🚗 US02 - Novo Pedido de Aluguel
**Como** cliente,  
**Quero** introduzir um novo pedido de aluguel pela internet,  
**Para** iniciar o processo de contratação de um veículo.  

**Critérios de Aceitação:**
- O cliente deve selecionar um veículo (marca, modelo, placa)  
- O pedido deve ser registrado com status **Em Análise Financeira**  

---

## 🔄 US03 - Gerenciar Pedidos
**Como** cliente,  
**Quero** consultar, modificar ou cancelar meus pedidos de aluguel,  
**Para** manter minhas solicitações atualizadas.  

**Critérios de Aceitação:**
- Modificações só podem ocorrer enquanto o pedido não virou contrato  
- O cancelamento deve remover o pedido da fila de execução  

---

## 💼 US04 - Avaliação Financeira
**Como** agente (empresa/banco),  
**Quero** avaliar financeiramente os pedidos pendentes,  
**Para** decidir se o contrato pode ser executado.  

**Critérios de Aceitação:**
- O agente deve visualizar os rendimentos do cliente  
- O agente deve emitir um parecer (positivo ou negativo)  

---

## ✏️ US05 - Ajuste de Pedido
**Como** agente,  
**Quero** modificar detalhes de um pedido após a análise,  
**Para** ajustar cláusulas antes da assinatura final.  

**Critérios de Aceitação:**
- A alteração deve ser notificada ao cliente  
- O sistema deve registrar quem realizou a modificação  

---

## 🏦 US06 - Contrato de Crédito
**Como** banco parceiro,  
**Quero** associar um contrato de crédito a um aluguel aprovado,  
**Para** financiar a operação para o cliente.  

**Critérios de Aceitação:**
- O contrato de crédito deve estar vinculado ao ID do pedido  
- O veículo pode ser registrado em nome do banco conforme o tipo de contrato  

---

## ⚙️ US07 - Atualização em Tempo Real
**Como** sistema,  
**Quero** processar transições de status do pedido e atualizar a interface em tempo real via MVC,  
**Para** evitar recarregamento da página.  

**Critérios de Aceitação:**
- Deve seguir arquitetura MVC  
- A View deve refletir mudanças do Model em tempo real  

---

## 📊 US08 - Histórico de Pedidos
**Como** cliente,  
**Quero** visualizar o histórico completo dos meus pedidos e contratos,  
**Para** acompanhar meus alugueis anteriores.  

**Critérios de Aceitação:**
- O histórico deve mostrar status, veículo e período  
- Os dados devem ser somente leitura  

---

## 🔐 US09 - Login Seguro
**Como** usuário,  
**Quero** realizar login no sistema de forma segura,  
**Para** acessar funcionalidades conforme meu perfil.  

**Critérios de Aceitação:**
- O sistema deve validar login e senha  
- Tentativas inválidas devem ser registradas  

---

## 🧑‍💻 US10 - Controle de Acesso
**Como** sistema,  
**Quero** diferenciar acessos por perfil (cliente, agente, banco),  
**Para** garantir permissões corretas.  

**Critérios de Aceitação:**
- Cada perfil deve acessar apenas seus casos de uso  
- Permissões devem ser validadas no controller  

---

## 🔔 US11 - Notificações
**Como** cliente,  
**Quero** receber notificações quando meu pedido mudar de status,  
**Para** acompanhar o processo de aprovação.  

**Critérios de Aceitação:**
- Notificações devem ocorrer em toda transição de status  
- A notificação deve conter data e agente responsável  

---

## 📋 US12 - Fila de Pedidos
**Como** agente,  
**Quero** acessar a fila de pedidos em análise,  
**Para** organizar melhor meu atendimento.  

**Critérios de Aceitação:**
- A fila deve ser ordenada por data de criação  
- Apenas pedidos pendentes devem aparecer  

---

## 🚫 US13 - Exclusividade de Veículo
**Como** sistema,  
**Quero** garantir que um veículo não esteja vinculado a mais de um pedido ativo,  
**Para** evitar conflitos de aluguel.  

**Critérios de Aceitação:**
- O sistema deve bloquear pedidos duplicados para o mesmo veículo  
- Deve exibir mensagem de indisponibilidade  

---

## 💰 US14 - Detalhes Financeiros
**Como** cliente,  
**Quero** visualizar os detalhes financeiros do meu pedido antes da aprovação,  
**Para** decidir se continuo com o aluguel.  

**Critérios de Aceitação:**
- Os valores devem ser calculados automaticamente  
- Os dados devem ser apenas para visualização  

---

## 📑 US15 - Consulta de Contratos
**Como** banco,  
**Quero** consultar contratos vinculados aos meus financiamentos,  
**Para** controle das operações de crédito.  

**Critérios de Aceitação:**
- Cada contrato deve conter o ID do banco  
- Apenas contratos aprovados devem ser exibidos  

---

## 🧾 US16 - Auditoria de Alterações
**Como** sistema,  
**Quero** registrar todas as alterações feitas em pedidos e contratos,  
**Para** garantir rastreabilidade.  

**Critérios de Aceitação:**
- Cada alteração deve registrar usuário, data e ação  
- Os logs não podem ser alterados  

---

## ❌ US17 - Reprovação de Pedido
**Como** agente,  
**Quero** reprovar um pedido justificando o motivo,  
**Para** manter transparência com o cliente.  

**Critérios de Aceitação:**
- A reprovação deve exigir justificativa  
- O cliente deve ser notificado automaticamente  

---

## 📌 US18 - Visualizar Reprovação
**Como** cliente,  
**Quero** visualizar os motivos de reprovação do meu pedido,  
**Para** decidir se faço um novo pedido.  

**Critérios de Aceitação:**
- A justificativa deve estar visível no pedido  
- O pedido deve ficar com status **Reprovado**  

---

## ⛔ US19 - Bloqueio de Alterações
**Como** sistema,  
**Quero** impedir modificações em pedidos já cancelados ou aprovados,  
**Para** preservar consistência dos dados.  

**Critérios de Aceitação:**
- O sistema deve validar o status antes de permitir alterações  
- Deve exibir mensagem de erro apropriada  

---

## 📈 US20 - Métricas Gerenciais
**Como** administrador do sistema,  
**Quero** visualizar métricas de pedidos aprovados, reprovados e cancelados,  
**Para** análise gerencial.  

**Critérios de Aceitação:**
- O sistema deve gerar estatísticas por período  
- Os dados devem ser atualizados automaticamente
