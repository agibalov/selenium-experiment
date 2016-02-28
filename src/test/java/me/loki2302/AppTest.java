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
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AppTest {
    @Rule
    public final WebDriverRule webDriverRule = new WebDriverRule();
    private WebDriver webDriver;

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
        webDriver.get("http://localhost:8080/");
        webDriver.findElement(By.tagName("button")).click();
        new WebDriverWait(webDriver, 3).until(elementTextIsNotEmpty(By.tagName("h1")));
        assertEquals("hi there", webDriver.findElement(By.tagName("h1")).getText());
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
