package com.financial.openfinancedata.controller;

import com.financial.openfinancedata.yahoo.YahooChartClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class ChartController {

    private final YahooChartClient chartClient;

    @GetMapping("/{symbol}")
    public String getHistory(
            @PathVariable String symbol,
            @RequestParam String range,
            @RequestParam String interval) {

        return chartClient.getHistory(symbol, range, interval);
    }
}
