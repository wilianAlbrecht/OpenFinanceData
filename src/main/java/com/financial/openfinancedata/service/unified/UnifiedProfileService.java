package com.financial.openfinancedata.service.unified;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UnifiedProfileService {

    private final ObjectMapper mapper;

    public UnifiedProfileService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectNode unify(String yahooResponse) {

        JsonNode root;
        try {
            root = mapper.readTree(yahooResponse);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler JSON no UnifiedProfileService", e);
        }

        ObjectNode out = mapper.createObjectNode();

        JsonNode result = root.path("quoteSummary").path("result").get(0);

        if (result.isMissingNode()) {
            return out;
        }

        JsonNode asset = result.path("assetProfile");
        JsonNode summary = result.path("summaryProfile");

        // ============================================================
        // 1 — Campos simples (assetProfile)
        // ============================================================
        if (asset.isObject()) {
            extractSimpleFields(asset, out);
        }

        // ============================================================
        // 2 — Campos simples (summaryProfile)
        // sobrescrevem os do asset conforme padrão do projeto
        // ============================================================
        if (summary.isObject()) {
            extractSimpleFields(summary, out);
        }

        // ============================================================
        // 3 — Arrays: companyOfficers (com raw → somente raw)
        // ============================================================
        ArrayNode officersArray = mapper.createArrayNode();

        JsonNode officers = asset.path("companyOfficers");
        if (officers.isArray()) {
            for (JsonNode officer : officers) {

                ObjectNode item = mapper.createObjectNode();

                // Campos simples (name, title, age, yearBorn…)
                extractSimpleFields(officer, item);

                // Campos com RAW (exercisedValue, unexercisedValue)
                extractRaw(officer, item, "exercisedValue");
                extractRaw(officer, item, "unexercisedValue");

                officersArray.add(item);
            }
        }

        out.set("companyOfficers", officersArray);

        // ============================================================
        // 4 — Arrays: executiveTeam (normal, sem RAW)
        // ============================================================
        ArrayNode execTeamArray = mapper.createArrayNode();

        JsonNode execTeamAsset = asset.path("executiveTeam");
        if (execTeamAsset.isArray()) {
            for (JsonNode member : execTeamAsset) {

                ObjectNode item = mapper.createObjectNode();

                extractSimpleFields(member, item);

                execTeamArray.add(item);
            }
        }

        JsonNode execTeamSummary = summary.path("executiveTeam");
        if (execTeamSummary.isArray()) {
            for (JsonNode member : execTeamSummary) {

                ObjectNode item = mapper.createObjectNode();

                extractSimpleFields(member, item);

                execTeamArray.add(item);
            }
        }

        out.set("executiveTeam", execTeamArray);

        return out;
    }


    // ============================================================
    // MÉTODOS UTILITÁRIOS (SEMPRE OS MESMOS EM TODOS UNIFIERS)
    // ============================================================

    /**
     * Extrai automaticamente campos simples:
     * - string
     * - number
     * - boolean
     * - null
     * - arrays simples (somente valores)
     */
    private void extractSimpleFields(JsonNode source, ObjectNode target) {

        if (!source.isObject()) return;

        for (Map.Entry<String, JsonNode> entry : source.properties()) {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            // Ignorar campos com RAW (tratados separadamente)
            if (value.has("raw")) {
                continue;
            }

            // Valor simples
            if (value.isValueNode()) {
                target.set(key, value);
                continue;
            }

            // Array simples (strings/numbers)
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

            // Ignorar objetos vazios
            if (value.isObject() && value.size() == 0) {
                continue;
            }
        }
    }

    /**
     * Extrai campos com "raw"
     */
    private void extractRaw(JsonNode source, ObjectNode target, String fieldName) {

        JsonNode f = source.path(fieldName);
        JsonNode raw = f.get("raw");

        if (raw != null) {
            target.set(fieldName, raw);
        }
    }
}
