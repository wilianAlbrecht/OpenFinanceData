package com.financial.openfinancedata.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financial.openfinancedata.service.unified.UnifiedEarningsService;
import com.financial.openfinancedata.service.unified.UnifiedFinancialDataService;
import com.financial.openfinancedata.service.unified.UnifiedFinancialsService;
import com.financial.openfinancedata.service.unified.UnifiedFundamentalsService;
import com.financial.openfinancedata.service.unified.UnifiedPriceService;
import com.financial.openfinancedata.service.unified.UnifiedProfileService;
import com.financial.openfinancedata.yahoo.YahooQuoteSummaryClient;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuoteSummaryController {

    private final YahooQuoteSummaryClient client;
    private final UnifiedFundamentalsService unifiedFundamentalsService;
    private final UnifiedPriceService unifiedPriceService;
    private final UnifiedFinancialsService unifiedFinancialsService;
    private final UnifiedEarningsService unifiedEarningsService;
    private final UnifiedProfileService unifiedProfileService;
    private final UnifiedFinancialDataService unifiedFinancialDataService;
    
    @GetMapping(value = "/price/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPrice(@PathVariable String symbol, @RequestParam(required = false) String mode) {

        String response = client.getModules(symbol, "price");

        if (mode == null || mode.equals("original")) {

            return ResponseEntity.ok(response);

        } else if (mode.equals("modular")) {

        } else if (mode.equals("unified")) {

            return ResponseEntity.ok(unifiedPriceService.unify(response).toString());

        }

        return ResponseEntity.badRequest()
                .body("Parâmetro 'mode' inválido. Valores aceitos: unified, modular, original.");

    }

    @GetMapping(value = "/fundamentals/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFundamentals(@PathVariable String symbol,
            @RequestParam(required = false) String mode) {

        String response = client.getModules(symbol, "summaryDetail,financialData,defaultKeyStatistics");

        if (mode == null || mode.equals("original")) {

            return ResponseEntity.ok(response);

        } else if (mode.equals("modular")) {

        } else if (mode.equals("unified")) {

            return ResponseEntity.ok(unifiedFundamentalsService.unify(response).toString());

        }

        return ResponseEntity.badRequest()
                .body("Parâmetro 'mode' inválido. Valores aceitos: unified, modular, original.");

    }

    @GetMapping(value = "/financials/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFinancials(@PathVariable String symbol, @RequestParam(required = false) String mode) {


        String response = client.getModules(symbol,
                "balanceSheetHistory,incomeStatementHistory,cashflowStatementHistory");

        if (mode == null || mode.equals("original")) {

            return ResponseEntity.ok(response);

        } else if (mode.equals("modular")) {

        } else if (mode.equals("unified")) {

            return ResponseEntity.ok(unifiedFinancialsService.unify(response).toString());

        }

        return ResponseEntity.badRequest()
                .body("Parâmetro 'mode' inválido. Valores aceitos: unified, modular, original.");

    }

    @GetMapping(value = "/earnings/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getEarnings(@PathVariable String symbol, @RequestParam(required = false) String mode) {

        String response = client.getModules(symbol, "earnings");

        if (mode == null || mode.equals("original")) {

            return ResponseEntity.ok(response);

        } else if (mode.equals("modular")) {

        } else if (mode.equals("unified")) {

            return ResponseEntity.ok(unifiedEarningsService.unify(response).toString());

        }

        return ResponseEntity.badRequest()
                .body("Parâmetro 'mode' inválido. Valores aceitos: unified, modular, original.");
    }

    @GetMapping(value = "/profile/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getProfile(@PathVariable String symbol, @RequestParam(required = false) String mode) {


        String response = client.getModules(symbol,"summaryProfile,assetProfile");
        if (mode == null || mode.equals("original")) {

            return ResponseEntity.ok(response);

        } else if (mode.equals("modular")) {

        } else if (mode.equals("unified")) {

            return ResponseEntity.ok(unifiedProfileService.unify(response).toString());

        }

        return ResponseEntity.badRequest()
                .body("Parâmetro 'mode' inválido. Valores aceitos: unified, modular, original.");
    }

    @GetMapping(value = "/financialData/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFinancialData(@PathVariable String symbol, @RequestParam(required = false) String mode) {

        String response = client.getModules(symbol, "financialData");

        if (mode == null || mode.equals("original")) {

            return ResponseEntity.ok(response);

        } else if (mode.equals("modular")) {

        } else if (mode.equals("unified")) {

            return ResponseEntity.ok(unifiedFinancialDataService.unify(response).toString());

        }

        return ResponseEntity.badRequest()
                .body("Parâmetro 'mode' inválido. Valores aceitos: unified, modular, original.");

    }
}
