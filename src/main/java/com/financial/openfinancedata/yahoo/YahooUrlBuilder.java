package com.financial.openfinancedata.yahoo;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class YahooUrlBuilder {

    private static final String BASE = "https://query1.finance.yahoo.com";

    /**
     * Endpoint genérico para o quoteSummary (v10)
     * Permite qualquer conjunto de módulos.
     */
    public String quoteSummaryEndpoint(String symbol, String modules, String crumb) {
        return UriComponentsBuilder.fromUriString(BASE)
                .pathSegment("v10", "finance", "quoteSummary", symbol)
                .queryParam("modules", modules)
                .queryParam("crumb", crumb)
                .build()
                .encode()
                .toUriString();
    }

    /**
     * Endpoint para histórico (chart API - v8)
     * Range e interval dinâmicos.
     */
    public String chartEndpoint(String symbol, String range, String interval, String crumb) {
        return UriComponentsBuilder.fromUriString(BASE)
                .pathSegment("v8", "finance", "chart", symbol)
                .queryParam("interval", interval)
                .queryParam("range", range)
                .queryParam("events", "div")
                .queryParam("crumb", crumb)
                .build()
                .encode()
                .toUriString();
    }

    /**
     * Endpoint para quotes rápidos (v7)
     * Pode enviar vários símbolos separados por vírgula.
     */
    public String quoteV7Endpoint(String symbols, String crumb) {
        return UriComponentsBuilder.fromUriString(BASE)
                .pathSegment("v7", "finance", "quote")
                .queryParam("symbols", symbols)
                .queryParam("crumb", crumb)
                .build()
                .encode()
                .toUriString();
    }

    /**
     * Endpoint para busca/autocomplete (v1)
     */
    public String searchEndpoint(String query) {
        return UriComponentsBuilder.fromUriString(BASE)
                .pathSegment("v1", "finance", "search")
                .queryParam("q", query)
                .build()
                .encode()
                .toUriString();
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
