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
public class YahooFinanceClient {

    private final YahooUrlBuilder urlBuilder;
    private final YahooSessionStore sessionStore;
    private final HttpClient httpClient;

    public String getQuoteRaw(String symbol) {

        // Obtém o estado atual (cookies + crumb) do Selenium
        ModelSession session = sessionStore.getCurrentState();

        if (!session.isValid()) {
            throw new RuntimeException("Sessão Yahoo inválida — Selenium ainda não inicializou ou crumb indisponível.");
        }

        // Constrói a URL com crumb
        String url = urlBuilder.quoteEndpoint(symbol, session.getCrumb());

        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .header("Accept", "application/json")
                    .GET();

            // APLICAR COOKIES DO SELENIUM
            applyCookiesToRequest(builder, session);

            HttpRequest request = builder.build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter dados do Yahoo Finance para " + symbol, e);
        }
    }

    /**
     * Constrói o header "Cookie" com todos os cookies coletados via Selenium.
     */
    private void applyCookiesToRequest(HttpRequest.Builder builder, ModelSession session) {
        if (session.getCookies() == null || session.getCookies().isEmpty()) {
            return;
        }

        // Formato: "A1=xxx; A3=yyy; B=zzz"
        String cookieHeader = session.getCookies().stream()
                .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                .reduce((a, b) -> a + "; " + b)
                .orElse("");

        if (!cookieHeader.isEmpty()) {
            builder.header("Cookie", cookieHeader);
        }
    }
}
