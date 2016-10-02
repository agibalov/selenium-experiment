package me.loki2302;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;

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
    public void buttonShouldRevealTheMessage() {
        final String TEST_MESSAGE = "hello test";

        when(messageProvider.getMessage()).thenReturn(TEST_MESSAGE);

        webDriver.get("http://localhost:8080/");
        webDriver.findElement(By.tagName("button")).click();
        new WebDriverWait(webDriver, 3).until(elementTextIsNotEmpty(By.tagName("h1")));
        assertEquals(TEST_MESSAGE, webDriver.findElement(By.tagName("h1")).getText());
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
                    System.setProperty("webdriver.chrome.driver", "node_modules/chromedriver/bin/chromedriver");
                    webDriver = new ChromeDriver();
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
