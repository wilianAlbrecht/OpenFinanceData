package com.financial.openfinancedata.service.unified;

import java.util.Map;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UnifiedHistoryService {

    private final ObjectMapper mapper;

    public UnifiedHistoryService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectNode unify(String yahooJsonString) {

        JsonNode root;
        try {
            root = mapper.readTree(yahooJsonString);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter JSON do history", e);
        }

        JsonNode result = root
            .path("chart")
            .path("result")
            .path(0);

        ObjectNode out = mapper.createObjectNode();

        /* ============================================================
         * 1) META → FLATTEN DIRECTLY INTO ROOT (REMOVE META BLOCK)
         * ============================================================ */
        JsonNode meta = result.path("meta");

        if (meta.isObject()) {
            for (Map.Entry<String, JsonNode> entry : meta.properties()) {

                String key = entry.getKey();
                JsonNode value = entry.getValue();

                // Flatten currentTradingPeriod
                if (key.equals("currentTradingPeriod") && value.isObject()) {

                    value.properties().forEach(period -> {
                        String periodName = period.getKey(); // pre / regular / post
                        JsonNode info = period.getValue();

                        info.properties().forEach(field -> {
                            String flatKey = "currentTradingPeriod_" +
                                             periodName + "_" + 
                                             field.getKey();
                            out.set(flatKey, field.getValue());
                        });
                    });

                    continue;
                }

                // Meta normal
                out.set(key, value);
            }
        }

        /* ============================================================
         * 2) TIMESTAMP
         * ============================================================ */
        out.set("timestamp", result.path("timestamp"));

        /* ============================================================
         * 3) QUOTE (open, close, high, low, volume)
         * ============================================================ */
        JsonNode quote = result
            .path("indicators")
            .path("quote")
            .path(0);

        if (quote.isObject()) {
            for (Map.Entry<String, JsonNode> entry : quote.properties()) {
                out.set(entry.getKey(), entry.getValue());
            }
        }

        /* ============================================================
         * 4) ADJCLOSE
         * ============================================================ */
        JsonNode adjclose = result
            .path("indicators")
            .path("adjclose")
            .path(0)
            .path("adjclose");

        if (!adjclose.isMissingNode()) {
            out.set("adjclose", adjclose);
        }

        /* ============================================================
         * 5) DIVIDENDS → LIST FORMAT
         * ============================================================ */
        JsonNode dividendsNode = result.path("events").path("dividends");

        if (dividendsNode.isObject()) {

            ArrayNode list = mapper.createArrayNode();

            for (Map.Entry<String, JsonNode> entry : dividendsNode.properties()) {

                ObjectNode div = mapper.createObjectNode();
                div.put("timestamp", Long.parseLong(entry.getKey()));

                JsonNode data = entry.getValue();
                div.set("amount", data.path("amount"));
                div.set("date", data.path("date"));

                list.add(div);
            }

            out.set("dividends", list);
        }

        return out;
    }
}
