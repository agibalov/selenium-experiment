package me.loki2302;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void pageTitleShouldSayHello() throws MalformedURLException {
        webDriver.get("http://localhost:8080/");
        assertEquals("Hello", webDriver.getTitle());
    }

    @Test
    public void buttonShouldRevealTheMessage_CRUTCH() {
        final String TEST_MESSAGE = "hello test";

        when(messageProvider.getMessage()).thenReturn(TEST_MESSAGE);

        webDriver.get("http://localhost:8080/");
        webDriver.findElement(By.tagName("button")).click();

        new WebDriverWait(webDriver, 3).until(elementTextIsNotEmpty(By.tagName("h1")));
        assertEquals(TEST_MESSAGE, webDriver.findElement(By.tagName("h1")).getText());
    }

    @Test
    public void buttonShouldRevealTheMessage() {
        final String TEST_MESSAGE = "hello test";

        when(messageProvider.getMessage()).thenReturn(TEST_MESSAGE);

        webDriver.get("http://localhost:8080/");
        webDriver.findElement(By.tagName("button")).click();

        String syncScript = String.join("\n",
                "console.log('before');",
                "var done = arguments[0];",
                "angular.getTestability(document.body).whenStable(function() { console.log('stable'); done(); });",
                "console.log('after');");

        ((JavascriptExecutor)webDriver).executeAsyncScript(syncScript);

        assertEquals(TEST_MESSAGE, webDriver.findElement(By.tagName("h1")).getText());

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

                    webDriver = new ChromeDriver(desiredCapabilities);
                    webDriver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);

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
}
