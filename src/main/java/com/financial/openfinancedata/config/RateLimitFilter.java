package com.financial.openfinancedata.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_MILLIS = Duration.ofMinutes(1).toMillis();

    private final Map<String, ClientWindow> clients = new ConcurrentHashMap<>();

    @Value("${security.rate-limit.requests-per-minute:120}")
    private int requestsPerMinute;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        long now = System.currentTimeMillis();
        ClientWindow window = clients.computeIfAbsent(request.getRemoteAddr(), ignored -> new ClientWindow(now));

        synchronized (window) {
            if (now - window.startedAt >= WINDOW_MILLIS) {
                window.startedAt = now;
                window.count = 0;
            }

            if (window.count >= requestsPerMinute) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"rate_limit_exceeded\"}");
                return;
            }

            window.count++;
        }

        cleanupOldWindows(now);
        filterChain.doFilter(request, response);
    }

    private void cleanupOldWindows(long now) {
        if (clients.size() < 10_000) {
            return;
        }

        clients.entrySet().removeIf(entry -> now - entry.getValue().startedAt > WINDOW_MILLIS * 2);
    }

    private static class ClientWindow {
        private long startedAt;
        private int count;

        private ClientWindow(long startedAt) {
            this.startedAt = startedAt;
        }
    }
}
