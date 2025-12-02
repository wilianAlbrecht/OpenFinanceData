package com.financial.openfinancedata.service.unified;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UnifiedSearchService {

    private final ObjectMapper mapper;

    public UnifiedSearchService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectNode unify(String yahooResponse) {

        JsonNode root;
        try {
            root = mapper.readTree(yahooResponse);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler JSON no UnifiedSearchService", e);
        }

        ObjectNode output = mapper.createObjectNode();

        // Campos principais no topo (count, explains etc)
        extractSimpleFields(root, output);

        // Arrays não vazias (quotes, news, nav etc)
        extractNonEmptyArrays(root, output);

        // Objetos não vazios (caso apareça futuramente)
        extractNonEmptyObjects(root, output);

        return output;
    }

    // ==========================================================
    // CAMPOS SIMPLES: strings, numbers, booleans
    // ==========================================================
    private void extractSimpleFields(JsonNode source, ObjectNode target) {

        for (Map.Entry<String, JsonNode> entry : source.properties()) {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            if (value.isValueNode()) {
                target.set(key, value);
            }
        }
    }

    // ==========================================================
    // ARRAYS (quotes, news, nav, etc)
    // ==========================================================
    private void extractNonEmptyArrays(JsonNode source, ObjectNode target) {

        for (Map.Entry<String, JsonNode> entry : source.properties()) {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            if (value.isArray() && value.size() > 0) {
                target.set(key, value);
            }
        }
    }

    // ==========================================================
    // Objetos não-vazios (caso existam)
    // ==========================================================
    private void extractNonEmptyObjects(JsonNode source, ObjectNode target) {

        for (Map.Entry<String, JsonNode> entry : source.properties()) {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            if (value.isObject() && value.size() > 0) {
                target.set(key, value);
            }
        }
    }
}
