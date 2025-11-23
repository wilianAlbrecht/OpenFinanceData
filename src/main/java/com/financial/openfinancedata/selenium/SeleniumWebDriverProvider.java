package com.financial.openfinancedata.selenium;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeleniumWebDriverProvider {

    private final ChromeOptions chromeOptions;
    private volatile WebDriver driver;

    public SeleniumWebDriverProvider(ChromeOptions chromeOptions) {
        this.chromeOptions = chromeOptions;
        initDriver();
    }

    private synchronized void initDriver() {
        try {
            if (driver != null) {
                try { driver.quit(); } catch (Exception ignored) {}
            }
            driver = new ChromeDriver(chromeOptions);
            log.info("Selenium WebDriver inicializado.");
        } catch (Exception ex) {
            driver = null;
            log.error("Falha ao inicializar WebDriver: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Retorna o WebDriver ativo. Se estiver nulo ou morto, tenta reinicializar.
     */
    public synchronized WebDriver getDriver() {
        try {
            if (driver == null) initDriver();
            return driver;
        } catch (Exception ex) {
            log.warn("Erro ao obter driver; tentando reinstanciar: {}", ex.getMessage());
            initDriver();
            return driver;
        }
    }

    /**
     * Força reinicialização do driver (usado em fallback).
     */
    public synchronized void restartDriver() {
        log.info("Reiniciando WebDriver por solicitação.");
        initDriver();
    }

    @PreDestroy
    public synchronized void shutdown() {
        try {
            if (driver != null) {
                driver.quit();
                log.info("WebDriver finalizado corretamente.");
            }
        } catch (Exception ex) {
            log.warn("Erro ao finalizar WebDriver: {}", ex.getMessage());
        } finally {
            driver = null;
        }
    }
}
