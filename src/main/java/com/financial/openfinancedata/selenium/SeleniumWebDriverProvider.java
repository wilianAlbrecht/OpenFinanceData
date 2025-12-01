package com.financial.openfinancedata.selenium;

import java.net.URI;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SeleniumWebDriverProvider {

    private final ChromeOptions chromeOptions;
    private volatile WebDriver driver;

    public SeleniumWebDriverProvider(ChromeOptions chromeOptions) {
        this.chromeOptions = chromeOptions;
        initDriver();
    }

    private void initDriver() {
        int tentativas = 0;

        while (tentativas < 10) {
            try {
                tentativas++;

                if (driver != null) {
                    try {
                        driver.quit();
                    } catch (Exception ignored) {
                    }
                }

               //identificar se está rodando no Docker ou localmente
                String mode = System.getenv("SELENIUM_MODE");
                boolean rodandoNoDocker = mode != null && mode.equalsIgnoreCase("docker");

                //inicializar o driver no docker ou localmente
                if (rodandoNoDocker) {
                    log.info("Ambiente identificado: DOCKER : RemoteWebDriver");
                    driver = new RemoteWebDriver(
                            new URI("http://selenium:4444/wd/hub").toURL(),
                            chromeOptions);
                            return;
                } else {
                    log.info("Ambiente identificado: LOCAL : ChromeDriver");
                    driver = new ChromeDriver(chromeOptions);
                    return;
                }

            } catch (Exception ex) {
                log.warn("Falha ao iniciar WebDriver (tentativa {}): {}", tentativas, ex.getMessage());
                driver = null;
                sleepSafe(3000);
            }
        }

        throw new RuntimeException(
                "Não foi possível iniciar o WebDriver após " + tentativas + " tentativas.");
    }

    /**
     * Retorna o WebDriver ativo. Se estiver nulo ou morto, tenta reinicializar.
     */
    public synchronized WebDriver getDriver() {
        try {
            if (driver == null)
                initDriver();
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

    private void sleepSafe(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}
