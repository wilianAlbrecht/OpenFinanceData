package com.financial.openfinancedata.controller;

import com.financial.openfinancedata.yahoo.YahooQuoteClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quote")
@RequiredArgsConstructor
public class QuoteController {

    private final YahooQuoteClient client;

    @GetMapping
    public String getQuotes(@RequestParam String symbols) {
        return client.getQuotes(symbols);
    }
}
