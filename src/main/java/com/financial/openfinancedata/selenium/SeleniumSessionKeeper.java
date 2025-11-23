package com.financial.openfinancedata.selenium;

import com.financial.openfinancedata.model.ModelSession;
import com.financial.openfinancedata.session.YahooSessionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.HttpCookie;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeleniumSessionKeeper {

    private final SeleniumWebDriverProvider driverProvider;
    private final YahooSessionStore sessionStore;

    // CSS selector baseado na classe solicitada: qualquer elemento com essa classe
    private static final String CLICK_TARGET_SELECTOR = "#ybar-logo";

    /**
     * Agendado para rodar a cada 5 minutos (300_000 ms).
     * Mantém a página ativa clicando/refresh e atualiza cookies + crumb.
     */
    @Scheduled(fixedRate = 300_000, initialDelay = 5_000)
    public void keepSessionAlive() {
        WebDriver driver = null;
        ModelSession newState = new ModelSession();

        try {
            driver = driverProvider.getDriver();
            if (driver == null) {
                throw new IllegalStateException("WebDriver não disponível.");
            }

            // 1) Se já estivermos em outra página, garantir que vamos para finance.yahoo.com
            try {
                driver.navigate().to("https://finance.yahoo.com");
            } catch (WebDriverException e) {
                log.warn("Navegação falhou; reiniciando driver e tentando novamente.");
                driverProvider.restartDriver();
                driver = driverProvider.getDriver();
                driver.navigate().to("https://finance.yahoo.com");
            }

            // dá um tempo para scripts carregarem
            sleepSafe(1500);

            // 2) Tentar encontrar e clicar no elemento alvo; se não encontrado, fazemos refresh
            boolean clicked = tryClickTarget(driver);
            if (!clicked) {
                log.debug("Elemento alvo não encontrado — executando refresh.");
                driver.navigate().refresh();
                sleepSafe(1000);
                // tentar clicar mais uma vez
                clicked = tryClickTarget(driver);
                if (!clicked) log.debug("Elemento alvo ainda não encontrado após refresh.");
            } else {
                log.debug("Elemento alvo clicado com sucesso para manter a sessão ativa.");
            }

            sleepSafe(500); // espera pós-clique para eventuais JS

            // 3) Coletar cookies do navegador Selenium e convertê-los
            List<HttpCookie> cookies = driver.manage().getCookies().stream()
                    .map(c -> {
                        HttpCookie hc = new HttpCookie(c.getName(), c.getValue());
                        // tenta copiar atributos importantes quando possível (path/domain)
                        try {
                            if (c.getDomain() != null) hc.setDomain(c.getDomain());
                            if (c.getPath() != null) hc.setPath(c.getPath());
                        } catch (Exception ignored) {}
                        return hc;
                    })
                    .collect(Collectors.toList());

            newState.setCookies(cookies);

            // 4) Extrair crumb via JavaScript (tenta múltiplas expressões se necessário)
            String crumb = extractCrumb(driver);
            newState.setCrumb(crumb);

            newState.setCreatedAt(Instant.now());
            newState.setValid(crumb != null && !crumb.isBlank());

            sessionStore.setCurrentState(newState);
            log.info("Sessão Yahoo atualizada. Crumb presente? {}", newState.isValid());

        } catch (Exception ex) {
            log.error("Falha ao atualizar sessão via Selenium: {}", ex.getMessage(), ex);
            newState.setValid(false);
            newState.setLastError(ex.getMessage());
            newState.setCreatedAt(Instant.now());
            sessionStore.setCurrentState(newState);

            // fallback: tentar reiniciar driver na próxima execução
            try {
                driverProvider.restartDriver();
            } catch (Exception e) {
                log.warn("Falha ao reiniciar driver no fallback: {}", e.getMessage());
            }
        }
    }

    private boolean tryClickTarget(WebDriver driver) {
    try {
        // procura pelo elemento por selector de ID
        List<WebElement> elements = driver.findElements(By.cssSelector(CLICK_TARGET_SELECTOR));
        if (elements == null || elements.isEmpty()) return false;

        WebElement el = elements.get(0);

        // tenta click via Actions (mais robusto)
        Actions actions = new Actions(driver);
        actions.moveToElement(el).click().perform();

        return true;

    } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
        log.debug("Elemento presente mas não clicável agora: {}", e.getMessage());
        return false;

    } catch (NoSuchElementException e) {
        return false;

    } catch (Exception e) {
        log.warn("Erro ao tentar clicar elemento: {}", e.getMessage());
        return false;
    }
}

    /**
     * Extrai crumb com JS. Tenta a expressão padrão e, se falhar, tenta alternativas.
     */
    private String extractCrumb(WebDriver driver) {
        try {
            Object result;
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // expressão 1: padrão observado
            String script1 =
                "return (window.root && root.App && root.App.main && " +
                "root.App.main.context && root.App.main.context.dispatcher && " +
                "root.App.main.context.dispatcher.stores && " +
                "root.App.main.context.dispatcher.stores.CrumbStore && " +
                "root.App.main.context.dispatcher.stores.CrumbStore.crumb) || null;";

            result = js.executeScript(script1);
            if (result instanceof String && !((String) result).isBlank()) return (String) result;

            // expressão 2: às vezes o crumb está em outro objeto
            String script2 =
                "try { for (var k in window) { if (window[k] && window[k].App && window[k].App.main) { " +
                "var s = window[k].App.main.context && window[k].App.main.context.dispatcher && " +
                "window[k].App.main.context.dispatcher.stores && window[k].App.main.context.dispatcher.stores.CrumbStore; " +
                "if (s && s.crumb) return s.crumb; } } } catch(e){}; return null;";
            result = js.executeScript(script2);
            if (result instanceof String && !((String) result).isBlank()) return (String) result;

            // expressão 3: fallback — buscar pattern no HTML
            String script3 =
                "var h = document.documentElement.innerHTML || document.body.innerHTML; " +
                "var m = h.match(/\"crumb\":\"([^\"]+)\"/); return m ? m[1] : null;";
            result = js.executeScript(script3);
            if (result instanceof String && !((String) result).isBlank()) return (String) result;

            return null;

        } catch (Exception ex) {
            log.warn("Erro extraindo crumb via JS: {}", ex.getMessage());
            return null;
        }
    }

    private void sleepSafe(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
