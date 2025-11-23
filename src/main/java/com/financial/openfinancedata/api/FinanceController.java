package com.financial.openfinancedata.api;

import com.financial.openfinancedata.yahoo.YahooFinanceClient;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FinanceController {

    private final YahooFinanceClient yahooFinanceClient;

    @GetMapping("/status")
    public String status() {
        return "OpenFinanceData API is running";
    }

    @GetMapping("/yahoo/{symbol}")
    public String getYahooData(@PathVariable String symbol) {
        return yahooFinanceClient.getQuoteRaw(symbol);
    }
}
