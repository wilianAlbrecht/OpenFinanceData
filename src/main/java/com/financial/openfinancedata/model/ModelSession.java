package com.financial.openfinancedata.model;

import java.net.HttpCookie;
import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class ModelSession {

    private List<HttpCookie> cookies;
    private String crumb;
    private Instant createdAt;
    private boolean valid;
    private String lastError;

    public boolean isExpired() {
        if (createdAt == null) return true;
        return createdAt.isBefore(Instant.now().minusSeconds(300)); // expira em 5 min
    }
}
