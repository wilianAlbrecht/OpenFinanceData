package com.financial.openfinancedata.controller;

import com.financial.openfinancedata.yahoo.YahooQuoteSummaryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuoteSummaryController {

    private final YahooQuoteSummaryClient client;

    @GetMapping("/price/{symbol}")
    public String getPrice(@PathVariable String symbol) {
        return client.getModules(symbol, "price");
    }

    @GetMapping("/fundamentals/{symbol}")
    public String getFundamentals(@PathVariable String symbol) {
        return client.getModules(symbol,
                "summaryDetail,financialData,defaultKeyStatistics");
    }

    @GetMapping("/financials/{symbol}")
    public String getFinancials(@PathVariable String symbol) {
        return client.getModules(symbol,
                "balanceSheetHistory,incomeStatementHistory,cashflowStatementHistory");
    }

    @GetMapping("/earnings/{symbol}")
    public String getEarnings(@PathVariable String symbol) {
        return client.getModules(symbol, "earnings");
    }

    @GetMapping("/profile/{symbol}")
    public String getProfile(@PathVariable String symbol) {
        return client.getModules(symbol,
                "summaryProfile,assetProfile");
    }
}
