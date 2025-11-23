package com.financial.openfinancedata.yahoo;

import org.springframework.stereotype.Component;

@Component
public class YahooUrlBuilder {

    private static final String BASE = "https://query1.finance.yahoo.com";

    public String quoteEndpoint(String symbol, String crumb) {
        return BASE + "/v10/finance/quoteSummary/" + symbol
                + "?modules=price&crumb=" + crumb;
    }

    public String chartEndpoint(String symbol, String crumb) {
        return BASE + "/v8/finance/chart/" + symbol
                + "?interval=1d&range=1mo&crumb=" + crumb;
    }
}
