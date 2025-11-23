# OpenFinanceData

OpenFinanceData é um cliente Java criado para oferecer acesso confiável, direto e estruturado aos dados públicos do Yahoo Finance. O objetivo é simples: **entregar uma ferramenta técnica, estável e fácil de integrar**, especialmente útil em ambientes onde precisão e consistência são fundamentais.

## Por que este projeto existe

O mercado carece de uma forma simples, consistente e acessível de obter dados financeiros completos a partir de fontes públicas. Muitas APIs gratuitas entregam apenas informações básicas, enquanto serviços mais completos exigem assinaturas caras. O OpenFinanceData foi criado para suprir essa necessidade: uma solução direta e estável que centraliza dados fundamentais, estatísticas, resultados, perfil corporativo e histórico de preços em um único ponto de acesso, utilizável por qualquer aplicação capaz de fazer requisições HTTP.

## O que você pode fazer com OpenFinanceData

* Consultar cotações em tempo real.
* Obter histórico de preços com diferentes períodos e intervalos.
* Acessar dados fundamentais (resultados, balanços, fluxo de caixa, etc.).
* Consultar estatísticas de mercado.
* Obter informações completas de perfil corporativo.
* Integrar facilmente esses dados em APIs, sistemas de análise, dashboards ou ferramentas de trading.

## Tecnologias Utilizadas

* Java 21
* Spring Boot 3 (Web, WebFlux, Validation, Security)
* Java HttpClient (simulação de navegador)
* CookieManager e CrumbManager (gestão de sessão)
* Selenium (captura inicial de cookies quando necessário)
* Jackson
* Maven Wrapper
* Lombok
* JUnit 5 e Mockito

## Arquitetura Interna

```
openfinancedata/
 ├── external/
 │    ├── yahoo/
 │    │     ├── client/        # Requisições HTTP reais
 │    │     ├── models/        # Estruturas de dados
 │    │     ├── crumb/         # Lógica de cookies + crumb
 │    │     └── parser/        # Tratamento de respostas instáveis
 │    └── ...
 ├── service/
 │    ├── YahooDataService
 │    └── DataRefreshService
 ├── controller/
 │    ├── QuoteController
 │    ├── FinancialsController
 │    ├── EarningsController
 │    ├── ProfileController
 │    └── HistoryController
 └── config/
      ├── HttpClientProvider
      └── SeleniumProvider
```

## Como o OpenFinanceData trabalha com o Yahoo Finance

O Yahoo Finance utiliza mecanismos de proteção como cookies, headers específicos e o famoso **crumb**, necessário para validar requisições mais sensíveis.

O OpenFinanceData resolve isso automaticamente por meio de um fluxo consistente:

1. Captura inicial de cookies visitando os domínios do Yahoo.
2. Solicitação do crumb usando cabeçalhos que simulam um navegador real.
3. Validação da resposta para garantir consistência.
4. Renovação periódica da sessão para evitar falhas.
5. Reutilização do mesmo contexto em todas as requisições.

Essa abordagem mantém o cliente funcional mesmo quando o Yahoo altera detalhes internos.

## Como Executar o Projeto

### 1. Clonar

```
git clone https://github.com/SEU_USUARIO/openfinancedata
cd openfinancedata
```

### 2. Executar via Maven Wrapper

```
./mvnw spring-boot:run
```

### 3. Acessar a API

```
http://localhost:8080/api/yahoo/
```

## Endpoints Disponíveis

Abaixo estão todos os endpoints fornecidos pela API, com seus parâmetros.

### Quotes

`GET /api/yahoo/quote/{symbol}`

* **symbol** — ticker da ação

### Demonstrativos Financeiros

`GET /api/yahoo/financials/{symbol}`

* **symbol** — ticker da empresa
* Inclui: *Income Statement*, *Balance Sheet*, *Cash Flow*

### Perfil da Empresa

`GET /api/yahoo/profile/{symbol}`

* **symbol** — ticker

### Earnings

`GET /api/yahoo/earnings/{symbol}`

* **symbol** — ticker
* Retorna resultados anuais e trimestrais

### Histórico de Preços

`GET /api/yahoo/history/{symbol}`

* **symbol** — ticker
* **range** (opcional): 1d, 5d, 1mo, 3mo, 6mo, 1y, 2y, 5y, 10y, ytd, max
* **interval** (opcional): 1m, 2m, 5m, 15m, 1h, 1d, 1wk, 1mo

### Utilidades Internas

`GET /api/yahoo/status`

* Retorna dados sobre sessão atual (cookies, crumb, etc.)

### Atualização Manual da Sessão

`POST /api/yahoo/refresh`

* Força uma atualização de cookies e crumb

## Campos Retornados pela API

A API retorna diversos conjuntos de dados. Aqui estão os principais grupos.

### Quote (tempo real)

* Preço atual
* Variação absoluta
* Variação percentual
* Abertura
* Máxima e mínima
* Volume
* Market Cap
* Preço médio
* Fechamento anterior

### Histórico (OHLCV)

* Open
* High
* Low
* Close
* Volume
* Timestamp
* Ajustes

### Demonstrativos Financeiros

**Income Statement**

* Receita
* Lucro líquido
* EPS
* Lucro bruto
* Resultado operacional

**Balance Sheet**

* Ativos totais
* Passivos totais
* Patrimônio
* Caixa e equivalentes

**Cash Flow**

* Fluxo de caixa operacional
* Fluxo de caixa de investimento
* Fluxo de caixa de financiamento
* Free Cash Flow

### Estatísticas

* P/E
* Forward P/E
* Beta
* Price to Book
* Dividend Yield
* Earnings Date
* EPS (TTM)
* PEG Ratio

### Perfil da Empresa

* Nome
* Setor
* Indústria
* Endereço
* Funcionários
* Website oficial
* Descrição

## Agradecimento

Obrigado por utilizar o OpenFinanceData. O foco do projeto é **manter uma solução sólida e confiável**, reagindo rapidamente a mudanças do Yahoo Finance e garantindo estabilidade de longo prazo. A manutenção contínua é prioridade absoluta.
