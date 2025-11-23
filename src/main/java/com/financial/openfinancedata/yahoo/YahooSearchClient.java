package com.financial.openfinancedata.yahoo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class YahooSearchClient {

    private final YahooUrlBuilder urlBuilder;
    private final HttpClient httpClient;

    public String search(String query) {

        String url = urlBuilder.searchEndpoint(query);

        try {
            HttpResponse<String> response = httpClient.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("User-Agent", "Mozilla/5.0")
                            .header("Accept", "application/json")
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar: " + query, e);
        }
    }
}
