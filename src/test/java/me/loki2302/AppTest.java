package me.loki2302;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AppTest {
    @Configuration
    public static class TestApp extends App {
        @Override
        public MessageProvider messageProvider() {
            return mock(MessageProvider.class);
        }
    }

    @Rule
    public final WebDriverRule webDriverRule = new WebDriverRule();
    private WebDriver webDriver;

    @Autowired
    private MessageProvider messageProvider;

    @Before
    public void setWebDriver() {
        webDriver = webDriverRule.getWebDriver();
    }

    @Test
    public void angularJsPageTitleShouldSayHello() throws MalformedURLException {
        webDriver.get("http://localhost:8080/angularjs-app.html");
        assertEquals("Hello", webDriver.getTitle());
    }

    @Test
    public void angularJsButtonShouldRevealTheMessage_CRUTCH() {
        final String TEST_MESSAGE = "hello test";

        when(messageProvider.getMessage()).thenReturn(TEST_MESSAGE);

        webDriver.get("http://localhost:8080/angularjs-app.html");
        webDriver.findElement(By.tagName("button")).click();

        new WebDriverWait(webDriver, 3).until(elementTextIsNotEmpty(By.tagName("h1")));
        assertEquals(TEST_MESSAGE, webDriver.findElement(By.tagName("h1")).getText());
    }

    @Test
    public void angular2ButtonShouldRevealTheMessage() {
        final String TEST_MESSAGE = "hello test";

        when(messageProvider.getMessage()).thenReturn(TEST_MESSAGE);

        webDriver.get("http://localhost:8080/angular2-app.html");

        webDriver.findElement(By.tagName("button")).click();

        String syncScript = String.join("\n",
                "console.log('sync - before');",
                "var done = arguments[0];",
                "window.getAngularTestability(document.querySelector('app')).whenStable(function() { console.log('sync - stable'); done(); });",
                "console.log('sync - after');");

        ((JavascriptExecutor)webDriver).executeAsyncScript(syncScript);

        assertEquals(String.format("message is %s", TEST_MESSAGE), webDriver.findElement(By.tagName("h1")).getText());

        LogEntries logEntries = webDriver.manage().logs().get(LogType.BROWSER);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        for(LogEntry logEntry : logEntries) {
            System.out.printf("%s|%s|%s\n",
                    format.format(new Date(logEntry.getTimestamp())),
                    logEntry.getLevel(),
                    logEntry.getMessage());
        }
    }

    @Test
    public void angularJsButtonShouldRevealTheMessage() throws IOException {
        final String TEST_MESSAGE = "hello test";

        when(messageProvider.getMessage()).thenReturn(TEST_MESSAGE);

        webDriver.get("http://localhost:8080/angularjs-app.html");

        if(true) {
            File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("1.png"));
        }

        if(true) {
            WebElement webElement = webDriver.findElement(By.tagName("button"));
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

        if(true) {
            File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("2.png"));
        }

        if(true) {
            String unhighlightScript =
                    "(function() { " +
                    "var el = document.getElementById('highlighter');" +
                    "document.body.removeChild(el);" +
                    "})()";
            ((JavascriptExecutor) webDriver).executeScript(unhighlightScript);
        }

        webDriver.findElement(By.tagName("button")).click();

        String syncScript = String.join("\n",
                "console.log('before');",
                "var done = arguments[0];",
                "angular.getTestability(document.body).whenStable(function() { console.log('stable'); done(); });",
                "console.log('after');");

        ((JavascriptExecutor)webDriver).executeAsyncScript(syncScript);

        assertEquals(TEST_MESSAGE, webDriver.findElement(By.tagName("h1")).getText());

        if(true) {
            File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("3.png"));
        }

        LogEntries logEntries = webDriver.manage().logs().get(LogType.BROWSER);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        for(LogEntry logEntry : logEntries) {
            System.out.printf("%s|%s|%s\n",
                    format.format(new Date(logEntry.getTimestamp())),
                    logEntry.getLevel(),
                    logEntry.getMessage());
        }
    }

    private static ExpectedCondition<Boolean> elementTextIsNotEmpty(By by) {
        return input -> !input.findElement(by).getText().isEmpty();
    }

    public static class WebDriverRule implements TestRule {
        private WebDriver webDriver;

        public WebDriver getWebDriver() {
            return webDriver;
        }

        @Override
        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    LoggingPreferences loggingPreferences = new LoggingPreferences();
                    loggingPreferences.enable(LogType.BROWSER, Level.ALL);

                    DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
                    desiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);

                    ChromeDriver chromeDriver = new ChromeDriver(desiredCapabilities);
                    chromeDriver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
                    chromeDriver.manage().window().setSize(new Dimension(1366, 768));

                    EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(chromeDriver);
                    eventFiringWebDriver.register(new MyWebDriverEventListener());

                    webDriver = eventFiringWebDriver;

                    try {
                        base.evaluate();
                    } finally {
                        webDriver.quit();
                        webDriver = null;
                    }
                }
            };
        }
    }

    public static class MyWebDriverEventListener extends AbstractWebDriverEventListener {
        private final static Logger LOGGER = LoggerFactory.getLogger(MyWebDriverEventListener.class);

        @Override
        public void beforeClickOn(WebElement element, WebDriver driver) {
            LOGGER.info("beforeClickOn() element={}", element);
        }

        @Override
        public void afterClickOn(WebElement element, WebDriver driver) {
            LOGGER.info("afterClickOn() element={}", element);
        }
    }
}
