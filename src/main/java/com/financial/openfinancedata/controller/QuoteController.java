package com.financial.openfinancedata.controller;

import com.financial.openfinancedata.yahoo.YahooQuoteClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quote")
@RequiredArgsConstructor
public class QuoteController {

    private final YahooQuoteClient client;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getQuotes(@RequestParam String symbols) {
        return ResponseEntity.ok(client.getQuotes(symbols));
    }
}
