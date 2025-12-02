package com.financial.openfinancedata.service.unified;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UnifiedPriceService {

    private final ObjectMapper mapper;

    public UnifiedPriceService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Unifica o bloco "price" do Yahoo Finance em um JSON flat.
     */
    public ObjectNode unify(String yahooResponse) {

        JsonNode root;
        try {
            root = mapper.readTree(yahooResponse);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter JSON em unifyPrice", e);
        }

        ObjectNode result = mapper.createObjectNode();

        JsonNode price =
                root.path("quoteSummary")
                    .path("result")
                    .path(0)
                    .path("price");

        if (!price.isObject()) {
            return result;
        }

        extractModule(price, result);

        return result;
    }

    /**
     * Extrai os campos úteis do módulo "price".
     * Regras:
     *  - Se tem raw → usar raw
     *  - Se é valor simples → usar valor
     *  - Se é {} → ignorar
     *  - Se é objeto sem raw → ignorar
     */
    private void extractModule(JsonNode module, ObjectNode output) {

        for (Map.Entry<String, JsonNode> entry : module.properties()) {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            // 1 — raw
            JsonNode rawNode = value.get("raw");
            if (rawNode != null && !rawNode.isMissingNode()) {
                output.set(key, rawNode);
                continue;
            }

            // 2 — valor simples
            if (value.isValueNode()) {
                output.set(key, value);
                continue;
            }

            // 3 — objeto vazio {}
            if (value.isObject() && value.size() == 0) {
                continue;
            }

            // 4 — objetos ignorados (não possuem raw)
            // (por exemplo, volumeAllCurrencies, circulatingSupply, etc.)
        }
    }
}

