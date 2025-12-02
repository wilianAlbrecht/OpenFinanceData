package com.financial.openfinancedata.service.unified;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UnifiedFinancialsService {

    private final ObjectMapper mapper;

    public UnifiedFinancialsService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Unifica financials em três arrays:
     * - cashflow
     * - balanceSheet
     * - incomeStatement
     *
     * Padrão:
     *  - Se tiver "raw", retorna o raw
     *  - Se for valor simples, retorna o valor
     *  - Se for objeto vazio {}, ignora
     *  - Se for objeto sem raw, ignora
     */
    public ObjectNode unify(String yahooResponse) {

        JsonNode root;
        try {
            root = mapper.readTree(yahooResponse);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter JSON para UnifiedFinancials", e);
        }

        JsonNode resultNode =
                root.path("quoteSummary")
                    .path("result")
                    .path(0);

        ObjectNode unified = mapper.createObjectNode();

        // ======================
        // CASHFLOW
        // ======================
        JsonNode cashflowArr = resultNode
                .path("cashflowStatementHistory")
                .path("cashflowStatements");

        ArrayNode cashflowUnified = mapper.createArrayNode();
        extractFinancialArray(cashflowArr, cashflowUnified);
        unified.set("cashflow", cashflowUnified);

        // ======================
        // BALANCE SHEET
        // ======================
        JsonNode balanceArr = resultNode
                .path("balanceSheetHistory")
                .path("balanceSheetStatements");

        ArrayNode balanceUnified = mapper.createArrayNode();
        extractFinancialArray(balanceArr, balanceUnified);
        unified.set("balanceSheet", balanceUnified);

        // ======================
        // INCOME STATEMENT
        // ======================
        JsonNode incomeArr = resultNode
                .path("incomeStatementHistory")
                .path("incomeStatementHistory");

        ArrayNode incomeUnified = mapper.createArrayNode();
        extractFinancialArray(incomeArr, incomeUnified);
        unified.set("incomeStatement", incomeUnified);

        return unified;
    }

    /**
     * Extrai cada item da lista financial (um item por ano).
     * Cada item é transformado em JSON flat.
     */
    private void extractFinancialArray(JsonNode array, ArrayNode outputList) {

        if (!array.isArray()) {
            return;
        }

        for (JsonNode yearNode : array) {

            ObjectNode flatYear = mapper.createObjectNode();

            for (Map.Entry<String, JsonNode> entry : yearNode.properties()) {

                String key = entry.getKey();
                JsonNode value = entry.getValue();

                // 1 — Campo com "raw"
                JsonNode raw = value.get("raw");
                if (raw != null && !raw.isMissingNode()) {
                    flatYear.set(key, raw);
                    continue;
                }

                // 2 — Campo simples (string, number, boolean, null)
                if (value.isValueNode()) {
                    flatYear.set(key, value);
                    continue;
                }

                // 3 — Objeto vazio {}
                if (value.isObject() && value.size() == 0) {
                    continue;
                }

                // 4 — Ignorar objetos que não possuem raw
            }

            outputList.add(flatYear);
        }
    }
}
