package com.financial.openfinancedata.yahoo;

import org.springframework.stereotype.Component;

@Component
public class YahooUrlBuilder {

    private static final String BASE = "https://query1.finance.yahoo.com";

    /**
     * Endpoint genérico para o quoteSummary (v10)
     * Permite qualquer conjunto de módulos.
     */
    public String quoteSummaryEndpoint(String symbol, String modules, String crumb) {
        return BASE + "/v10/finance/quoteSummary/" + symbol
                + "?modules=" + modules
                + "&crumb=" + crumb;
    }

    /**
     * Endpoint para histórico (chart API - v8)
     * Range e interval dinâmicos.
     */
    public String chartEndpoint(String symbol, String range, String interval, String crumb) {
        return BASE + "/v8/finance/chart/" + symbol
                + "?interval=" + interval
                + "&range=" + range
                + "&crumb=" + crumb;
    }

    /**
     * Endpoint para quotes rápidos (v7)
     * Pode enviar vários símbolos separados por vírgula.
     */
    public String quoteV7Endpoint(String symbols, String crumb) {
        return BASE + "/v7/finance/quote?symbols=" + symbols + "&crumb=" + crumb;
    }

    /**
     * Endpoint para busca/autocomplete (v1)
     */
    public String searchEndpoint(String query) {
        return BASE + "/v1/finance/search?q=" + query;
    }

    /**
     * Normalização opcional de símbolo brasileiro.
     * PETR4 → PETR4.SA
     */
    public String normalizeSymbol(String symbol) {
        if (symbol == null || symbol.isBlank())
            return symbol;

        // Se já tem ., não adiciona .SA
        if (symbol.contains("."))
            return symbol;

        // Se termina com número (ex: VALE3, PETR4, ITUB4)
        char last = symbol.charAt(symbol.length() - 1);
        if (Character.isDigit(last)) {
            return symbol + ".SA";
        }

        return symbol;
    }
}
