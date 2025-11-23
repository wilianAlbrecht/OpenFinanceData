# OpenFinanceData

Cliente Java moderno para coleta de dados brutos do Yahoo Finance, projetado para ser estável, modular e fácil de integrar em outros serviços — incluindo APIs que calculam indicadores financeiros.

## Por que este projeto existe

A maioria dos desenvolvedores Java enfrenta um problema comum:

* APIs gratuitas raramente fornecem dados fundamentais completos;
* Muitos serviços exigem assinaturas caras;
* As bibliotecas Java existentes cobrem apenas preços históricos — não fundamentals;
* Python possui ferramentas excelentes (como *yfinance*), mas Java não.

**OpenFinanceData resolve esse problema.**
Ele permite que desenvolvedores, analistas e estudantes acessem dados financeiros diretamente dos endpoints públicos do Yahoo, sem limites de requisição e sem API keys.

### O que você pode fazer com OpenFinanceData

* Obter **quotes em tempo real**;
* Buscar **histórico de preços** (candles OHLC);
* Coletar dados fundamentais brutos;
* 100% open source e gratuito;
* Ideal para sistemas backend, ferramentas de trading, dashboards e projetos educacionais.
  O **OpenFinanceData** tem como propósito:
* Obter **dados brutos** do Yahoo Finance (quotes, históricos, fundamentals, earnings, profile);
* Utilizar uma arquitetura **estável**, baseada em cookies e crumb atualizados dinamicamente;
* Servir como camada de dados para projetos como análise de indicadores, dashboards ou APIs REST;
* Ser totalmente acessível por HTTP via endpoints já prontos.

## Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3** (Web, WebFlux, Validation, Security)
* **Java HttpClient** (simulação de navegador)
* **CookieManager** + **CrumbManager** (gestão completa de sessão)
* **Selenium** (captura inicial de cookies quando necessário)
* **Jackson**
* **Maven Wrapper**
* **Lombok**
* **JUnit 5** + **Mockito**

## Arquitetura Interna

```
openfinancedata/
 ├── external/
 │    ├── yahoo/
 │    │     ├── client/ (requisições HTTP reais)
 │    │     ├── models/ (dados retornados)
 │    │     ├── crumb/ (lógica de crumb + cookies)
 │    │     └── parser/ (tratamento de JSON instável)
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

1. Coleta de cookies visitando `fc.yahoo.com` ou `finance.yahoo.com`;
2. Obtenção do **crumb** usando headers completos simulando navegador;
3. Validação se o crumb é string e não JSON de erro;
4. Revalidação periódica de cookies e crumb;
5. Todas as requisições usam o mesmo contexto de sessão.

Este fluxo garante que o cliente continue funcionando mesmo com restrições recentes impostas pelo Yahoo.

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

Os endpoints ficam em:

```
http://localhost:8080/api/yahoo/
```

## Endpoints Disponíveis

A API expõe múltiplos endpoints para acessar diferentes tipos de dados do Yahoo Finance. Abaixo está a lista completa, incluindo seus parâmetros.

### Quotes

`GET /api/yahoo/quote/{symbol}`

* **symbol**: ticker da ação (ex: PETR4, AAPL, MSFT)

### Demonstrativos Financeiros (Financials)

`GET /api/yahoo/financials/{symbol}`

* **symbol**: ticker da empresa
* Retorna: Income Statement, Balance Sheet, Cash Flow

### Perfil da Empresa (Profile)

`GET /api/yahoo/profile/{symbol}`

* **symbol**: ticker da empresa

### Earnings (Resultados)

`GET /api/yahoo/earnings/{symbol}`

* **symbol**: ticker
* Retorna earnings anuais e trimestrais

### Histórico (Historical Prices)

`GET /api/yahoo/history/{symbol}`

* **symbol**: ticker
* **range** (opcional): período

  * aceito: `1d`, `5d`, `1mo`, `3mo`, `6mo`, `1y`, `2y`, `5y`, `10y`, `ytd`, `max`
* **interval** (opcional): intervalo dos candles

  * aceito: `1m`, `2m`, `5m`, `15m`, `1h`, `1d`, `1wk`, `1mo`

### Utilidades Internas

`GET /api/yahoo/status`

* Retorna informações de saúde do client, cookies e crumb

### Atualização Manual de Sessão

`POST /api/yahoo/refresh`

* Força atualização de cookies e crumb/{symbol}?range=1y` – histórico de preços

## Campos Retornados pela API

A API retorna uma ampla variedade de dados brutos diretamente dos endpoints do Yahoo Finance. Abaixo está a lista dos principais campos disponibilizados:

### Quote (Cotação em Tempo Real)

* Preço atual
* Variação absoluta
* Variação percentual
* Preço de abertura
* Máxima e mínima do dia
* Volume
* Market Cap
* Preço médio do dia
* Fechamento anterior
* Tipo do mercado / estado

### Histórico (OHLCV)

* Open
* High
* Low
* Close
* Volume
* Data/hora do candle
* Ajuste de preço

### Demonstrativos Financeiros (Fundamentals)

* Income Statement (Demonstração de Resultados)

  * Revenue
  * Net Income
  * Gross Profit
  * Operating Income
  * EPS
* Balance Sheet (Balanço Patrimonial)

  * Total Assets
  * Total Liabilities
  * Shareholder Equity
  * Cash & Equivalents
* Cash Flow Statement (Fluxo de Caixa)

  * Operating Cash Flow
  * Investing Cash Flow
  * Financing Cash Flow
  * Free Cash Flow

### Estatísticas (Key Statistics)

* P/E Ratio
* Forward P/E
* Beta
* Price to Book
* Dividend Yield
* Earnings Date
* PEG Ratio
* EPS (TTM)

### Perfil da Empresa (Company Profile)

* Nome da empresa
* Setor
* Indústria
* Descrição do negócio
* Número de funcionários
* Endereço completo
* Localização
* Website oficial

## Agradecimento

Obrigado pelo interesse no OpenFinanceData! O foco principal do projeto é **manter um cliente Java estável**, resiliente às constantes mudanças do Yahoo Finance, garantindo que os dados continuem acessíveis mesmo quando o Yahoo altera endpoints, cookies, headers ou estruturas internas.

A prioridade é a **manutenção contínua**, correções rápidas e melhorias na robustez — acima de novas features — para que o OpenFinanceData permaneça confiável a longo prazo.
