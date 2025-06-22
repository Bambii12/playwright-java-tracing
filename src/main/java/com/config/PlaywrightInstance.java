package com.config;

import com.properties.TestConfig;
import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


@Slf4j
@Configuration
public class PlaywrightInstance {

    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> browserContextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    public PlaywrightInstance() {
    }

    private Playwright getPlaywright() {
        if (playwrightThreadLocal.get() == null) {
            Playwright playwright = Playwright.create();
            playwrightThreadLocal.set(playwright);
        }
        return playwrightThreadLocal.get();
    }

    private Browser getBrowser() {
        if (browserThreadLocal.get() == null) {
            Playwright playwright = getPlaywright();
            Browser browser = switch (TestConfig.browserType) {
                case "safari" -> playwright
                        .webkit()
                        .launch(new BrowserType.LaunchOptions()
                                .setHeadless(true).setSlowMo(1000));
                case "chrome" -> playwright
                        .chromium()
                        .launch(new BrowserType.LaunchOptions()
                                .setHeadless(true).setSlowMo(1000)
                        );
                case "firefox" -> playwright
                        .firefox()
                        .launch(new BrowserType.LaunchOptions()
                                .setHeadless(true).setSlowMo(1000));
                default -> playwright.chromium().launch();
            };
            browserThreadLocal.set(browser);
        }
        return browserThreadLocal.get();
    }

    public BrowserContext getBrowserContext() {
        return browserContextThreadLocal.get();
    }

    public void createContextAndPage() {
        if (pageThreadLocal.get() == null || pageThreadLocal.get().isClosed()) {
            Browser browser = getBrowser();
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setIgnoreHTTPSErrors(true));
            context.setDefaultTimeout(20000);

            if (TestConfig.tracing) {
                context.tracing().start(new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true)
                        .setTitle("TRACING DEMO")
                );
            }

            Page page = context.newPage();

            browserContextThreadLocal.set(context);
            pageThreadLocal.set(page);
            log.info("Initialized on browser: {}", browser.browserType().name());
        }
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Page getPage() {
        createContextAndPage();
        return pageThreadLocal.get();
    }

    public void closeBrowserContext() {
        BrowserContext context = browserContextThreadLocal.get();
        if (context != null) {
            if (TestConfig.tracing) {
                context.tracing().stop();
            }
            context.close();
            browserContextThreadLocal.remove();
        }
    }

    public void closeBrowser() {
        Browser browser = browserThreadLocal.get();
        if (browser != null) {
            browser.close();
            browserThreadLocal.remove();
        }
        Playwright playwright = playwrightThreadLocal.get();
        if (playwright != null) {
            playwright.close();
            playwrightThreadLocal.remove();
        }
    }

}

