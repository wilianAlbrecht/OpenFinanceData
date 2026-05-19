package com.financial.openfinancedata.controller;

import com.financial.openfinancedata.service.unified.UnifiedQuoteService;
import com.financial.openfinancedata.yahoo.YahooQuoteClient;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/quote")
@RequiredArgsConstructor
public class QuoteController {

    private static final String SYMBOLS_PATTERN = "^[A-Za-z0-9.^=\\-]+(,[A-Za-z0-9.^=\\-]+){0,49}$";
    private static final String MODE_PATTERN = "^(original|modular|unified)$";

    private final YahooQuoteClient client;
    private final UnifiedQuoteService unifiedQuoteService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getQuotes(
            @RequestParam
            @Size(min = 1, max = 300)
            @Pattern(regexp = SYMBOLS_PATTERN)
            String symbols,
            @RequestParam(required = false)
            @Pattern(regexp = MODE_PATTERN)
            String mode) {

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
