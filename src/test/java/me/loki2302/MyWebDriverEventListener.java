package me.loki2302;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyWebDriverEventListener extends AbstractWebDriverEventListener {
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
