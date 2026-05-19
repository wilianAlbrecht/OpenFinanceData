package com.financial.openfinancedata.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuoteSummaryController {

    private static final String SYMBOL_PATTERN = "^[A-Za-z0-9.^=\\-]{1,30}$";
    private static final String MODE_PATTERN = "^(original|modular|unified)$";

    private final YahooQuoteSummaryClient client;
    private final UnifiedFundamentalsService unifiedFundamentalsService;
    private final UnifiedPriceService unifiedPriceService;
    private final UnifiedFinancialsService unifiedFinancialsService;
    private final UnifiedEarningsService unifiedEarningsService;
    private final UnifiedProfileService unifiedProfileService;
    private final UnifiedFinancialDataService unifiedFinancialDataService;
    
    @GetMapping(value = "/price/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPrice(@PathVariable @Pattern(regexp = SYMBOL_PATTERN) String symbol, @RequestParam(required = false) @Pattern(regexp = MODE_PATTERN) String mode) {

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
    public ResponseEntity<String> getFundamentals(@PathVariable @Pattern(regexp = SYMBOL_PATTERN) String symbol,
            @RequestParam(required = false) @Pattern(regexp = MODE_PATTERN) String mode) {

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
    public ResponseEntity<String> getFinancials(@PathVariable @Pattern(regexp = SYMBOL_PATTERN) String symbol, @RequestParam(required = false) @Pattern(regexp = MODE_PATTERN) String mode) {


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
    public ResponseEntity<String> getEarnings(@PathVariable @Pattern(regexp = SYMBOL_PATTERN) String symbol, @RequestParam(required = false) @Pattern(regexp = MODE_PATTERN) String mode) {

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
    public ResponseEntity<String> getProfile(@PathVariable @Pattern(regexp = SYMBOL_PATTERN) String symbol, @RequestParam(required = false) @Pattern(regexp = MODE_PATTERN) String mode) {


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
    public ResponseEntity<String> getFinancialData(@PathVariable @Pattern(regexp = SYMBOL_PATTERN) String symbol, @RequestParam(required = false) @Pattern(regexp = MODE_PATTERN) String mode) {

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
