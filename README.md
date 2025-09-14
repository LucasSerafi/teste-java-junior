# Como Executar o Projeto

Este é um guia completo para configurar, executar e testar a aplicação de gerenciamento de produtos.

## Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior
- Git

## Clonando o Projeto

```bash
git clone <url-do-repositorio>
cd teste-java-junior
```

## Configuração

### 1. Banco de Dados

O projeto está configurado para usar H2 Database (banco em memória) por padrão, então não é necessária configuração adicional de banco de dados.

### 2. Configurações da Aplicação

As configurações estão no arquivo `src/main/resources/application.properties`. As principais configurações incluem:

- Porta da aplicação: `8080`
- Configurações do H2 Database
- Configurações de logging

## Executando a Aplicação

### Opção 1: Via Maven
```bash
mvn spring-boot:run
```

### Opção 2: Compilar e executar o JAR
```bash
# Compilar o projeto
mvn clean package

# Executar o JAR gerado
java -jar target/produto-manager-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em: `http://localhost:8080`

## Acessando a Aplicação

### Interface Web
- **URL**: `http://localhost:8080`
- A aplicação possui uma interface web simples para gerenciar produtos e categorias

### API REST
Base URL: `http://localhost:8080/api`

#### Endpoints de Categorias (`/api/categorias`)
- `GET /api/categorias` - Listar todas as categorias
- `GET /api/categorias/{id}` - Buscar categoria por ID
- `POST /api/categorias` - Criar nova categoria
- `PUT /api/categorias/{id}` - Atualizar categoria
- `DELETE /api/categorias/{id}` - Deletar categoria

#### Endpoints de Produtos (`/api/products`)
- `GET /api/products` - Listar todos os produtos
- `GET /api/products/{id}` - Buscar produto por ID
- `POST /api/products` - Criar novo produto
- `PUT /api/products/{id}` - Atualizar produto
- `DELETE /api/products/{id}` - Deletar produto
- `GET /api/products/search?nome={nome}` - Buscar produtos por nome
- `GET /api/products/low-stock?quantidade={qtd}&page={page}&size={size}` - Produtos com estoque baixo (paginado)
- `GET /api/products/stock-value` - Calcular valor total do estoque
- `GET /api/products/category/{categoriaId}?page={page}&size={size}` - Produtos por categoria (paginado)

### Documentação da API (Swagger)
- **URL**: `http://localhost:8080/swagger-ui.html`
- Interface interativa para testar os endpoints da API

### Console do H2 Database
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (deixar em branco)

## Executando os Testes

### Todos os Testes
```bash
mvn test
```

### Testes Específicos
```bash
# Testes de uma classe específica
mvn test -Dtest=ProdutoServiceTest

# Testes de um método específico
mvn test -Dtest=ProdutoServiceTest#testSalvarProduto
```

### Relatório de Cobertura (se configurado)
```bash
mvn jacoco:report
```

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/teste/produto/
│   │       ├── config/          # Configurações
│   │       ├── controller/      # Controllers REST
│   │       ├── dto/            # Data Transfer Objects
│   │       ├── exception/       # Tratamento de exceções
│   │       ├── model/          # Entidades JPA
│   │       ├── repository/     # Repositórios JPA
│   │       └── service/        # Lógica de negócio
│   └── resources/
│       ├── static/             # Arquivos estáticos (HTML, CSS, JS)
│       ├── application.properties
│       └── data.sql           # Dados iniciais
└── test/
    └── java/
        └── com/teste/produto/
            ├── controller/     # Testes dos controllers
            └── service/       # Testes dos services
```

## Dados de Exemplo

O projeto inclui dados de exemplo que são carregados automaticamente no banco H2:

### Categorias
- Eletrônicos
- Roupas
- Casa e Jardim

### Produtos
- Smartphone Samsung Galaxy S21
- Notebook Dell Inspiron
- Camiseta Polo
- Sofá 3 lugares
- E outros...

## Troubleshooting

### Erro de Porta em Uso
Se a porta 8080 estiver em uso, você pode alterar no `application.properties`:
```properties
server.port=8081
```

### Erro de Memória
Se houver problemas de memória, execute com mais heap:
```bash
java -Xmx512m -jar target/produto-manager-0.0.1-SNAPSHOT.jar
```

### Logs
Para ver mais detalhes nos logs, altere o nível no `application.properties`:
```properties
logging.level.com.teste.produto=DEBUG
```

## Funcionalidades Principais

### Gerenciamento de Categorias
- CRUD completo de categorias
- Validação de nome único
- Proteção contra exclusão de categorias com produtos

### Gerenciamento de Produtos
- CRUD completo de produtos
- Busca por nome
- Filtros por categoria
- Controle de estoque
- Relatório de produtos com estoque baixo
- Cálculo do valor total do estoque
- Paginação em listagens

### Validações
- Campos obrigatórios
- Valores numéricos positivos
- Relacionamentos entre entidades
- Tratamento de erros com mensagens personalizadas

### Interface Web
- Interface responsiva
- Operações CRUD via formulários
- Listagens dinâmicas
- Feedback visual para o usuário

## Tecnologias Utilizadas

- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco de dados em memória
- **Spring Web** - REST API
- **Spring Validation** - Validação de dados
- **Swagger/OpenAPI** - Documentação da API
- **JUnit 5** - Testes unitários
- **Mockito** - Mocks para testes
- **Maven** - Gerenciamento de dependências

## Bibliotecas e Decisões de Arquitetura

### Principais Dependências Maven

#### Core Framework
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
**Justificativa**: Fornece toda a infraestrutura necessária para criar APIs REST, incluindo servidor embarcado (Tomcat), Spring MVC e Jackson para serialização JSON.

#### Persistência de Dados
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```
**Justificativa**:
- **Spring Data JPA**: Simplifica operações de banco de dados com repositories automáticos e redução de boilerplate code
- **H2 Database**: Escolhido para desenvolvimento e testes por ser em memória, não requerer instalação externa e permitir reset automático entre execuções

#### Validação
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```
**Justificativa**: Implementa Bean Validation (JSR-303/380) para validações declarativas com anotações, proporcionando código mais limpo e consistente.

#### Documentação da API
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
</dependency>
```
**Justificativa**: Gera automaticamente documentação OpenAPI 3.0 e interface Swagger UI, essencial para APIs REST bem documentadas.

#### Testes
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```
**Justificativa**: Inclui JUnit 5, Mockito, Spring Test e outras ferramentas essenciais para testes unitários e de integração.

### Decisões Arquiteturais

#### Arquitetura em Camadas
```
Controller → Service → Repository → Database
```

**Justificativa**:
- **Controllers**: Responsáveis apenas pela comunicação HTTP, validação de entrada e serialização de resposta
- **Services**: Contêm toda a lógica de negócio, regras de validação customizadas e orquestração de operações
- **Repositories**: Abstração para acesso a dados, permitindo mudança de persistência sem impacto nas outras camadas
- **Models**: Entidades JPA que representam o modelo de domínio

#### Padrão Repository
```java
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByQuantidadeLessThanEqual(Integer quantidade, Pageable pageable);
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    // Métodos customizados quando necessário
}
```

**Justificativa**: Spring Data JPA gera automaticamente implementações baseadas no nome dos métodos, reduzindo código boilerplate e garantindo consistência.

#### Tratamento Global de Exceções
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        // Tratamento centralizado
    }
}
```

**Justificativa**: Centraliza tratamento de erros, padroniza respostas de erro e melhora manutenibilidade.

#### DTOs para Respostas de Erro
```java
public class ErrorResponse {
    private String mensagem;
    private LocalDateTime timestamp;
    private List<FieldError> campos;
}
```

**Justificativa**: Padroniza estrutura de erros, fornece informações consistentes para o frontend e melhora experiência do desenvolvedor.

#### Paginação
```java
@GetMapping("/low-stock")
public ResponseEntity<Page<Produto>> buscarProdutosComQuantidadeBaixa(
    @RequestParam(defaultValue = "10") Integer quantidade,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    Pageable pageable) {
    // Implementação
}
```

**Justificativa**: Essencial para performance em listagens grandes, implementada nativamente pelo Spring Data.

#### Validações Declarativas
```java
public class Produto {
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private BigDecimal preco;
}
```

**Justificativa**: Validações declarativas são mais legíveis, reutilizáveis e integradas ao framework.

#### Relacionamentos JPA
```java
@Entity
public class Produto {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}

@Entity
public class Categoria {
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Produto> produtos = new ArrayList<>();
}
```

**Justificativa**:
- **FetchType.LAZY**: Evita carregamento desnecessário de dados relacionados
- **Bidirectional Mapping**: Facilita navegação entre entidades quando necessário

#### Configuração de CORS
```java
@CrossOrigin(origins = "*")
```

**Justificativa**: Permite acesso da interface web local durante desenvolvimento. Em produção, deve ser configurado com origens específicas.

#### Logs Estruturados
```java
@Service
public class ProdutoService {
    private static final Logger logger = LoggerFactory.getLogger(ProdutoService.class);

    public Produto salvar(Produto produto) {
        logger.info("Salvando produto: {}", produto.getNome());
        // Implementação
    }
}
```

**Justificativa**: Facilita debugging, monitoramento e auditoria da aplicação.

### Choices Arquiteturais Importantes

1. **Banco H2 em Memória**: Escolhido para simplificar setup de desenvolvimento e permitir testes isolados
2. **Responsive Web Design**: Interface adaptável sem frameworks pesados como Bootstrap
3. **REST API Stateless**: Facilita escalabilidade e integração com diferentes frontends
4. **Testes em Múltiplas Camadas**: Cobertura abrangente com testes unitários (services) e de integração (controllers)
5. **Documentação Automática**: Swagger integrado para facilitar consumo da API por outros desenvolvedores




---
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
