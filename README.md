# Dog API - Test Automation

Suíte de testes automatizados de API para a [Dog CEO API](https://dog.ceo/dog-api/documentation),
desenvolvida como parte de um desafio técnico de QA.

O objetivo é garantir a qualidade da integração com a Dog API: validar que os endpoints
respondem corretamente, que os dados retornados estão no formato esperado e que a aplicação
se comporta adequadamente em cenários positivos e negativos.

[![API Tests](https://github.com/SEU_USUARIO/qa-automation-challenge-api/actions/workflows/api-tests.yml/badge.svg)](https://github.com/SEU_USUARIO/qa-automation-challenge-api/actions/workflows/api-tests.yml)

> Substitua `SEU_USUARIO` pela sua conta do GitHub após publicar o repositório.

---

## Sumário

- [Stack técnica](#stack-técnica)
- [Endpoints cobertos](#endpoints-cobertos)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Como executar](#como-executar)
- [Relatório de resultados (Allure)](#relatório-de-resultados-allure)
- [Configuração e portabilidade](#configuração-e-portabilidade)
- [Cenários de teste](#cenários-de-teste)
- [Integração contínua (CI)](#integração-contínua-ci)
- [Decisões de design](#decisões-de-design)

---

## Stack técnica

| Camada            | Ferramenta                | Motivo                                                        |
|-------------------|---------------------------|---------------------------------------------------------------|
| Linguagem         | **Java 21 (LTS)**         | Preferência indicada no desafio; ecossistema maduro           |
| Build             | **Maven** + Maven Wrapper | Reprodutível; **não exige Maven instalado** no avaliador      |
| HTTP client       | **REST Assured 5.5**      | Padrão de mercado para testes de API REST                     |
| Test runner       | **JUnit 5 (Jupiter)**     | Testes parametrizados, repetidos e ciclo de vida moderno      |
| Assertions        | **Hamcrest**              | Asserções legíveis e expressivas                              |
| Relatórios        | **Allure**                | Relatório rico, com steps, categorias e metadados             |
| Configuração      | **Owner**                 | Configuração tipada e sobrescrevível por ambiente             |
| Logging           | **SLF4J + Logback**       | Log limpo e configurável                                      |

---

## Endpoints cobertos

| Método | Endpoint                    | Descrição                          |
|--------|-----------------------------|------------------------------------|
| `GET`  | `/breeds/list/all`          | Lista todas as raças e sub-raças   |
| `GET`  | `/breed/{breed}/images`     | Todas as imagens de uma raça       |
| `GET`  | `/breeds/image/random`      | Uma imagem aleatória               |

---

## Arquitetura

O projeto segue uma arquitetura em camadas, com **separação clara de responsabilidades**.
A regra central é: **os testes nunca falam REST Assured diretamente**. Toda comunicação
HTTP é encapsulada na camada de *client*, deixando os testes focados apenas em cenários
e asserções de negócio.

```
                       ┌─────────────────────────────┐
                       │           Testes            │  ← cenários + asserções (JUnit + Hamcrest)
                       │  BreedsListTest, ...        │
                       └──────────────┬──────────────┘
                                      │ usa
                       ┌──────────────▼──────────────┐
                       │        Service Client        │  ← ÚNICA camada que conhece REST Assured
                       │        BreedsClient          │     (métodos de domínio + @Step Allure)
                       └──────────────┬──────────────┘
                                      │ usa
                    ┌─────────────────▼─────────────────┐
                    │               Core                 │  ← specs e paths reutilizáveis
                    │      SpecFactory, Endpoints        │
                    └─────────────────┬─────────────────┘
                                      │ lê
                       ┌──────────────▼──────────────┐
                       │         Configuration        │  ← config tipada (Owner), sobrescrevível
                       │   Configuration, ConfigFactory│
                       └─────────────────────────────┘

           Model (POJOs): BreedsListResponse, ImageListResponse, RandomImageResponse, ApiStatus
```

### Estrutura de pastas

```
.
├── .github/workflows/api-tests.yml     # Pipeline CI (GitHub Actions)
├── .mvn/wrapper/                       # Maven Wrapper (dispensa Maven instalado)
├── mvnw / mvnw.cmd                     # Scripts do wrapper (Unix / Windows)
├── pom.xml                             # Dependências e plugins
├── src/test
│   ├── java/com/agi/qa
│   │   ├── client/BreedsClient.java            # Encapsula REST Assured
│   │   ├── config/Configuration.java           # Config tipada (Owner)
│   │   ├── config/ConfigFactory.java
│   │   ├── core/Endpoints.java                 # Constantes de endpoints
│   │   ├── core/SpecFactory.java               # Request/Response specs
│   │   ├── core/AllureEnvironmentWriter.java   # Metadados do relatório
│   │   ├── model/                              # POJOs de resposta
│   │   └── tests/                              # Testes por endpoint
│   └── resources
│       ├── config.properties                   # Configuração padrão
│       ├── allure.properties
│       ├── logback-test.xml
│       ├── allure/categories.json              # Taxonomia de falhas do Allure
│       └── schemas/                            # JSON Schemas dos contratos de resposta
└── README.md
```

---

## Pré-requisitos

- **JDK 21** instalado e disponível (variável `JAVA_HOME` apontando para o JDK).
- **Não é necessário instalar o Maven** — o projeto usa o Maven Wrapper (`mvnw`),
  que baixa e usa a versão correta automaticamente.
- Acesso à internet (a Dog API é pública e consumida em tempo real).

Verifique o Java:

```bash
java -version
```

---

## Como executar

Clone o repositório e rode os testes com o wrapper. **Não use `mvn`, use `./mvnw`** (ou `mvnw.cmd` no Windows).

### Linux / macOS

```bash
./mvnw test
```

### Windows (PowerShell / CMD)

```powershell
.\mvnw.cmd test
```

> **Dica (Windows):** se o wrapper reclamar de `JAVA_HOME`, defina-o na sessão antes de rodar:
> ```powershell
> $env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.11"
> .\mvnw.cmd test
> ```

Ao final, o resumo dos testes aparece no console e os relatórios ficam em `target/`.

### Executar uma classe ou um teste específico

```bash
./mvnw test -Dtest=BreedImagesTest
./mvnw test -Dtest=BreedImagesTest#shouldReturn404ForNonExistentBreed
```

---

## Relatório de resultados (Allure)

Após rodar os testes, os resultados brutos ficam em `target/allure-results`.

### Gerar e abrir o relatório localmente

```bash
./mvnw allure:serve
```

Esse comando gera o relatório e abre no navegador automaticamente. Para apenas gerar o
HTML estático (em `target/site/allure-maven-plugin`):

```bash
./mvnw allure:report
```

O relatório inclui:

- Resultado de **cada teste** (sucesso / falha) com detalhes.
- **Request e response** de cada chamada HTTP (via filtro do REST Assured).
- **Steps** legíveis de negócio (ex.: *"Get images for breed 'hound'"*).
- **Severidade**, **Epic/Feature/Story** e **metadados de ambiente**.
- **Categorias de falha** (defeito de produto, contrato, performance, infraestrutura).

> Em relatórios de execução via CI, o Allure é publicado automaticamente (ver seção de CI).

### Relatório de execução mais recente

```
Tests run: 36, Failures: 0, Errors: 0, Skipped: 0

- BreedImagesTest .... 17 testes  ✔
- ApiContractTest .... 7 testes   ✔
- BreedsListTest ..... 6 testes   ✔
- RandomImageTest .... 6 testes   ✔
```

---

## Configuração e portabilidade

Toda a configuração fica em `src/test/resources/config.properties` e pode ser
**sobrescrita em tempo de execução** via *system property* ou variável de ambiente —
o que torna a suíte portável entre máquinas e ambientes de CI.

| Chave                   | Padrão              | Descrição                          |
|-------------------------|---------------------|------------------------------------|
| `base.uri`              | `https://dog.ceo`   | URI base da API                    |
| `base.path`             | `/api`              | Path base prefixado nos endpoints  |
| `timeout.connection.ms` | `10000`             | Timeout de conexão (ms)            |
| `timeout.socket.ms`     | `30000`             | Timeout de socket (ms)             |
| `http.logging.enabled`  | `false`             | Log verboso de request/response    |

Exemplo de sobrescrita:

```bash
./mvnw test -Dbase.uri=https://dog.ceo -Dhttp.logging.enabled=true
```

---

## Cenários de teste

A suíte cobre cenários **positivos**, **negativos**, **data-driven**, **de contrato
(JSON Schema)** e de **performance leve**. Cada um dos três endpoints tem validação de
**schema** e de **contrato** (status code, estrutura da resposta e content-type).

### `GET /breeds/list/all` — `BreedsListTest`
- Retorna `200` com corpo JSON e `status = success`.
- Retorna um mapa de raças não vazio (desserializado em POJO tipado).
- Contém raças conhecidas e estáveis (`hound`, `bulldog`, `retriever`).
- Sub-raças de `bulldog` são uma lista válida e sem entradas em branco.
- Responde dentro de um tempo aceitável (guard de regressão de performance).
- **Schema:** a resposta valida contra `schemas/breeds-list-schema.json`.

### `GET /breed/{breed}/images` — `BreedImagesTest`
- **Positivo:** raça válida retorna `200` e lista não vazia de imagens.
- **Formato:** toda URL retornada é uma URL de imagem válida (regex).
- **Data-driven:** múltiplas raças conhecidas retornam imagens (`@ParameterizedTest`).
- **Negativo:** raça inexistente retorna `404` com `status = error`.
- **Negativo data-driven:** entradas inválidas (nome com espaço, numérico, desconhecido,
  caracteres especiais) retornam `404`.
- **Contrato de erro:** o `404` é JSON, com `status = error` e `message` não nulo.
- **Comportamento documentado:** a busca por raça é *case-insensitive*
  (`HOUND`, `Hound`, `hOuNd` retornam o mesmo dataset de `hound`).
- **Schema:** a resposta valida contra `schemas/image-list-schema.json`.

### `GET /breeds/image/random` — `RandomImageTest`
- Retorna `200`, `status = success` e uma única URL de imagem.
- A URL retornada é uma URL de imagem válida.
- Chamadas repetidas retornam imagens válidas de forma consistente (`@RepeatedTest`).
- **Schema:** a resposta valida contra `schemas/random-image-schema.json`.

### Contrato transversal e rotas desconhecidas — `ApiContractTest`
- Rotas inexistentes retornam `404` (data-driven).
- Um `404` com corpo JSON respeita o contrato de erro (`status = error`).
- Todos os endpoints de sucesso respondem `200` com content-type `application/json`
  (data-driven cobrindo os três endpoints).

---

## Integração contínua (CI)

O workflow [`.github/workflows/api-tests.yml`](.github/workflows/api-tests.yml) executa
automaticamente em `push` e `pull_request` para `main`/`master`, e também sob demanda
(`workflow_dispatch`). Ele:

1. Configura o **JDK 21** (Temurin) com cache de dependências Maven.
2. Roda a suíte com `./mvnw -B test`.
3. Publica como **artefatos**: relatórios Surefire, `allure-results` e o HTML do Allure.
4. No branch `main`, publica o **relatório Allure no GitHub Pages**.

> Para habilitar a publicação no GitHub Pages: em *Settings → Pages*, selecione
> **GitHub Actions** como source.

O código é executável em **Linux, Windows e macOS** — tanto localmente quanto no runner
do CI — graças ao Maven Wrapper.

---

## Decisões de design

- **Encapsulamento do REST Assured no `BreedsClient`.** Os testes não conhecem detalhes
  de HTTP; se a API mudar (URL, autenticação, headers), o impacto fica isolado em uma
  única camada.
- **Specs reutilizáveis (`SpecFactory`).** Base URI, timeouts, `Accept` e o filtro do
  Allure ficam centralizados, evitando duplicação e divergências entre testes.
- **Configuração tipada e sobrescrevível (Owner).** Sem *magic strings*; a mesma suíte
  roda em qualquer ambiente apenas trocando propriedades.
- **POJOs tipados.** As respostas são desserializadas em modelos, o que torna as
  asserções mais seguras e legíveis do que navegar sempre pelo JSON cru.
- **Asserções de contrato + formato + comportamento.** Além do status HTTP, validamos a
  estrutura da resposta (`status`), o formato dos dados (URLs de imagem) e o comportamento
  negativo (`404` para raça inexistente).
- **Relatório autossuficiente.** `environment.properties` e `categories.json` são
  escritos programaticamente, garantindo metadados e taxonomia de falhas em qualquer máquina.

---

## Autor

Desenvolvido como parte de um desafio técnico de QA — automação de testes de API.
