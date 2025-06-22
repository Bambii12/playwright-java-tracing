package com.playwrightdemo;

import com.config.PlaywrightInstance;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import com.properties.TestConfig;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.properties.TestConfig.tracing;

@Slf4j
public class PlaywrightBaseTest extends PlaywrightDemoApplicationTests {

    @Autowired
    protected PlaywrightInstance playwright;

    protected Page page;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        this.page = playwright.getPage();
        this.page.navigate(TestConfig.baseUrl);
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        playwright.closeBrowser();
    }

    @AfterMethod(alwaysRun = true)
    public void teardown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            addAttachments(result.getName());
        }
        playwright.closeBrowserContext();
    }

    private void addAttachments(String testName) {
        if (this.page != null) {
            try {
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                Allure.addAttachment("Failure Screenshot", new ByteArrayInputStream(screenshot));
                log.info("Screenshot captured on failure");
                if (tracing) {
                    String traceFileName = "trace_" + testName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMdd_HHmmss")) +  ".zip";
                    Path tracePath = Paths.get(traceFileName);
                    page.context().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                    try (FileInputStream fis = new FileInputStream(tracePath.toFile())) {
                        Allure.addAttachment(traceFileName, "application/zip", fis, ".zip");
                        log.info("Trace file attached to Allure");
                    }                }
            } catch (Exception e) {
                log.error("Failed to add attachments: {}", e.getMessage());
            }
        } else {
            log.error("Page is null, cannot add attachments");
        }
    }
}
