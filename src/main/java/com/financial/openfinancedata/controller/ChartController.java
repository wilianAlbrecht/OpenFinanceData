package com.financial.openfinancedata.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financial.openfinancedata.service.unified.UnifiedHistoryService;
import com.financial.openfinancedata.yahoo.YahooChartClient;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/history")
@AllArgsConstructor
public class ChartController {

    private final YahooChartClient chartClient;
    private final UnifiedHistoryService unifiedHistoryService;

    @GetMapping(value = "/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getHistory(
            @PathVariable String symbol,
            @RequestParam String range,
            @RequestParam(required = false) String mode,
            @RequestParam String interval) {

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
