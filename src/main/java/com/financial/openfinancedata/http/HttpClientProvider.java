package com.financial.openfinancedata.http;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientProvider {

    @Bean
    public CookieManager cookieManager() {
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        return manager;
    }

    @Bean
    public HttpClient httpClient(CookieManager cookieManager) {
        return HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }
}
