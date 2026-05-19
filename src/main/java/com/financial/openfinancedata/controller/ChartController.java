package com.financial.openfinancedata.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financial.openfinancedata.service.unified.UnifiedHistoryService;
import com.financial.openfinancedata.yahoo.YahooChartClient;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/history")
@AllArgsConstructor
public class ChartController {

    private static final String SYMBOL_PATTERN = "^[A-Za-z0-9.^=\\-]{1,30}$";
    private static final String RANGE_PATTERN = "^(1d|5d|1mo|3mo|6mo|1y|2y|5y|10y|ytd|max)$";
    private static final String INTERVAL_PATTERN = "^(1m|2m|5m|15m|30m|60m|90m|1h|1d|5d|1wk|1mo|3mo)$";
    private static final String MODE_PATTERN = "^(original|modular|unified)$";

    private final YahooChartClient chartClient;
    private final UnifiedHistoryService unifiedHistoryService;

    @GetMapping(value = "/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getHistory(
            @PathVariable
            @Pattern(regexp = SYMBOL_PATTERN)
            String symbol,
            @RequestParam
            @Pattern(regexp = RANGE_PATTERN)
            String range,
            @RequestParam(required = false)
            @Pattern(regexp = MODE_PATTERN)
            String mode,
            @RequestParam
            @Pattern(regexp = INTERVAL_PATTERN)
            String interval) {

        String response = chartClient.getHistory(symbol, range, interval);

        if (mode == null || mode.equals("original")) {
            return ResponseEntity.ok(response);
        } else if (mode.equals("modular")) {
            
        } else if (mode.equals("unified")) {
            return ResponseEntity.ok(unifiedHistoryService.unify(response).toString());
        }

        return ResponseEntity.badRequest()
                .body("Parâmetro 'mode' inválido. Valores aceitos: unified, modular, original.");

    }
}
