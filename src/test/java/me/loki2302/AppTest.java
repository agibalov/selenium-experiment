package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AppTest {
    @Configuration
    @Import(WebDriverConfiguration.class)
    public static class TestApp extends App {
        @Override
        public MessageProvider messageProvider() {
            return mock(MessageProvider.class);
        }
    }

    @Autowired
    private MessageProvider messageProvider;

    @Autowired
    private WebDriver webDriver;

    @Autowired
    private WebDriverUtils webDriverUtils;

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
        // see the nicer solution below
        webDriverUtils.synchronizeAngular2();
        webDriverUtils.synchronizeAngular2();
        webDriverUtils.synchronizeAngular2();
        webDriverUtils.synchronizeAngular2();
        webDriverUtils.synchronizeAngular2();
        webDriverUtils.synchronizeAngular2();
        webDriverUtils.dumpLogs();

        assertEquals(String.format("message is %s", TEST_MESSAGE), webDriver.findElement(By.tagName("h1")).getText());
    }

    @Test
    public void angular2ButtonShouldRevealTheMessageSmart() {
        final String TEST_MESSAGE = "hello test";

        when(messageProvider.getMessage()).thenReturn(TEST_MESSAGE);

        webDriver.get("http://localhost:8080/angular2-app.html");

        webDriver.findElement(By.tagName("button")).click();
        webDriverUtils.synchronizeAngular2Smart();
        webDriverUtils.dumpLogs();

        assertEquals(String.format("message is %s", TEST_MESSAGE), webDriver.findElement(By.tagName("h1")).getText());
    }

    @Test
    public void angular2InvokeExposedApi() {
        final String TEST_MESSAGE = "hello test";

        when(messageProvider.getMessage()).thenReturn(TEST_MESSAGE);

        webDriver.get("http://localhost:8080/angular2-app.html");

        webDriver.findElement(By.tagName("button")).click();
        webDriverUtils.synchronizeAngular2();

        Object result = ((JavascriptExecutor)webDriver).executeScript("return window.addNumbers(2, 3);");
        assertEquals(5L, (long)(Long)result);
        webDriverUtils.dumpLogs();
    }

    @Test
    public void angularJsButtonShouldRevealTheMessage() throws IOException {
        final String TEST_MESSAGE = "hello test";

        when(messageProvider.getMessage()).thenReturn(TEST_MESSAGE);

        webDriver.get("http://localhost:8080/angularjs-app.html");
        webDriverUtils.makeScreenshot(new File("1.png"));

        WebElement buttonWebElement = webDriver.findElement(By.tagName("button"));
        webDriverUtils.highlight(buttonWebElement);
        webDriverUtils.makeScreenshot(new File("2.png"));
        webDriverUtils.unhighlight();
        buttonWebElement.click();
        webDriverUtils.synchronizeAngularJs();

        assertEquals(TEST_MESSAGE, webDriver.findElement(By.tagName("h1")).getText());
        webDriverUtils.makeScreenshot(new File("3.png"));

        webDriverUtils.dumpLogs();
    }

    private static ExpectedCondition<Boolean> elementTextIsNotEmpty(By by) {
        return input -> !input.findElement(by).getText().isEmpty();
    }
}
