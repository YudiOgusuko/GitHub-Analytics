# 📊 GitHub Analytics

![Java 17+](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)
![Spring Boot 3.x](https://img.shields.io/badge/Spring_Boot-3.x-green?style=flat-square&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8-blue?style=flat-square&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
![Status](https://img.shields.io/badge/Status-Conclu%C3%ADdo-brightgreen?style=flat-square)

Projeto full-stack para analisar perfis do GitHub, com foco principal em back-end Java.
Ele permite consultar dados de um usuário (repositórios, contribuições, atividade recente, seguidores, linguagens mais usadas) e **acompanhar como o perfil muda ao longo do tempo** através de sincronizações que geram um histórico (snapshots).

---

## 🎯 Objetivo

Este projeto foi criado para praticar e demonstrar:

- Consumo de APIs externas (GitHub REST e GitHub GraphQL)
- Modelagem de DTOs, paginação genérica e tratamento global de erros em uma API REST
- Persistência de dados com Spring Data JPA + MySQL
- Estratégia de cache em banco para reduzir chamadas à API externa
- Validação de entrada com Bean Validation
- Testes unitários com JUnit 5 e Mockito
- Containerização com Docker e Docker Compose
- Integração entre um back-end Java e um front-end web (HTML/CSS/JS)

Mesmo com o foco de carreira em back-end, o front-end foi incluído para tornar o projeto mais completo e demonstrável de ponta a ponta.

---

## 🏗️ Arquitetura do Projeto (back-end)

```
src/main/java/br/GitHub/Analytics/
├── config/          (CorsConfig, RestClientConfig)
├── controller/      (UsuarioController, RepositorioController, AnalisarController)
├── domain/          (Usuario, Repositorio, Snapshot)
├── dto/
│   ├── paginacao/   (PaginaDto)
│   ├── repositorio/
│   ├── snapshot/
│   └── usuario/
├── handler/
│   ├── errorResponse/
│   ├── exception/
│   └── globalHandler/
├── repository/      (IUsuarioRepository, IRepositorioRepository, ISnapshotRepository)
├── service/         (GitHubClient, UsuarioService, RepositorioService,
│                     SnapshotService, SincronizacaoPersistenciaService, Utilitarios)
└── GitHubAnalyticsApplication
```

**Fluxo geral:** `Controller` → `Service` → `GitHubClient` (API do GitHub) e/ou `Repository` (MySQL) → `DTO` de resposta.

---

## 🛠️ Funcionalidades

- 👤 **Perfil do usuário:** nome, bio, linguagem preferida, repositórios, estrelas, seguidores, seguindo e data de criação da conta.
- 📦 **Repositórios:** listagem completa e detalhes de cada repositório (commits, branches, issues, pull requests, forks, estrelas, linguagens e contribuidores).
- 📈 **Contribuições:** total de contribuições, commits, issues e pull requests.
- 🕒 **Atividade recente:** eventos públicos paginados (push, pull request, issues etc.).
- 🤝 **Rede:** seguidores e seguindo, com paginação.
- 🔎 **Busca de usuários** por nome, com paginação.
- 🏆 **Ranking de linguagens** mais usadas pelo usuário.
- 🔄 **Sincronização e histórico:** atualiza os dados salvos e registra um snapshot com o que mudou (repositórios adicionados, atualizados e removidos).
- ✅ **Validação de entrada** nos parâmetros e **tratamento global de exceções** com respostas de erro padronizadas.

---

## ☀️ Consulta e Consumo da API Externa (GitHub)

O back-end consome a API do GitHub com o `RestClient` do Spring, centralizado na classe `GitHubClient`:

| Recurso do GitHub | Uso no projeto |
|---|---|
| REST `users/{username}` | Dados do perfil |
| REST `users/{username}/repos` e `repos/{owner}/{repo}` | Repositórios e detalhes |
| REST `users/{username}/events` | Atividade recente |
| REST `users/{username}/followers` e `following` | Rede de contatos |
| REST `search/users` | Busca de usuários |
| GraphQL | Total de contribuições (número do gráfico de contribuições do perfil) |

- A paginação é genérica (`PaginaDto` + `GitHubClient`) e reaproveitada em seguidores, seguindo, eventos e busca de usuários.
- É necessário gerar um **Personal Access Token (classic)** no GitHub (a consulta GraphQL de contribuições exige esse tipo de token). Ver seção `.env`.

---

## 🗃️ Gestão e Persistência de Dados

- Banco de dados **MySQL**, acessado com **Spring Data JPA**.
- Entidades: `Usuario`, `Repositorio` e `Snapshot` (tabela `tb_snapshot`, ligada ao `Usuario` por `@ManyToOne`).
- **Estratégia de cache em banco:** ao consultar um usuário na API do GitHub, o usuário e seus repositórios são salvos. As consultas seguintes aos repositórios de um usuário já salvo são lidas do banco, economizando chamadas à API (que tem limite de requisições).
- **Sincronização:** o endpoint `POST /api.github/analisar/sincronizar` busca os dados atualizados, compara com o que está salvo, atualiza o banco e grava um `Snapshot` contendo:
    - os dados antigos do usuário (repositórios, seguidores, estrelas, commits, etc.);
    - quantos repositórios foram **adicionados**, **atualizados** e **removidos**;
    - se a tabela de usuários e/ou a de repositórios foram alteradas;
    - data e hora da sincronização.
- O endpoint `GET /api.github/analisar/historico` devolve esses snapshots, permitindo ver como o perfil evoluiu.

---

## 🌐 Endpoints da API

Prefixo base: `http://localhost:8080/api.github`

### Usuários (`/users`)

| Método | Rota | Parâmetros | Descrição |
|---|---|---|---|
| GET | `/users/unico` | `nome` | Perfil de um usuário |
| GET | `/users/multiplos` | `nome`, `pagina`, `tamanho` | Busca de usuários por nome |
| GET | `/users/eventos` | `nome`, `pagina`, `tamanho` | Atividade recente |
| GET | `/users/contribuicoes` | `nome` | Contribuições, commits, issues e PRs |
| GET | `/users/seguidores` | `nome`, `pagina`, `tamanho` | Seguidores |
| GET | `/users/seguindo` | `nome`, `pagina`, `tamanho` | Quem o usuário segue |
| GET | `/users/rankingLinguagens` | `nome` | Linguagens mais usadas |

### Repositórios (`/repos`)

| Método | Rota | Parâmetros | Descrição |
|---|---|---|---|
| GET | `/repos/multiplos` | `usuario` | Todos os repositórios do usuário |
| GET | `/repos/unico` | `usuario`, `repositorio` | Detalhes de um repositório |

### Análise (`/analisar`)

| Método | Rota | Parâmetros | Descrição |
|---|---|---|---|
| POST | `/analisar/sincronizar` | `nome` | Sincroniza dados e grava um snapshot |
| GET | `/analisar/historico` | `nome`, `pagina`, `tamanho` | Histórico de snapshots |

**Regras de validação:** `nome` do usuário com 1 a 39 caracteres; `repositorio` com 1 a 100; `pagina` mínima 1; `tamanho` de 1 a 100 (padrão 30).

### Exemplo de resposta (`GET /users/unico?nome=octocat`)

```json
{
  "userName": "octocat",
  "nome": "The Octocat",
  "bio": "Exemplo de biografia",
  "linguagemPreferida": "Java",
  "repositorios": 8,
  "estrelas": 120,
  "contribuicoes": 350,
  "commits": 300,
  "issues": 10,
  "pullRequests": 5,
  "seguidores": 1500,
  "seguindo": 9,
  "criadoEm": "25 jan. 2011"
}
```

Uma collection do Postman com todas as rotas está disponível em `github-analytics.postman_collection.json`.

---

## 🖥️ Integração com o Front-end

O front-end (HTML/CSS/JavaScript puro, na pasta `frontend/`) usa `fetch()` para consumir os endpoints acima e exibe:

- tela inicial com busca de perfil e busca de usuários;
- perfil com cartões de estatísticas, barras de contribuições e ranking de linguagens;
- lista de repositórios com detalhe de cada um;
- eventos, seguidores e seguindo com paginação;
- histórico com linha do tempo e botão de sincronização.

```
frontend/
├── index.html
├── css/style.css
└── js/app.js
```

**Como conectar:**

- **Opção 1 (mais simples):** copie a pasta `frontend` para `src/main/resources/static/` e acesse `http://localhost:8080/`. Não precisa de CORS.
- **Opção 2:** abra o `frontend/index.html` com a extensão **Live Server** do VS Code. Nesse caso o back-end precisa de CORS liberado para a origem do front-end (classe `CorsConfig`).
- O endereço do back-end fica na constante `API` no início do `frontend/js/app.js`.

---

## 🧰 Tecnologias e ferramentas

- **Back-end:** Java 17, Spring Boot, Spring Web, Spring Data JPA, Bean Validation, RestClient (consumo da API do GitHub), Lombok, Maven
- **Banco de dados:** MySQL
- **Testes:** JUnit 5 e Mockito
- **Front-end:** HTML, CSS, JavaScript (sem frameworks)
- **Infraestrutura:** Docker, Docker Compose
- **API externa:** GitHub REST API e GitHub GraphQL API
- **Ferramentas:** IntelliJ IDEA, Postman, Git/GitHub

---

## 🐋 Docker

O projeto sobe em dois containers via Docker Compose:

- **db:** MySQL, com dados persistidos em um volume Docker.
- **app:** aplicação Spring Boot (`GitHubAnalyticsApplication`), construída a partir do `Dockerfile` do projeto.

> Confira o `docker-compose.yml` do projeto para confirmar os nomes exatos dos serviços e as portas mapeadas. Os exemplos abaixo assumem `app` na porta `8080` e `db` na porta `3306`.

## 🔑 Variáveis de ambiente (`.env`)

Copie `.env.example` para `.env` na raiz do projeto e preencha os valores. **Nunca envie o `.env` para o GitHub.**

```env
GITHUB_TOKEN=seu_token_classic_do_github
DB_NAME=GitHub_Analytics
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
```

Para gerar o token: GitHub → Settings → Developer settings → Personal access tokens → **Tokens (classic)**.

## ▶️ Como executar (Docker)

1. Clone o repositório.
2. Copie `.env.example` para `.env` e preencha os valores.
3. Na raiz do projeto, rode:
```bash
docker-compose up --build
```
4. Aguarde os logs mostrarem que a aplicação subiu (procure por `Started GitHubAnalyticsApplication`).
5. Teste o back-end importando `github-analytics.postman_collection.json` no Postman, Insomnia ou Bruno.
6. Abra o front-end em `http://localhost:8080/` (ou com o Live Server, se não estiver na pasta `static`).

Para rodar em segundo plano: `docker-compose up --build -d`.
Para parar: `docker-compose down` (adicione `-v` para também apagar o volume do banco).

### ❌ Erro "port is already allocated"

Se aparecer um erro como:

```
Bind for 0.0.0.0:8080 failed: port is already allocated
```

outro processo ou container já está usando a porta `8080` (aplicação) ou `3306` (MySQL). Duas soluções:

**🔸 Opção 1 — liberar a porta**
- Windows (PowerShell): `netstat -ano | findstr :8080` (ou `:3306`), depois `taskkill /PID <pid> /F`
- Linux/Mac: `lsof -i :8080` (ou `:3306`), depois `kill -9 <pid>`
- Se for outro container: `docker ps` para achar o nome e `docker stop <nome>`

**🔸 Opção 2 — mudar a porta local**
No `docker-compose.yml`, altere apenas o lado esquerdo do mapeamento:
```yaml
ports:
  - "8081:8080"   # app passa a responder em localhost:8081
  - "3307:3306"   # MySQL passa a responder em localhost:3307
```
Se mudar a porta do `app`, atualize a constante `API` em `frontend/js/app.js`.

---

## ▶️ Como executar (sem Docker)

1. Tenha o JDK 17+ e um MySQL rodando em `localhost:3306` com o banco `GitHub_Analytics` criado.
2. Configure o `.env` (ou as variáveis de ambiente) com o token do GitHub e as credenciais do banco.
3. Rode `./mvnw spring-boot:run` (ou execute `GitHubAnalyticsApplication` pelo IntelliJ).

---

## 👨‍💻 Autor

**Victor**, estudante de Ciência da Computação, em busca da primeira oportunidade como desenvolvedor back-end Java.

GitHub: [github.com/YudiOgusuko](https://github.com/YudiOgusuko)