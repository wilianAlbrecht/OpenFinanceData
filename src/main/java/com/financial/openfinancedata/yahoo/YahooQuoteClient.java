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
public class YahooQuoteClient {

    private final YahooUrlBuilder urlBuilder;
    private final YahooSessionStore sessionStore;
    private final HttpClient httpClient;

    public String getQuotes(String symbols) {

        // Aplica normalização
        // String normalized = applyNormalization(symbols);

        // Obtém estado da sessão
        ModelSession session = sessionStore.getCurrentState();

        if (session == null || !session.isValid()) {
            throw new RuntimeException("Sessão Yahoo inválida — Selenium ainda não inicializou.");
        }

        // Usa crumb obrigatório
        String crumb = session.getCrumb();

        // Monta URL completa com crumb
        String url = urlBuilder.quoteV7Endpoint(symbols, crumb);

        try {

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Accept", "application/json");

            // Adiciona cookies
            if (session.getCookies() != null && !session.getCookies().isEmpty()) {
                String cookieHeader = session.getCookies().stream()
                        .map(c -> c.getName() + "=" + c.getValue())
                        .reduce((a, b) -> a + "; " + b)
                        .orElse("");
                builder.header("Cookie", cookieHeader);
            }

            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter quotes para: " + symbols, e);
        }
    }

    private String applyNormalization(String symbols) {
        String[] arr = symbols.split(",");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            String normalized = urlBuilder.normalizeSymbol(arr[i].trim());
            sb.append(normalized);
            if (i < arr.length - 1) sb.append(",");
        }

        return sb.toString();
    }
}
