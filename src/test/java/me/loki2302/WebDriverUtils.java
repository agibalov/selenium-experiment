package me.loki2302;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebDriverUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebDriverUtils.class);

    @Autowired
    private WebDriver webDriver;

    @Value("classpath:/angularjs-sync.js")
    private Resource angularJsSyncScript;

    @Value("classpath:/angular2-sync.js")
    private Resource angular2SyncScript;

    @Value("classpath:/highlight.js")
    private Resource highlightScript;

    @Value("classpath:/unhighlight.js")
    private Resource unhighlightScript;

    public void makeScreenshot(File file) {
        File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshot, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void dumpLogs() {
        LogEntries logEntries = webDriver.manage().logs().get(LogType.BROWSER);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        for(LogEntry logEntry : logEntries) {
            System.out.printf("%s|%s|%s\n",
                    format.format(new Date(logEntry.getTimestamp())),
                    logEntry.getLevel(),
                    logEntry.getMessage());
        }
    }

    public void synchronizeAngularJs() {
        String scriptContent = readResource(angularJsSyncScript);
        ((JavascriptExecutor)webDriver).executeAsyncScript(scriptContent);
    }

    public void synchronizeAngular2() {
        String scriptContent = readResource(angular2SyncScript);
        ((JavascriptExecutor)webDriver).executeAsyncScript(scriptContent);
    }

    public void highlight(WebElement webElement) {
        Point point = webElement.getLocation();
        Dimension size = webElement.getSize();
        int highlightSize = 30;
        int cx = point.getX() + size.getWidth() / 2 - highlightSize / 2;
        int cy = point.getY() + size.getHeight() / 2 - highlightSize / 2;

        String scriptContent = readResource(highlightScript);
        ((JavascriptExecutor)webDriver).executeScript(scriptContent, cx, cy, highlightSize, highlightSize);
    }

    public void unhighlight() {
        String scriptContent = readResource(unhighlightScript);
        ((JavascriptExecutor)webDriver).executeScript(scriptContent);
    }

    private static String readResource(Resource resource) {
        try {
            String scriptContent = new String(Files.readAllBytes(resource.getFile().toPath()), Charset.forName("UTF-8"));
            LOGGER.info("Read {} as {}", resource.getFile(), scriptContent);
            return scriptContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
