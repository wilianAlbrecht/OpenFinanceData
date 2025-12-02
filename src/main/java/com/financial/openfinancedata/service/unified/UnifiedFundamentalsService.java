package com.financial.openfinancedata.service.unified;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UnifiedFundamentalsService {

    private final ObjectMapper mapper;

    public UnifiedFundamentalsService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Recebe o JSON original do Yahoo e retorna um JSON unificado contendo:
     *  - todos os campos que possuem raw (retornando o raw)
     *  - todos os campos simples (string, number, boolean, null)
     *  - ignorando objetos vazios ou inúteis
     */
    public ObjectNode unify(String yahooResponse) {

        JsonNode root;
        try {
            root = mapper.readTree(yahooResponse);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter resposta do Yahoo para JSON (Fundamentals)", e);
        }

        ObjectNode result = mapper.createObjectNode();

        // Caminho padrão do Fundamentals
        JsonNode node =
                root.path("quoteSummary")
                    .path("result")
                    .path(0);

        if (node.isMissingNode()) {
            return result;
        }

        // Os três módulos principais
        extractModule(node.path("summaryDetail"), result);
        extractModule(node.path("defaultKeyStatistics"), result);
        extractModule(node.path("financialData"), result);

        return result;
    }

    /**
     * Extrai os campos úteis de um módulo (summaryDetail, defaultKeyStatistics, financialData)
     */
    private void extractModule(JsonNode module, ObjectNode output) {

        if (!module.isObject()) {
            return;
        }

        for (Map.Entry<String, JsonNode> entry : module.properties()) {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            // 1 — Se tiver raw, usar raw
            JsonNode rawNode = value.get("raw");
            if (rawNode != null && !rawNode.isMissingNode()) {
                output.set(key, rawNode);
                continue;
            }

            // 2 — Se for valor simples (string, number, boolean, null)
            if (value.isValueNode()) {
                output.set(key, value);
                continue;
            }

            // 3 — Ignorar objetos vazios ou não úteis
            if (value.isObject() && value.size() == 0) {
                continue;
            }

        }
    }
}
