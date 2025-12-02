package com.financial.openfinancedata.service.unified;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UnifiedQuoteService {

    private final ObjectMapper mapper;

    public UnifiedQuoteService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ArrayNode unify(String yahooResponse) {

        JsonNode root;
        try {
            root = mapper.readTree(yahooResponse);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler JSON no UnifiedQuoteService", e);
        }

        ArrayNode outputList = mapper.createArrayNode();

        JsonNode list = root.path("quoteResponse").path("result");
        if (!list.isArray()) {
            return outputList; // retorna lista vazia
        }

        for (JsonNode quoteNode : list) {

            ObjectNode cleaned = mapper.createObjectNode();

            extractSimpleFields(quoteNode, cleaned);
            extractArrays(quoteNode, cleaned);
            extractNonEmptyObjects(quoteNode, cleaned);

            outputList.add(cleaned);
        }

        return outputList;
    }


    // ===========================
    // EXTRAÇÃO DE CAMPOS SIMPLES
    // ===========================
    private void extractSimpleFields(JsonNode source, ObjectNode target) {

        for (Map.Entry<String, JsonNode> entry : source.properties()) {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            if (value.isValueNode()) {
                target.set(key, value);
            }
        }
    }

    // ===========================
    // ARRAYS (corporateActions etc)
    // ===========================
    private void extractArrays(JsonNode source, ObjectNode target) {

        for (Map.Entry<String, JsonNode> entry : source.properties()) {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            if (value.isArray()) {
                target.set(key, value);
            }
        }
    }

    // ===========================
    // Objetos normais (não-vazios)
    // ===========================
    private void extractNonEmptyObjects(JsonNode source, ObjectNode target) {

        for (Map.Entry<String, JsonNode> entry : source.properties()) {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            if (value.isObject() && value.size() > 0 && !value.has("raw")) {
                // objetos como:
                // - corporateActions[x].meta
                // - earnings timestamps (não existe aqui mas mantemos padrão)
                target.set(key, value);
            }
        }
    }
}
