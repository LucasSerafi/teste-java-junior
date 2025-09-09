# Teste Prático - Desenvolvedor Java Junior

## Visão Geral

Bem-vindo ao teste prático para a vaga de Desenvolvedor Java Junior! Este teste foi desenvolvido para avaliar suas habilidades técnicas em Java, Spring Boot, desenvolvimento web e boas práticas de programação.

## Objetivo do Teste

Você deverá implementar melhorias e funcionalidades adicionais em um sistema de gerenciamento de produtos já existente. O sistema atual possui funcionalidades básicas de CRUD (Create, Read, Update, Delete) e você precisará expandir suas capacidades.

## Tecnologias Utilizadas

- **Backend**: Java 17, Spring Boot 3.2, Spring Data JPA, Spring Web
- **Banco de Dados**: H2 Database (em memória)
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Build Tool**: Maven
- **Testes**: JUnit 5, Mockito

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/teste/produto/
│   │   ├── ProdutoManagerApplication.java
│   │   ├── controller/
│   │   │   └── ProdutoController.java
│   │   ├── model/
│   │   │   └── Produto.java
│   │   ├── repository/
│   │   │   └── ProdutoRepository.java
│   │   └── service/
│   │       └── ProdutoService.java
│   └── resources/
│       ├── static/
│       │   ├── index.html
│       │   ├── style.css
│       │   └── script.js
│       ├── application.properties
│       └── data.sql
└── test/
    └── java/com/teste/produto/
        └── ProdutoServiceTest.java
```

## Como Executar o Projeto

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior

### Passos para Execução

1. **Clone ou baixe o projeto**
2. **Navegue até o diretório do projeto**
   ```bash
   cd teste-java-junior
   ```

3. **Execute o projeto**
   ```bash
   mvn spring-boot:run
   ```

4. **Acesse a aplicação**
   - Interface Web: http://localhost:8080
   - Console H2: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:testdb`
     - Username: `sa`
     - Password: (deixe em branco)

5. **Execute os testes**
   ```bash
   mvn test
   ```

## Funcionalidades Atuais

O sistema já possui as seguintes funcionalidades implementadas:

### Backend (API REST)
- **GET /api/products** - Listar todos os produtos
- **GET /api/products/{id}** - Buscar produto por ID
- **POST /api/products** - Criar novo produto
- **PUT /api/products/{id}** - Atualizar produto existente
- **DELETE /api/products/{id}** - Deletar produto
- **GET /api/products/search?nome={nome}** - Buscar produtos por nome
- **GET /api/products/low-stock?quantidade={quantidade}** - Buscar produtos com estoque baixo

### Frontend
- Interface web responsiva para gerenciar produtos
- Formulário para adicionar/editar produtos
- Listagem de produtos em tabela
- Funcionalidades de busca
- Validações básicas no frontend

### Modelo de Dados
A entidade `Produto` possui os seguintes campos:
- **id**: Identificador único (Long)
- **nome**: Nome do produto (String, obrigatório, 2-100 caracteres)
- **descricao**: Descrição do produto (String, opcional, máximo 500 caracteres)
- **preco**: Preço do produto (BigDecimal, obrigatório, maior que 0)
- **quantidade**: Quantidade em estoque (Integer, obrigatório, não negativo)

## Tarefas a Serem Implementadas

Você deve escolher e implementar **pelo menos 3 das seguintes tarefas**. Quanto mais tarefas você completar com qualidade, melhor será sua avaliação.

### Tarefa 1: Implementar Categorias de Produtos (Obrigatória)
**Descrição**: Adicionar o conceito de categorias aos produtos.

**Requisitos**:
- Criar entidade `Categoria` com campos: id, nome, descrição
- Relacionar `Produto` com `Categoria` (Many-to-One)
- Criar endpoints REST para gerenciar categorias
- Atualizar a interface web para incluir categorias
- Implementar testes unitários

**Critérios de Avaliação**:
- Modelagem correta do relacionamento JPA
- Implementação completa dos endpoints
- Validações adequadas
- Interface funcional e intuitiva

### Tarefa 2: Sistema de Validações Avançadas
**Descrição**: Implementar validações mais robustas e tratamento de erros.

**Requisitos**:
- Criar validações customizadas (ex: nome único por categoria)
- Implementar tratamento global de exceções
- Adicionar validações no frontend com feedback visual
- Criar mensagens de erro padronizadas
- Implementar logs estruturados

**Critérios de Avaliação**:
- Uso correto de Bean Validation
- Tratamento adequado de exceções
- Experiência do usuário aprimorada
- Qualidade dos logs

### Tarefa 3: Funcionalidades de Relatórios
**Descrição**: Implementar endpoints para gerar relatórios básicos.

**Requisitos**:
- Endpoint para relatório de produtos por categoria
- Endpoint para produtos com estoque crítico (configurável)
- Endpoint para valor total do estoque
- Implementar paginação nos relatórios
- Criar interface para visualizar relatórios

**Critérios de Avaliação**:
- Eficiência das consultas
- Implementação correta da paginação
- Qualidade da apresentação dos dados
- Performance das operações

### Tarefa 4: Testes Automatizados Abrangentes
**Descrição**: Expandir a cobertura de testes do sistema.

**Requisitos**:
- Testes unitários para todas as camadas
- Testes de integração para os controllers
- Testes para validações customizadas
- Testes para cenários de erro
- Configurar relatório de cobertura

**Critérios de Avaliação**:
- Cobertura de código (mínimo 80%)
- Qualidade dos testes (cenários positivos e negativos)
- Uso adequado de mocks
- Organização e legibilidade dos testes

### Tarefa 5: Melhorias na Interface do Usuário
**Descrição**: Aprimorar a experiência do usuário na interface web.

**Requisitos**:
- Implementar confirmações para ações destrutivas
- Adicionar loading states e feedback visual
- Implementar ordenação nas colunas da tabela
- Adicionar filtros avançados
- Melhorar responsividade mobile

**Critérios de Avaliação**:
- Qualidade da experiência do usuário
- Responsividade e acessibilidade
- Código JavaScript organizado
- Design consistente e profissional

### Tarefa 6: Documentação da API
**Descrição**: Implementar documentação automática da API.

**Requisitos**:
- Integrar Swagger/OpenAPI
- Documentar todos os endpoints
- Adicionar exemplos de request/response
- Incluir descrições detalhadas
- Configurar interface Swagger UI

**Critérios de Avaliação**:
- Completude da documentação
- Qualidade das descrições
- Exemplos práticos e úteis
- Facilidade de uso da interface

## Instruções de Entrega

### O que entregar:
1. **Código fonte completo** do projeto
2. **README atualizado** com:
   - Instruções de execução
   - Descrição das funcionalidades implementadas
   - Decisões técnicas tomadas
   - Dificuldades encontradas e como foram resolvidas
3. **Evidências de teste** (screenshots da aplicação funcionando)

### Como entregar:
- Subir para o github 
- Envie por email o link do projeto

### Prazo:
- **Tempo recomendado**: 1 dia

## Recursos Úteis

### Documentação Oficial:
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [H2 Database](http://www.h2database.com/)
- [JUnit 5](https://junit.org/junit5/)

### Ferramentas Recomendadas:
- **IDE**: Eclipse, VS Code
- **Teste de API**: Postman, Insomnia
- **Navegador**: Chrome DevTools para debug frontend
