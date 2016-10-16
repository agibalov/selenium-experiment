package me.loki2302;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebDriverUtils {
    @Autowired
    private WebDriver webDriver;

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
        String syncScript = String.join("\n",
                "console.log('before');",
                "var done = arguments[0];",
                "angular.getTestability(document.body).whenStable(function() { console.log('stable'); done(); });",
                "console.log('after');");

        ((JavascriptExecutor)webDriver).executeAsyncScript(syncScript);
    }

    public void synchronizeAngular2() {
        String syncScript = String.join("\n",
                "console.log('sync - before');",
                "var done = arguments[0];",
                "window.getAngularTestability(document.querySelector('app')).whenStable(function() { console.log('sync - stable'); done(); });",
                "console.log('sync - after');");

        ((JavascriptExecutor)webDriver).executeAsyncScript(syncScript);
    }

    public void highlight(WebElement webElement) {
        Point point = webElement.getLocation();
        Dimension size = webElement.getSize();
        int highlightSize = 30;
        int cx = point.getX() + size.getWidth() / 2 - highlightSize / 2;
        int cy = point.getY() + size.getHeight() / 2 - highlightSize / 2;

        String highlightScript = String.format(
                "(function() { " +
                        "var el = document.createElement('div');" +
                        "el.setAttribute('id', 'highlighter');" +
                        "el.style.position='fixed';" +
                        "el.style.left='%dpx';" +
                        "el.style.top='%dpx';" +
                        "el.style.width='%dpx';" +
                        "el.style.height='%dpx';" +
                        "el.style.border='3px solid red';" +
                        "document.body.appendChild(el); " +
                        "})()", cx, cy, highlightSize, highlightSize);
        ((JavascriptExecutor) webDriver).executeScript(highlightScript);
    }

    public void unhighlight() {
        String unhighlightScript =
                "(function() { " +
                        "var el = document.getElementById('highlighter');" +
                        "document.body.removeChild(el);" +
                        "})()";
        ((JavascriptExecutor) webDriver).executeScript(unhighlightScript);
    }
}
