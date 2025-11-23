package com.financial.openfinancedata.controller;

import com.financial.openfinancedata.yahoo.YahooChartClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class ChartController {

    private final YahooChartClient chartClient;

    @GetMapping(value = "/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getHistory(
            @PathVariable String symbol,
            @RequestParam String range,
            @RequestParam String interval) {

        return ResponseEntity.ok(chartClient.getHistory(symbol, range, interval));
    }
}
