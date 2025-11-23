🇧🇷 [Português](README.md)  | 🇺🇸 [English](README.en.md) | 🇨🇳 [中文](README.zh.md)

# OpenFinanceData

OpenFinanceData is a Java client designed to provide reliable, direct, and structured access to Yahoo Finance public data. The goal is straightforward: **deliver a technical, stable, and easy‑to‑integrate tool**, especially useful in environments where precision and consistency are essential.

## Why this project exists

The market lacks a simple, consistent, and accessible way to obtain complete financial data from public sources. Many free APIs offer only limited or inconsistent information, while more advanced solutions often require expensive subscriptions. OpenFinanceData was created to address this gap: a stable solution that centralizes fundamental data, statistics, earnings, corporate profile information, and historical prices in a single, organized point of access. It can be used by any system or language capable of making HTTP requests.

## What you can do with OpenFinanceData

* Retrieve real‑time quotes.
* Get historical price data across different ranges and intervals.
* Access full fundamental data (financial statements, results, cash flow, etc.).
* Retrieve market statistics.
* Access complete corporate profile information.
* Integrate all these datasets into APIs, analytics systems, dashboards, or trading tools.

## Technologies Used

* Java 21
* Spring Boot 3 (Web, WebFlux, Validation, Security)
* Java HttpClient (browser‑like request simulation)
* CookieManager and CrumbManager (session management)
* Selenium (initial cookie capture when required)
* Jackson
* Maven Wrapper
* Lombok
* JUnit 5 and Mockito

## Internal Architecture

```
openfinancedata/
 ├── external/
 │    ├── yahoo/
 │    │     ├── client/        # Actual HTTP requests
 │    │     ├── models/        # Data structures
 │    │     ├── crumb/         # Cookie + crumb logic
 │    │     └── parser/        # Handling of unstable Yahoo responses
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

## How OpenFinanceData interacts with Yahoo Finance

Yahoo Finance uses protection mechanisms such as cookies, specific headers, and the known **crumb**, required for accessing certain endpoints.

OpenFinanceData handles this automatically through a consistent process:

1. Initial cookie capture by visiting Yahoo domains.
2. Crumb retrieval using headers that mimic a real browser.
3. Response validation to ensure consistency.
4. Periodic session renewal to prevent failures.
5. Reuse of the same session context across all requests.

This approach keeps the client functional even when Yahoo changes internal behaviors.

## How to Run the Project

### 1. Clone the repository

```
git clone https://github.com/wilianAlbrecht/OpenFinanceData
cd openfinancedata
```

### 2. Run with Maven Wrapper

```
./mvnw spring-boot:run
```

### 3. Access the API

```
http://localhost:8080/api/yahoo/
```

## Available Endpoints

Below are all API endpoints along with their parameters.

### Quotes

`GET /api/yahoo/quote/{symbol}`

* **symbol** — stock ticker

### Financial Statements

`GET /api/yahoo/financials/{symbol}`

* **symbol** — stock ticker
* Includes: *Income Statement*, *Balance Sheet*, *Cash Flow*

### Company Profile

`GET /api/yahoo/profile/{symbol}`

* **symbol** — stock ticker

### Earnings

`GET /api/yahoo/earnings/{symbol}`

* **symbol** — stock ticker
* Returns annual and quarterly results

### Historical Prices

`GET /api/yahoo/history/{symbol}`

* **symbol** — stock ticker
* **range** (optional): 1d, 5d, 1mo, 3mo, 6mo, 1y, 2y, 5y, 10y, ytd, max
* **interval** (optional): 1m, 2m, 5m, 15m, 1h, 1d, 1wk, 1mo

### Internal Utilities

`GET /api/yahoo/status`

* Returns information about the current session (cookies, crumb, etc.)

### Manual Session Refresh

`POST /api/yahoo/refresh`

* Forces an update of cookies and crumb

## Data Returned by the API

The API returns multiple structured datasets. Below is an overview.

### Real‑time Quotes

* Current price
* Absolute change
* Percentage change
* Open
* Day high / day low
* Volume
* Market Cap
* Average price
* Previous close

### Historical Prices (OHLCV)

* Open
* High
* Low
* Close
* Volume
* Timestamp
* Adjusted data

### Financial Statements

**Income Statement**

* Revenue
* Net income
* EPS
* Gross profit
* Operating income

**Balance Sheet**

* Total assets
* Total liabilities
* Shareholder equity
* Cash and equivalents

**Cash Flow**

* Operating cash flow
* Investing cash flow
* Financing cash flow
* Free cash flow

### Market Statistics

* P/E
* Forward P/E
* Beta
* Price to Book
* Dividend Yield
* Earnings Date
* EPS (TTM)
* PEG Ratio

### Company Profile

* Company name
* Sector
* Industry
* Address
* Number of employees
* Official website
* Description

## Acknowledgment

Thank you for using OpenFinanceData. The project’s priority is **maintaining a solid and reliable solution**, quickly adapting to Yahoo Finance changes and ensuring long‑term stability. Continuous maintenance is the top focus.
