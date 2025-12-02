package com.financial.openfinancedata.controller;

import com.financial.openfinancedata.service.unified.UnifiedQuoteService;
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
    private final UnifiedQuoteService unifiedQuoteService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getQuotes(@RequestParam String symbols, @RequestParam(required = false) String mode) {

        String response = client.getQuotes(symbols);

        if (mode == null || mode.equals("original")) {

            return ResponseEntity.ok(response);

        } else if (mode.equals("modular")) {

        } else if (mode.equals("unified")) {

            return ResponseEntity.ok(unifiedQuoteService.unify(response).toString());

        }

        return ResponseEntity.badRequest()
                .body("Parâmetro 'mode' inválido. Valores aceitos: unified, modular, original.");

    }
}
