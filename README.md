# SIGAPAR — Sistema de Atendimento Genérico

## Visão Geral

O **SIGAPAR** é um Sistema de Atendimento Genérico projetado para organizar, controlar e registrar atendimentos realizados em estabelecimentos que operam com **profissionais**, **horários** e **espaços físicos** (como cadeiras, salas ou consultórios).

O sistema é flexível e pode ser utilizado em diferentes tipos de negócios, como:

* Barbearias e salões de beleza
* Clínicas médicas e odontológicas
* Academias e estúdios
* Escritórios de serviços em geral

---

## Minimundo — Sistema de Atendimento Genérico

O Sistema de Atendimento tem como objetivo organizar e registrar todo o processo de atendimentos realizados em um estabelecimento.

Ele permite que:

* **Clientes** realizem agendamentos de serviços
* **Profissionais** acompanhem e gerenciem suas agendas
* **Administradores** controlem usuários, recursos, horários e relatórios

O sistema registra cada atendimento contendo:

* Cliente
* Profissional responsável
* Serviço
* Local de atendimento (cadeira, sala, consultório etc.)
* Data e horário
* Status do atendimento

---

## Objetivo do Sistema

* Centralizar o controle de agendamentos e atendimentos
* Evitar conflitos de horário
* Melhorar a organização do estabelecimento
* Oferecer uma interface simples para diferentes tipos de usuários
* Gerar relatórios para apoiar a gestão de tempo e recursos

---

## Funcionamento Geral

1. O **cliente** acessa o sistema e escolhe:

   * O tipo de serviço
   * O profissional (ou deixa o sistema escolher automaticamente)
   * Um horário disponível

2. Cada profissional está associado a um **espaço físico** de atendimento em determinado horário.

3. Após a confirmação do agendamento, o sistema registra:

   * Cliente
   * Profissional
   * Serviço
   * Local
   * Data e hora

4. O **profissional** pode:

   * Visualizar sua agenda diária
   * Confirmar atendimentos
   * Marcar atendimentos como concluídos

5. O **administrador** pode:

   * Acompanhar todos os agendamentos
   * Gerenciar usuários
   * Cancelar atendimentos
   * Gerar relatórios de produtividade

---

## Criação Automática do Usuário Administrador

O sistema possui um mecanismo automático de criação do **usuário administrador**, implementado na classe:

```
br.com.sigapar1.config.StartupAdmin
```

Essa classe é executada automaticamente na inicialização da aplicação (`@Startup`).

### O que ela faz?

* Verifica se já existe algum usuário com a role **ADMIN**
* Caso não exista, cria automaticamente um usuário administrador padrão
* As credenciais podem ser configuradas via **variáveis de ambiente**

### Credenciais padrão (caso não configuradas)

* **Email:** `admin@sigapar.com`
* **Senha:** `1234`

> ⚠️ Recomenda-se alterar a senha em ambiente de produção.

### Variáveis de ambiente suportadas

* `SIGAPAR_ADMIN_EMAIL` → Email do administrador
* `SIGAPAR_ADMIN_PASS` → Senha do administrador
* `SIGAPAR_RESET_ADMIN_PASS` → Se definido como `true`, reseta a senha do admin existente

---

## Papéis de Usuário (Roles)

O sistema trabalha com diferentes tipos de usuários:

### 👑 Administrador (ADMIN)

* Criado automaticamente pelo sistema
* Pode:

  * Criar usuários do tipo **Atendente** e **Recepcionista**
  * Gerenciar profissionais, serviços e horários
  * Visualizar todos os agendamentos
  * Gerar relatórios

### 🧾 Atendente / Recepcionista

* Criados **exclusivamente pelo Administrador**
* Responsáveis por:

  * Auxiliar no gerenciamento de atendimentos
  * Apoiar clientes presencialmente
  * Confirmar ou organizar agendamentos

### 👤 Usuário Comum (Cliente)

* **Não é criado pelo administrador**
* Deve se cadastrar pelo próprio sistema

➡️ Para criar um usuário comum, basta acessar a opção **“Cadastrar”** na tela inicial do sistema.

---

## Fluxo de Criação de Usuários (Resumo)

1. O sistema inicia
2. O **Administrador** é criado automaticamente
3. O Administrador acessa o sistema
4. O Administrador cria usuários:

   * Atendente
   * Recepcionista
5. Clientes se cadastram pela opção **Cadastrar**
6. Clientes passam a realizar agendamentos

---

## Considerações Finais

O SIGAPAR foi projetado para ser simples, organizado e flexível, atendendo desde pequenos negócios até estabelecimentos com maior volume de atendimentos.

Ele separa claramente as responsabilidades de cada tipo de usuário, garantindo segurança, organização e escalabilidade.

---

📌 **Projeto acadêmico / profissional com foco em organização de atendimentos e gestão de agendas.**
