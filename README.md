# � Sistema de Aluguel de Carros

![Status](https://img.shields.io/badge/Status-Production%20Ready-brightgreen)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Micronaut](https://img.shields.io/badge/Micronaut-4.x-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## 📋 Descrição

**Sistema de Aluguel de Carros** é uma aplicação web moderna desenvolvida em **Micronaut** com **Thymeleaf** para gerenciamento completo de aluguel de veículos. O sistema oferece fluxos separados para clientes e agentes, com análise financeira integrada, gestão de contratos e dashboards personalizados.

A aplicação implementa um workflow de aprovação de pedidos com avaliação financeira em tempo real, permitindo que agentes avaliem a capacidade de pagamento de clientes através de seus rendimentos informados.

---

## 🎯 Características Principais

### Para Clientes
- ✅ Autenticação segura com sessão
- ✅ Cadastro com validação de CPF/RG
- ✅ Registro de até 3 rendimentos
- ✅ Criação e gerenciamento de pedidos de aluguel
- ✅ Dashboard intuitivo com status de pedidos
- ✅ Visualização de automóveis disponíveis
- ✅ Perfil com histórico de transações

### Para Agentes
- ✅ Dashboard de análise financeira
- ✅ Avaliação de pedidos pendentes
- ✅ Visualização de rendimentos do cliente
- ✅ Aprovação/Rejeição com geração automática de contratos
- ✅ Fila de pedidos em análise

### Técnicas
- ✅ Arquitetura MVC com separação de responsabilidades
- ✅ Persistência com JPA/Hibernate
- ✅ Banco de dados H2 (desenvolvimento) com suporte para produção
- ✅ Validação de dados em múltiplas camadas
- ✅ Filtros HTTP para controle de cache e segurança
- ✅ Lazy-loading consciente com eager fetch estratégico

---

## 🏗️ Arquitetura

```
src/main/java/com/aluguelcarros/
├── config/                    # Configuração e startup
├── model/
│   ├── entity/               # Entidades JPA (Cliente, Agente, PedidoImpl, etc.)
│   ├── enums/                # Enumerações (PedidoStatus)
│   └── exception/            # Exceções customizadas
├── repository/               # Repositórios JPA manuais (não gerados)
├── service/                  # Lógica de negócio
│   └── facade/               # Facades para operações complexas
├── web/
│   ├── controller/           # Controladores MVC
│   ├── dto/                  # Data Transfer Objects
│   ├── filter/               # Filtros HTTP
│   └── resources/            # Views e assets
```

### Camadas
- **Controller**: Gerencia requisições HTTP e sessão do usuário
- **Service/Facade**: Implementa regras de negócio e orquestração
- **Repository**: Acesso a dados com JPA custom
- **Entity**: Modelos de domínio com persistência

---

## 📊 Fluxo de Pedidos

```
RASCUNHO → ENVIADO → EM_ANALISE → APROVADO/REPROVADO
                                  ↓
                           Gera Contrato
```

1. **RASCUNHO**: Cliente cria pedido inicial
2. **ENVIADO**: Cliente finaliza e envia para análise
3. **EM_ANALISE**: Agente avalia financeiramente
4. **APROVADO**: Contrato gerado automaticamente
5. **REPROVADO**: Pedido rejeitado, sem contrato

---

## 🛠️ Tecnologias

| Componente | Versão | Descrição |
|-----------|--------|-----------|
| **Java** | 17+ | Runtime esperado |
| **Micronaut** | 4.x | Framework web reativo |
| **Thymeleaf** | 3.x | Mecanismo de templates servidor |
| **Hibernate JPA** | 6.x | ORM para persistência |
| **H2 Database** | 2.x | Banco de dados em memória (dev) |
| **Maven** | 3.9+ | Gerenciador de dependências e build |
| **Logback** | 1.4+ | Logging estruturado |

---

## 📋 Pré-requisitos

- **Java Development Kit (JDK)** 17 ou superior
- **Maven** 3.9+
- **Git** para versionamento

Verificar instalação:
```bash
java -version
mvn -version
git --version
```

---

## 🚀 Instalação e Setup

### 1. Clonar o Repositório
```bash
git clone https://github.com/ZegarraV/Atividades_LAB.git
cd aluguel-carros
```

### 2. Build do Projeto
```bash
mvn clean compile
```

### 3. Resolver Dependências
```bash
mvn dependency:resolve
```

---

## 🏃 Executando a Aplicação

### Modo Desenvolvimento
```bash
mvn mn:run
```

A aplicação iniciará em: **http://localhost:8080**

### Build para Produção
```bash
mvn clean package
```

Executar o JAR gerado:
```bash
java -jar target/aluguel-carros-0.1.jar
```

---

## 🔐 Credenciais Padrão

Ao iniciar, o sistema carrega dados de seed:

| Login | Senha | Tipo | Descrição |
|-------|-------|------|-----------|
| `agente` | `123456` | Agente | Usuário dedicado para análise financeira |

**Clientes**: Podem se registrar em `http://localhost:8080/cadastro`

---

## 📂 Estrutura do Projeto

```
aluguel-carros/
├── pom.xml                           # Configuração Maven
├── README.md                         # Este arquivo
├── .gitignore                        # Exclusões Git
└── src/
    ├── main/
    │   ├── java/com/aluguelcarros/
    │   │   ├── Application.java      # Classe principal Micronaut
    │   │   ├── config/
    │   │   │   └── StartupDataLoader.java
    │   │   ├── model/
    │   │   │   ├── entity/
    │   │   │   │   ├── Usuario.java
    │   │   │   │   ├── Cliente.java
    │   │   │   │   ├── Agente.java
    │   │   │   │   ├── PedidoImpl.java
    │   │   │   │   ├── Contrato.java
    │   │   │   │   ├── Automovel.java
    │   │   │   │   ├── Rendimento.java
    │   │   │   │   └── Proprietario.java
    │   │   │   ├── enums/
    │   │   │   │   └── PedidoStatus.java
    │   │   │   └── exception/
    │   │   ├── repository/           # Repositórios JPA
    │   │   ├── service/              # Lógica de negócio
    │   │   └── web/
    │   │       ├── controller/       # Controladores HTTP/MVC
    │   │       ├── dto/              # DTOs para transferência
    │   │       ├── filter/           # Filtros HTTP
    │   │       └── resources/        # Assets estáticos
    │   └── resources/
    │       ├── application.yml       # Configuração Micronaut
    │       ├── logback.xml           # Configuração de logging
    │       ├── static/css/           # Estilos CSS
    │       └── views/                # Templates Thymeleaf
    │           ├── layout/           # Layout base
    │           ├── auth/             # Login/Cadastro
    │           ├── cliente/          # Dashboard cliente
    │           ├── agente/           # Dashboard agente
    │           ├── automoveis/       # Gestão de carros
    │           └── pedidos/          # Gestão de pedidos
    └── test/                         # Testes unitários
```

---

## 🔗 Endpoints Principais

### Autenticação
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/` | Homepage |
| `GET` | `/login` | Formulário de login |
| `POST` | `/login` | Efetuar login |
| `GET` | `/cadastro` | Formulário de cadastro |
| `POST` | `/cadastro` | Registrar novo cliente |
| `POST` | `/logout` | Efetuar logout |

### Cliente
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/cliente/dashboard` | Dashboard do cliente |
| `GET` | `/cliente/perfil` | Visualizar perfil |
| `GET` | `/pedidos/novo-pedido` | Criar novo pedido |
| `POST` | `/pedidos` | Enviar pedido |

### Agente
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/agente/dashboard` | Dashboard de análise |
| `POST` | `/agente/pedidos/{id}/analisar` | Iniciar análise |
| `POST` | `/agente/pedidos/{id}/aprovar` | Aprovar pedido |
| `POST` | `/agente/pedidos/{id}/reprovar` | Rejeitar pedido |

### Automóveis
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/automoveis` | Listar carros |
| `GET` | `/automoveis/novo` | Formulário para novo carro |
| `POST` | `/automoveis` | Criar novo automóvel |

---

## 🗄️ Entidades Principais

### Cliente
```java
@Entity
public class Cliente extends Usuario {
    private String cpf;              // CPF do cliente
    private String rg;               // RG do cliente
    private String profissao;        // Profissão
    @OneToMany(fetch = FetchType.LAZY)
    private List<Rendimento> rendimentos;  // Até 3 rendimentos
    @OneToMany(fetch = FetchType.LAZY)
    private List<Pedido> pedidos;    // Pedidos do cliente
}
```

### PedidoImpl
```java
@Entity
public class PedidoImpl implements Pedido {
    private PedidoStatus status;     // RASCUNHO → ENVIADO → EM_ANALISE → APROVADO/REPROVADO
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    @ManyToOne(fetch = FetchType.EAGER)
    private Cliente cliente;
    @ManyToOne(fetch = FetchType.EAGER)
    private Automovel automovel;
}
```

### Contrato
```java
@Entity
public class Contrato {
    @OneToOne
    private Pedido pedido;           // Pedido associado
    private LocalDateTime dataAssinatura;
    private BigDecimal valorFinanciado;
    private String tipoContrato;
}
```

---

## 🔒 Segurança

- **Sessão HTTP**: Implementada via Micronaut Session
- **Validação de Entrada**: Em Controllers e Services
- **Proteção de Cache**: Filtro `NoCacheRedirectFilter` adiciona headers:
  - `Cache-Control: no-store, no-cache, must-revalidate`
  - `Pragma: no-cache`
- **Isolamento de Usuário**: Controllers validam `usuarioId` da sessão
- **HTTPS Pronto**: Pode ser ativado via `application.yml`

---

## 📝 Desenvolvimento

### Compilar
```bash
mvn clean compile
```

### Executar Testes
```bash
mvn test
```

### Verificar Erros
```bash
mvn compile -X
```

### Limpar Build
```bash
mvn clean
```

---

## 🤝 Contribuindo

1. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
2. Commit suas mudanças (`git commit -m 'Add AmazingFeature'`)
3. Push para a branch (`git push origin feature/AmazingFeature`)
4. Abra um Pull Request

## 📞 Suporte

Para dúvidas ou problemas:
- Verificar logs em `target/` ou console
- Consultar `application.yml` para configuração de banco de dados
- Revisar `logback.xml` para níveis de log

---

## 📄 Licença

Este projeto está sob licença MIT. Veja o arquivo LICENSE para detalhes.

---

## 👨‍💻 Autor

Desenvolvido como projeto acadêmico de Software Laboratorio.

**Data**: 04/2026  
**Desenvolvedores**: Lucas Gonçalves Dolabela, Marcos Antunes, Davi Vinicius, Vinicius Zegarra.

---

## 🔄 Histórico de Commits

Consulte o repositório no GitHub para histórico completo:
```bash
git log --oneline
```  
---

## 📄 Histórias de Usuário

### US01 - Cadastro de Usuário
Novo usuário se cadastra informando dados pessoais e até 3 rendimentos para acessar o sistema.

**AC**: Validação de CPF/RG, máximo 3 rendimentos, acesso liberado após login.

### US02 - Novo Pedido de Aluguel
Cliente introduz novo pedido selecionando um veículo e registra com status **Em Análise Financeira**.

**AC**: Veículo deve ser selecionado, pedido registrado corretamente.

### US03 - Gerenciar Pedidos
Cliente consulta, modifica ou cancela seus pedidos.

**AC**: Modificação permitida antes de virar contrato, cancelamento remove da fila.

### US04 - Avaliação Financeira
Agente avalia rendimentos do cliente e emite parecer (positivo/negativo).

**AC**: Visualizar rendimentos, emitir parecer fundamentado.

### US05 - Ajuste de Pedido
Agente modifica detalhes após análise, notificando cliente.

**AC**: Alteração notificada, registro de quem alterou.

### US06 - Contrato de Crédito
Banco associa contrato a aluguel aprovado para financiamento.

**AC**: Contrato vinculado ao pedido, suporte para diferentes tipos.

### US07 - Atualização em Tempo Real
Sistema processa transições de status e atualiza interface via MVC.

**AC**: Arquitetura MVC, View reflete mudanças em tempo real.

### US08 - Histórico de Pedidos
Cliente visualiza histórico completo de pedidos e contratos (somente leitura).

**AC**: Mostrar status, veículo, período.

### US09 - Login Seguro
Usuário realiza login seguro acessando funcionalidades conforme perfil.

**AC**: Validação de credenciais, registro de tentativas inválidas.

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
