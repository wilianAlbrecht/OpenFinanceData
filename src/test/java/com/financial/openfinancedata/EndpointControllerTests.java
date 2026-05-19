package com.financial.openfinancedata;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.financial.openfinancedata.config.ApiExceptionHandler;
import com.financial.openfinancedata.config.SecurityConfig;
import com.financial.openfinancedata.controller.ChartController;
import com.financial.openfinancedata.controller.QuoteController;
import com.financial.openfinancedata.controller.QuoteSummaryController;
import com.financial.openfinancedata.controller.SearchController;
import com.financial.openfinancedata.service.unified.UnifiedEarningsService;
import com.financial.openfinancedata.service.unified.UnifiedFinancialDataService;
import com.financial.openfinancedata.service.unified.UnifiedFinancialsService;
import com.financial.openfinancedata.service.unified.UnifiedFundamentalsService;
import com.financial.openfinancedata.service.unified.UnifiedHistoryService;
import com.financial.openfinancedata.service.unified.UnifiedPriceService;
import com.financial.openfinancedata.service.unified.UnifiedProfileService;
import com.financial.openfinancedata.service.unified.UnifiedQuoteService;
import com.financial.openfinancedata.service.unified.UnifiedSearchService;
import com.financial.openfinancedata.yahoo.YahooChartClient;
import com.financial.openfinancedata.yahoo.YahooQuoteClient;
import com.financial.openfinancedata.yahoo.YahooQuoteSummaryClient;
import com.financial.openfinancedata.yahoo.YahooSearchClient;

@WebMvcTest(controllers = {
        QuoteSummaryController.class,
        QuoteController.class,
        ChartController.class,
        SearchController.class
})
@Import({ApiExceptionHandler.class, SecurityConfig.class})
class EndpointControllerTests {

    private static final String ORIGINAL_JSON = "{\"source\":\"yahoo\"}";

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private YahooQuoteSummaryClient quoteSummaryClient;

    @MockitoBean
    private YahooQuoteClient quoteClient;

    @MockitoBean
    private YahooChartClient chartClient;

    @MockitoBean
    private YahooSearchClient searchClient;

    @MockitoBean
    private UnifiedFundamentalsService unifiedFundamentalsService;

    @MockitoBean
    private UnifiedPriceService unifiedPriceService;

    @MockitoBean
    private UnifiedFinancialsService unifiedFinancialsService;

    @MockitoBean
    private UnifiedEarningsService unifiedEarningsService;

    @MockitoBean
    private UnifiedProfileService unifiedProfileService;

    @MockitoBean
    private UnifiedFinancialDataService unifiedFinancialDataService;

    @MockitoBean
    private UnifiedQuoteService unifiedQuoteService;

    @MockitoBean
    private UnifiedHistoryService unifiedHistoryService;

    @MockitoBean
    private UnifiedSearchService unifiedSearchService;

    @ParameterizedTest
    @MethodSource("quoteSummaryOriginalEndpoints")
    void quoteSummaryEndpointsReturnOriginalYahooResponse(String path, String expectedModules) throws Exception {
        when(quoteSummaryClient.getModules("AAPL", expectedModules)).thenReturn(ORIGINAL_JSON);

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().json(ORIGINAL_JSON));

        verify(quoteSummaryClient).getModules("AAPL", expectedModules);
    }

    @Test
    void quoteEndpointReturnsOriginalYahooResponse() throws Exception {
        when(quoteClient.getQuotes("AAPL,MSFT")).thenReturn(ORIGINAL_JSON);

        mockMvc.perform(get("/api/quote").param("symbols", "AAPL,MSFT"))
                .andExpect(status().isOk())
                .andExpect(content().json(ORIGINAL_JSON));

        verify(quoteClient).getQuotes("AAPL,MSFT");
    }

    @Test
    void historyEndpointReturnsOriginalYahooResponse() throws Exception {
        when(chartClient.getHistory("AAPL", "1mo", "1d")).thenReturn(ORIGINAL_JSON);

        mockMvc.perform(get("/api/history/AAPL")
                        .param("range", "1mo")
                        .param("interval", "1d"))
                .andExpect(status().isOk())
                .andExpect(content().json(ORIGINAL_JSON));

        verify(chartClient).getHistory("AAPL", "1mo", "1d");
    }

    @Test
    void searchEndpointReturnsOriginalYahooResponse() throws Exception {
        when(searchClient.search("apple")).thenReturn(ORIGINAL_JSON);

        mockMvc.perform(get("/api/search/apple"))
                .andExpect(status().isOk())
                .andExpect(content().json(ORIGINAL_JSON));

        verify(searchClient).search("apple");
    }

    @ParameterizedTest
    @MethodSource("quoteSummaryUnifiedEndpoints")
    void quoteSummaryEndpointsReturnUnifiedResponse(UnifiedEndpoint endpoint) throws Exception {
        ObjectNode unified = unifiedJson(endpoint.name());
        when(quoteSummaryClient.getModules("AAPL", endpoint.modules())).thenReturn(ORIGINAL_JSON);
        endpoint.stubUnifiedResponse(this, unified);

        mockMvc.perform(get(endpoint.path()).param("mode", "unified"))
                .andExpect(status().isOk())
                .andExpect(content().json(unified.toString()));

        endpoint.verifyUnifiedCall(this);
    }

    @Test
    void quoteEndpointReturnsUnifiedResponse() throws Exception {
        ArrayNode unified = mapper.createArrayNode().add(unifiedJson("quote"));
        when(quoteClient.getQuotes("AAPL")).thenReturn(ORIGINAL_JSON);
        when(unifiedQuoteService.unify(ORIGINAL_JSON)).thenReturn(unified);

        mockMvc.perform(get("/api/quote").param("symbols", "AAPL").param("mode", "unified"))
                .andExpect(status().isOk())
                .andExpect(content().json(unified.toString()));

        verify(unifiedQuoteService).unify(ORIGINAL_JSON);
    }

    @Test
    void historyEndpointReturnsUnifiedResponse() throws Exception {
        ObjectNode unified = unifiedJson("history");
        when(chartClient.getHistory("AAPL", "1mo", "1d")).thenReturn(ORIGINAL_JSON);
        when(unifiedHistoryService.unify(ORIGINAL_JSON)).thenReturn(unified);

        mockMvc.perform(get("/api/history/AAPL")
                        .param("range", "1mo")
                        .param("interval", "1d")
                        .param("mode", "unified"))
                .andExpect(status().isOk())
                .andExpect(content().json(unified.toString()));

        verify(unifiedHistoryService).unify(ORIGINAL_JSON);
    }

    @Test
    void searchEndpointReturnsUnifiedResponse() throws Exception {
        ObjectNode unified = unifiedJson("search");
        when(searchClient.search("apple")).thenReturn(ORIGINAL_JSON);
        when(unifiedSearchService.unify(ORIGINAL_JSON)).thenReturn(unified);

        mockMvc.perform(get("/api/search/apple").param("mode", "unified"))
                .andExpect(status().isOk())
                .andExpect(content().json(unified.toString()));

        verify(unifiedSearchService).unify(ORIGINAL_JSON);
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void endpointsRejectInvalidParameters(String path, String parameterName, String parameterValue) throws Exception {
        mockMvc.perform(get(path).param(parameterName, parameterValue))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"invalid_request\"}"));
    }

    @Test
    void quoteEndpointRequiresSymbolsParameter() throws Exception {
        mockMvc.perform(get("/api/quote"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"invalid_request\"}"));
    }

    @Test
    void historyEndpointRequiresRangeParameter() throws Exception {
        mockMvc.perform(get("/api/history/AAPL").param("interval", "1d"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"invalid_request\"}"));
    }

    @Test
    void historyEndpointRequiresIntervalParameter() throws Exception {
        mockMvc.perform(get("/api/history/AAPL").param("range", "1mo"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"invalid_request\"}"));
    }

    private ObjectNode unifiedJson(String endpointName) {
        ObjectNode node = mapper.createObjectNode();
        node.put("endpoint", endpointName);
        node.put("source", "unified");
        return node;
    }

    private static Stream<Arguments> quoteSummaryOriginalEndpoints() {
        return Stream.of(
                Arguments.of("/api/price/AAPL", "price"),
                Arguments.of("/api/fundamentals/AAPL", "summaryDetail,financialData,defaultKeyStatistics"),
                Arguments.of("/api/financials/AAPL", "balanceSheetHistory,incomeStatementHistory,cashflowStatementHistory"),
                Arguments.of("/api/earnings/AAPL", "earnings"),
                Arguments.of("/api/profile/AAPL", "summaryProfile,assetProfile"),
                Arguments.of("/api/financialData/AAPL", "financialData")
        );
    }

    private static Stream<UnifiedEndpoint> quoteSummaryUnifiedEndpoints() {
        return Stream.of(
                new UnifiedEndpoint("price", "/api/price/AAPL", "price"),
                new UnifiedEndpoint("fundamentals", "/api/fundamentals/AAPL", "summaryDetail,financialData,defaultKeyStatistics"),
                new UnifiedEndpoint("financials", "/api/financials/AAPL", "balanceSheetHistory,incomeStatementHistory,cashflowStatementHistory"),
                new UnifiedEndpoint("earnings", "/api/earnings/AAPL", "earnings"),
                new UnifiedEndpoint("profile", "/api/profile/AAPL", "summaryProfile,assetProfile"),
                new UnifiedEndpoint("financialData", "/api/financialData/AAPL", "financialData")
        );
    }

    private static Stream<Arguments> invalidRequests() {
        return Stream.of(
                Arguments.of("/api/quote", "symbols", "AAPL&range=10y"),
                Arguments.of("/api/quote", "mode", "debug"),
                Arguments.of("/api/history/AAPL?interval=1d", "range", "30y"),
                Arguments.of("/api/history/AAPL?range=1mo", "interval", "10m"),
                Arguments.of("/api/history/AAPL?range=1mo&interval=1d", "mode", "debug"),
                Arguments.of("/api/price/AAPL_", "mode", "original"),
                Arguments.of("/api/search/apple", "mode", "debug")
        );
    }

    private record UnifiedEndpoint(String name, String path, String modules) {

        private void stubUnifiedResponse(EndpointControllerTests tests, ObjectNode unified) {
            switch (name) {
                case "price" -> when(tests.unifiedPriceService.unify(anyString())).thenReturn(unified);
                case "fundamentals" -> when(tests.unifiedFundamentalsService.unify(anyString())).thenReturn(unified);
                case "financials" -> when(tests.unifiedFinancialsService.unify(anyString())).thenReturn(unified);
                case "earnings" -> when(tests.unifiedEarningsService.unify(anyString())).thenReturn(unified);
                case "profile" -> when(tests.unifiedProfileService.unify(anyString())).thenReturn(unified);
                case "financialData" -> when(tests.unifiedFinancialDataService.unify(anyString())).thenReturn(unified);
                default -> throw new IllegalArgumentException("Unknown endpoint: " + name);
            }
        }

        private void verifyUnifiedCall(EndpointControllerTests tests) {
            switch (name) {
                case "price" -> verify(tests.unifiedPriceService).unify(ORIGINAL_JSON);
                case "fundamentals" -> verify(tests.unifiedFundamentalsService).unify(ORIGINAL_JSON);
                case "financials" -> verify(tests.unifiedFinancialsService).unify(ORIGINAL_JSON);
                case "earnings" -> verify(tests.unifiedEarningsService).unify(ORIGINAL_JSON);
                case "profile" -> verify(tests.unifiedProfileService).unify(ORIGINAL_JSON);
                case "financialData" -> verify(tests.unifiedFinancialDataService).unify(ORIGINAL_JSON);
                default -> throw new IllegalArgumentException("Unknown endpoint: " + name);
            }
        }
    }
}
