package com.financial.openfinancedata.service.unified;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UnifiedEarningsService {

    private final ObjectMapper mapper;

    public UnifiedEarningsService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectNode unify(String yahooResponse) {

        JsonNode root;
        try {
            root = mapper.readTree(yahooResponse);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler JSON no UnifiedEarningsService", e);
        }

        ObjectNode out = mapper.createObjectNode();

        JsonNode earningsNode = root.path("quoteSummary").path("result").get(0).path("earnings");

        if (earningsNode.isMissingNode() || earningsNode.isNull()) {
            return out;
        }

        // ============================================================
        // 1) Extrair earningsChart.quarterly → earningsQuarterly[]
        // ============================================================
        ArrayNode earningsQuarterly = mapper.createArrayNode();
        JsonNode quarterly = earningsNode.path("earningsChart").path("quarterly");

        if (quarterly.isArray()) {
            for (JsonNode q : quarterly) {

                ObjectNode item = mapper.createObjectNode();

                // Campos simples
                extractSimpleFields(q, item);

                // Campos RAW
                extractRaw(q, item, "actual");
                extractRaw(q, item, "estimate");

                earningsQuarterly.add(item);
            }
        }

        out.set("earningsQuarterly", earningsQuarterly);

        // ============================================================
        // 2) financialsChart.yearly → financialsYearly[]
        // ============================================================
        ArrayNode yearly = mapper.createArrayNode();
        JsonNode yearlyNode = earningsNode.path("financialsChart").path("yearly");

        if (yearlyNode.isArray()) {
            for (JsonNode y : yearlyNode) {
                ObjectNode item = mapper.createObjectNode();

                extractSimpleFields(y, item);
                extractRaw(y, item, "revenue");
                extractRaw(y, item, "earnings");

                yearly.add(item);
            }
        }

        out.set("financialsYearly", yearly);

        // ============================================================
        // 3) financialsChart.quarterly → financialsQuarterly[]
        // ============================================================
        ArrayNode fq = mapper.createArrayNode();
        JsonNode fqNode = earningsNode.path("financialsChart").path("quarterly");

        if (fqNode.isArray()) {
            for (JsonNode q : fqNode) {
                ObjectNode item = mapper.createObjectNode();

                extractSimpleFields(q, item);
                extractRaw(q, item, "revenue");
                extractRaw(q, item, "earnings");

                fq.add(item);
            }
        }

        out.set("financialsQuarterly", fq);

        // ============================================================
        // 4) Campos simples em earningsChart (pedidos por você)
        // currentQuarterEstimateDate, currentCalendarQuarter etc.
        // ============================================================
        extractSimpleFields(earningsNode.path("earningsChart"), out);

        // ============================================================
        // 5) Campos simples do nó base earnings (financialCurrency, defaultMethodology)
        // ============================================================
        extractSimpleFields(earningsNode, out);

        return out;
    }

    // ============================================================
    // MÉTODOS UTILITÁRIOS (PADRÃO QUE VOCÊ PEDIU)
    // ============================================================

    /**
     * Extrai campos simples automaticamente (string, number, boolean, null, arrays simples)
     */
    private void extractSimpleFields(JsonNode source, ObjectNode target) {

        if (!source.isObject()) return;

        for (Map.Entry<String, JsonNode> entry : source.properties()) {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            // RAW é tratado na função extractRaw()
            if (value.has("raw")) {
                continue;
            }

            // value node simples
            if (value.isValueNode()) {
                target.set(key, value);
                continue;
            }

            // array simples
            if (value.isArray()) {
                boolean simpleArray = true;
                for (JsonNode v : value) {
                    if (v.isObject()) {
                        simpleArray = false;
                        break;
                    }
                }

                if (simpleArray) {
                    target.set(key, value);
                }
                continue;
            }

            // objeto vazio → ignorar
            if (value.isObject() && value.size() == 0) {
                continue;
            }
        }
    }


    /**
     * Extrai campos que possuem raw.
     */
    private void extractRaw(JsonNode source, ObjectNode target, String fieldName) {
        JsonNode f = source.path(fieldName);
        JsonNode raw = f.get("raw");

        if (raw != null) {
            target.set(fieldName, raw);
        }
    }
}
