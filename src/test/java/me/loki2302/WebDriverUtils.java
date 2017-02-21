package me.loki2302;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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

    public void makeScreenshotAwt(File file) {
        WebDriver.Window window = webDriver.manage().window();
        int width = window.getSize().getWidth();
        int height = window.getSize().getHeight();
        int left = window.getPosition().getX();
        int top = window.getPosition().getY();

        Robot robot;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        BufferedImage bufferedImage = robot.createScreenCapture(new java.awt.Rectangle(
                new java.awt.Point(left, top),
                new java.awt.Dimension(width, height)));
        try {
            ImageIO.write(bufferedImage, "png", file);
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
        Object didWork = ((JavascriptExecutor)webDriver).executeAsyncScript(scriptContent);
        LOGGER.info("didWork={}", didWork);
    }

    /**
     * One call to Angular's whenStable() only seems to address one async operation.
     * Its callback gets called with a single parameter - didWork
     * (see https://github.com/angular/angular/blob/50c37d45dc8a4f5e38afaf594c9d5017e8eab3bd/modules/%40angular/platform-browser/src/browser/testability.ts#L36)
     * It's true, when we actually had to wait for something and
     * false when there was nothing to wait. So, practically, the snippet should be:
     *
     * while(true) {
     *   boolean didWork = doWhenStableSync()
     *   if(didWork) {
     *     // there was something to wait for
     *     // so probably there's even more
     *     // so we try one more time
     *     continue;
     *   }
     *
     *   // there was nothing to wait for, so we're done here
     *   break;
     * }
     */
    private final static int MAX_ANGULAR2_SYNC_ITERATIONS = 100;
    public void synchronizeAngular2Smart() {
        String scriptContent = readResource(angular2SyncScript);

        for(int i = 0; i < MAX_ANGULAR2_SYNC_ITERATIONS; ++i) {
            boolean didWork = (Boolean) ((JavascriptExecutor) webDriver).executeAsyncScript(scriptContent);
            boolean shouldSyncOneMoreTime = didWork;
            LOGGER.info("Attempt #{}, shouldSyncOneMoreTime={}", i, shouldSyncOneMoreTime);
            if(shouldSyncOneMoreTime) {
                continue;
            }

            LOGGER.info("Should be stable now");
            break;
        }
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
