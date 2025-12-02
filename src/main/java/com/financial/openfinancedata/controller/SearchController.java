package com.financial.openfinancedata.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financial.openfinancedata.service.unified.UnifiedSearchService;
import com.financial.openfinancedata.yahoo.YahooSearchClient;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final YahooSearchClient client;
    private final UnifiedSearchService unifiedSearchService;

    @GetMapping(value = "/{query}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@PathVariable String query, @RequestParam(required = false) String mode) {

        String response = client.search(query);

        if (mode == null || mode.equals("original")) {

            return ResponseEntity.ok(response);

        } else if (mode.equals("modular")) {

        } else if (mode.equals("unified")) {

            return ResponseEntity.ok(unifiedSearchService.unify(response).toString());

        }

        return ResponseEntity.badRequest()
                .body("Parâmetro 'mode' inválido. Valores aceitos: unified, modular, original.");
    }
}
