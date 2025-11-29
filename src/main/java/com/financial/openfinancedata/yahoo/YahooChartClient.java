package com.financial.openfinancedata.yahoo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Service;

import com.financial.openfinancedata.model.ModelSession;
import com.financial.openfinancedata.session.YahooSessionStore;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class YahooChartClient {

    private final YahooUrlBuilder urlBuilder;
    private final YahooSessionStore sessionStore;
    private final HttpClient httpClient;

    public String getHistory(String symbol, String range, String interval) {

        // symbol = urlBuilder.normalizeSymbol(symbol);
        ModelSession session = sessionStore.getCurrentState();

        if (!session.isValid()) {
            throw new RuntimeException("Sessão Yahoo inválida: crumb/cookies indisponíveis.");
        }

        String url = urlBuilder.chartEndpoint(symbol, range, interval, session.getCrumb());

        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Accept", "application/json")
                    .GET();

            applyCookies(builder, session);

            return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString()).body();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter histórico para " + symbol, e);
        }
    }

    private void applyCookies(HttpRequest.Builder builder, ModelSession session) {
        if (session.getCookies() == null || session.getCookies().isEmpty()) return;

        String cookieHeader = session.getCookies().stream()
                .map(c -> c.getName() + "=" + c.getValue())
                .reduce((a, b) -> a + "; " + b)
                .orElse("");

        builder.header("Cookie", cookieHeader);
    }
}
