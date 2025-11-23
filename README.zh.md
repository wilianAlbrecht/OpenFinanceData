🇧🇷 [Português](README.md)  | 🇺🇸 [English](README.en.md) | 🇨🇳 [中文](README.zh.md)

# OpenFinanceData

OpenFinanceData 是一个专为 Yahoo Finance 公共数据构建的 Java 客户端，旨在提供可靠、直接且结构化的数据访问方式。其目标非常明确：**提供一个技术稳健、易于集成的工具**，适用于对数据一致性和准确性有较高要求的环境。

## 项目缘起

目前市场上缺乏一种简单、稳定且易于获取的方式来访问完整的金融数据。许多免费 API 提供的数据过于有限或前后不一致，而更完整的服务通常价格高昂。OpenFinanceData 正是为了解决这一痛点而诞生：它将基础面数据、统计数据、财报信息、公司资料以及历史价格整合到一个统一的访问点中。任何能够发起 HTTP 请求的系统或语言都可以使用它。

## 你可以使用 OpenFinanceData 做什么

* 获取实时行情（实时行情）。
* 获取不同周期和间隔的历史价格数据。
* 访问完整财务数据（财报、资产负债、现金流等）。
* 获取市场统计信息。
* 获取公司详细资料。
* 轻松将这些数据集成到 API、分析系统、看板或交易工具中。

## 使用技术

* Java 21
* Spring Boot 3（Web、WebFlux、Validation、Security）
* Java HttpClient（模拟浏览器请求）
* CookieManager 与 CrumbManager（会话管理）
* Selenium（必要时用于初始 Cookie 捕获）
* Jackson
* Maven Wrapper
* Lombok
* JUnit 5 与 Mockito

## 内部架构

```
openfinancedata/
 ├── external/
 │    ├── yahoo/
 │    │     ├── client/        # 真实 HTTP 请求
 │    │     ├── models/        # 数据结构
 │    │     ├── crumb/         # Cookie + crumb 逻辑
 │    │     └── parser/        # 处理不稳定的 Yahoo 响应
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

## OpenFinanceData 如何与 Yahoo Finance 交互

Yahoo Finance 使用多种保护机制，例如 Cookie、特定 Header 以及著名的 **crumb** 参数，用于验证某些请求。

OpenFinanceData 通过以下流程自动处理这些细节：

1. 访问 Yahoo 域获取初始 Cookie。
2. 使用模拟浏览器的头部信息获取 crumb。
3. 验证 crumb 响应确保其有效性。
4. 定期刷新会话，避免失效。
5. 所有请求共享同一会话上下文。

这种方式确保客户端在 Yahoo 修改内部机制时仍能保持稳定。

## 运行项目

### 1. 克隆仓库

```
git clone https://github.com/wilianAlbrecht/OpenFinanceData
cd openfinancedata
```

### 2. 使用 Maven Wrapper 运行

```
./mvnw spring-boot:run
```

### 3. 访问 API

```
http://localhost:8080/api/yahoo/
```

## 可用端点（Endpoints）

以下列出了所有可访问的端点及其参数（**URL 与参数名保持英文原文，不做翻译**）。

### 实时行情（实时行情）

`GET /api/yahoo/quote/{symbol}`

* **symbol** — stock ticker

### 财务报表（财务报表）

`GET /api/yahoo/financials/{symbol}`

* **symbol** — stock ticker
* Includes: Income Statement, Balance Sheet, Cash Flow

### 公司资料（公司资料）

`GET /api/yahoo/profile/{symbol}`

* **symbol** — stock ticker

### 财报（财报）

`GET /api/yahoo/earnings/{symbol}`

* **symbol** — stock ticker
* Returns annual and quarterly earnings

### 历史价格（历史价格）

`GET /api/yahoo/history/{symbol}`

* **symbol** — stock ticker
* **range** (optional): 1d, 5d, 1mo, 3mo, 6mo, 1y, 2y, 5y, 10y, ytd, max
* **interval** (optional): 1m, 2m, 5m, 15m, 1h, 1d, 1wk, 1mo

### 会话状态（Status）

`GET /api/yahoo/status`

* Returns session information (cookies, crumb, etc.)

### 手动刷新会话（Refresh）

`POST /api/yahoo/refresh`

* Force update of cookies and crumb

## API 返回的数据类型

本节列出 API 返回的所有主要字段（保留英文原名，不做翻译）。

### Quotes (Real-time)

* currentPrice
* change
* changePercent
* open
* dayHigh
* dayLow
* volume
* marketCap
* averagePrice
* previousClose

### Historical Prices (OHLCV)

* open
* high
* low
* close
* volume
* timestamp
* adjustedData

### Financial Statements

**Income Statement**

* revenue
* netIncome
* eps
* grossProfit
* operatingIncome

**Balance Sheet**

* totalAssets
* totalLiabilities
* shareholderEquity
* cashAndEquivalents

**Cash Flow**

* operatingCashFlow
* investingCashFlow
* financingCashFlow
* freeCashFlow

### Market Statistics

* peRatio
* forwardPe
* beta
* priceToBook
* dividendYield
* earningsDate
* epsTtm
* pegRatio

### Company Profile

* companyName
* sector
* industry
* address
* employees
* website
* description

## 致谢

感谢使用 OpenFinanceData。项目的核心目标是确保其长期稳定与可靠性，即使 Yahoo Finance 的内部机制发生变化，也能快速适配并保持正常运行。持续维护将始终作为最高优先级。
