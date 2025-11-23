package com.financial.openfinancedata.controller;

import com.financial.openfinancedata.yahoo.YahooQuoteSummaryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuoteSummaryController {

    private final YahooQuoteSummaryClient client;

    @GetMapping(value = "/price/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPrice(@PathVariable String symbol) {
        return ResponseEntity.ok(client.getModules(symbol, "price"));
    }

    @GetMapping(value = "/fundamentals/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFundamentals(@PathVariable String symbol) {
        return ResponseEntity.ok(client.getModules(symbol,
                "summaryDetail,financialData,defaultKeyStatistics"));
    }

    @GetMapping(value = "/financials/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFinancials(@PathVariable String symbol) {
        return ResponseEntity.ok(client.getModules(symbol,
                "balanceSheetHistory,incomeStatementHistory,cashflowStatementHistory"));
    }

    @GetMapping(value = "/earnings/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getEarnings(@PathVariable String symbol) {
        return ResponseEntity.ok(client.getModules(symbol, "earnings"));
    }

    @GetMapping(value = "/profile/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getProfile(@PathVariable String symbol) {
        return ResponseEntity.ok(client.getModules(symbol,
                "summaryProfile,assetProfile"));
    }
}
