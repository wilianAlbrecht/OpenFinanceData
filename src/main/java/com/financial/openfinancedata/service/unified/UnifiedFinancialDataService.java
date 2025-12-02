package com.financial.openfinancedata.service.unified;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UnifiedFinancialDataService {

    private final ObjectMapper mapper;

    public UnifiedFinancialDataService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectNode unify(String yahooResponse) {

        JsonNode root;
        try {
            root = mapper.readTree(yahooResponse);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler JSON no UnifiedFinancialDataService", e);
        }

        ObjectNode out = mapper.createObjectNode();

        JsonNode result = root.path("quoteSummary").path("result").get(0);
        if (result.isMissingNode()) {
            return out;
        }

        JsonNode fd = result.path("financialData");
        if (!fd.isObject()) {
            return out;
        }

        // ============================
        // 1 — Campos simples
        // ============================
        extractSimpleFields(fd, out);

        // ============================
        // 2 — Campos com raw
        // ============================
        extractAllRaw(fd, out);

        return out;
    }


    // ============================================================
    // UTILITÁRIOS (MESMOS PADRÕES USADOS EM TODAS AS SERVICES)
    // ============================================================

    /** Extrai campos simples: string, number, boolean, null, arrays simples */
    private void extractSimpleFields(JsonNode source, ObjectNode target) {

        for (Map.Entry<String, JsonNode> entry : source.properties()) {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            // Ignorar campos que possuem raw (tratados depois)
            if (value.has("raw")) {
                continue;
            }

            // Valor simples
            if (value.isValueNode()) {
                target.set(key, value);
                continue;
            }

            // Array simples
            if (value.isArray()) {
                boolean simpleArray = true;
                for (JsonNode v : value) {
                    if (v.isObject()) simpleArray = false;
                }
                if (simpleArray) {
                    target.set(key, value);
                }
                continue;
            }

            // Ignorar objetos vazios
            if (value.isObject() && value.size() == 0) {
                continue;
            }
        }
    }

    /** Extrai *todos* os campos com `raw` dentro da financialData */
    private void extractAllRaw(JsonNode source, ObjectNode target) {

        for (Map.Entry<String, JsonNode> entry : source.properties()) {

            String key = entry.getKey();
            JsonNode obj = entry.getValue();

            if (!obj.isObject()) continue;

            JsonNode raw = obj.get("raw");
            if (raw != null && !raw.isMissingNode()) {
                target.set(key, raw);
            }
        }
    }
}
