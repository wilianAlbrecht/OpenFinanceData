package com.financial.openfinancedata.controller;

import com.financial.openfinancedata.yahoo.YahooSearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final YahooSearchClient client;

    @GetMapping("/{query}")
    public String search(@PathVariable String query) {
        return client.search(query);
    }
}
