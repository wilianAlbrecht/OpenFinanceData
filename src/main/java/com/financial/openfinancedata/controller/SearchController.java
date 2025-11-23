package com.financial.openfinancedata.controller;

import com.financial.openfinancedata.yahoo.YahooSearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final YahooSearchClient client;

    @GetMapping(value = "/{query}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@PathVariable String query) {
        return ResponseEntity.ok(client.search(query));
    }
}
