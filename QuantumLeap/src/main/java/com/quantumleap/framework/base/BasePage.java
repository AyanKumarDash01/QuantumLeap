package com.quantumleap.framework.base;

import com.quantumleap.framework.config.ConfigManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Base Page class containing common page operations and utilities
 * All page classes should extend this base class
 * 
 * @author QuantumLeap Team
 */
public abstract class BasePage {
    
    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor jsExecutor;
    protected ConfigManager config;
    
    /**
     * Constructor to initialize BasePage
     * 
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigManager.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWait()));
        this.jsExecutor = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Navigate to a specific URL
     * 
     * @param url URL to navigate to
     */
    protected void navigateTo(String url) {
        try {
            driver.get(url);
            logger.info("Navigated to URL: {}", url);
        } catch (Exception e) {
            logger.error("Failed to navigate to URL: {}", url, e);
            throw new RuntimeException("Navigation failed", e);
        }
    }
    
    /**
     * Wait for element to be visible
     * 
     * @param element WebElement to wait for
     * @return WebElement once visible
     */
    protected WebElement waitForElementToBeVisible(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            logger.error("Element not visible within timeout", e);
            throw new RuntimeException("Element not visible", e);
        }
    }
    
    /**
     * Wait for element to be clickable
     * 
     * @param element WebElement to wait for
     * @return WebElement once clickable
     */
    protected WebElement waitForElementToBeClickable(WebElement element) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException e) {
            logger.error("Element not clickable within timeout", e);
            throw new RuntimeException("Element not clickable", e);
        }
    }
    
    /**
     * Wait for element to be present
     * 
     * @param locator By locator to find element
     * @return WebElement once present
     */
    protected WebElement waitForElementToBePresent(By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("Element not present within timeout: {}", locator, e);
            throw new RuntimeException("Element not present", e);
        }
    }
    
    /**
     * Click on element with wait
     * 
     * @param element WebElement to click
     */
    protected void clickElement(WebElement element) {
        try {
            waitForElementToBeClickable(element);
            element.click();
            logger.debug("Clicked on element: {}", element);
        } catch (Exception e) {
            logger.error("Failed to click element", e);
            // Try JavaScript click as fallback
            clickElementWithJS(element);
        }
    }
    
    /**
     * Click element using JavaScript
     * 
     * @param element WebElement to click
     */
    protected void clickElementWithJS(WebElement element) {
        try {
            jsExecutor.executeScript("arguments[0].click();", element);
            logger.debug("Clicked element using JavaScript");
        } catch (Exception e) {
            logger.error("Failed to click element with JavaScript", e);
            throw new RuntimeException("Click failed", e);
        }
    }
    
    /**
     * Enter text in input field
     * 
     * @param element WebElement input field
     * @param text Text to enter
     */
    protected void enterText(WebElement element, String text) {
        try {
            waitForElementToBeVisible(element);
            element.clear();
            element.sendKeys(text);
            logger.debug("Entered text '{}' in element", text);
        } catch (Exception e) {
            logger.error("Failed to enter text in element", e);
            throw new RuntimeException("Text input failed", e);
        }
    }
    
    /**
     * Get text from element
     * 
     * @param element WebElement to get text from
     * @return Element text
     */
    protected String getText(WebElement element) {
        try {
            waitForElementToBeVisible(element);
            String text = element.getText();
            logger.debug("Retrieved text: '{}'", text);
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from element", e);
            throw new RuntimeException("Get text failed", e);
        }
    }
    
    /**
     * Get attribute value from element
     * 
     * @param element WebElement
     * @param attributeName Attribute name
     * @return Attribute value
     */
    protected String getAttributeValue(WebElement element, String attributeName) {
        try {
            waitForElementToBeVisible(element);
            String value = element.getAttribute(attributeName);
            logger.debug("Retrieved attribute '{}' value: '{}'", attributeName, value);
            return value;
        } catch (Exception e) {
            logger.error("Failed to get attribute '{}' from element", attributeName, e);
            throw new RuntimeException("Get attribute failed", e);
        }
    }
    
    /**
     * Select dropdown option by visible text
     * 
     * @param element Select element
     * @param visibleText Visible text to select
     */
    protected void selectDropdownByVisibleText(WebElement element, String visibleText) {
        try {
            waitForElementToBeVisible(element);
            Select select = new Select(element);
            select.selectByVisibleText(visibleText);
            logger.debug("Selected dropdown option: '{}'", visibleText);
        } catch (Exception e) {
            logger.error("Failed to select dropdown option: '{}'", visibleText, e);
            throw new RuntimeException("Dropdown selection failed", e);
        }
    }
    
    /**
     * Select dropdown option by value
     * 
     * @param element Select element
     * @param value Value to select
     */
    protected void selectDropdownByValue(WebElement element, String value) {
        try {
            waitForElementToBeVisible(element);
            Select select = new Select(element);
            select.selectByValue(value);
            logger.debug("Selected dropdown by value: '{}'", value);
        } catch (Exception e) {
            logger.error("Failed to select dropdown by value: '{}'", value, e);
            throw new RuntimeException("Dropdown selection failed", e);
        }
    }
    
    /**
     * Check if element is displayed
     * 
     * @param element WebElement to check
     * @return true if displayed, false otherwise
     */
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not displayed");
            return false;
        }
    }
    
    /**
     * Check if element is enabled
     * 
     * @param element WebElement to check
     * @return true if enabled, false otherwise
     */
    protected boolean isElementEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            logger.debug("Element not enabled");
            return false;
        }
    }
    
    /**
     * Scroll element into view
     * 
     * @param element WebElement to scroll to
     */
    protected void scrollToElement(WebElement element) {
        try {
            jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            logger.debug("Scrolled to element");
            Thread.sleep(500); // Wait for scroll animation
        } catch (Exception e) {
            logger.error("Failed to scroll to element", e);
        }
    }
    
    /**
     * Scroll to top of page
     */
    protected void scrollToTop() {
        try {
            jsExecutor.executeScript("window.scrollTo(0, 0);");
            logger.debug("Scrolled to top of page");
        } catch (Exception e) {
            logger.error("Failed to scroll to top", e);
        }
    }
    
    /**
     * Scroll to bottom of page
     */
    protected void scrollToBottom() {
        try {
            jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            logger.debug("Scrolled to bottom of page");
        } catch (Exception e) {
            logger.error("Failed to scroll to bottom", e);
        }
    }
    
    /**
     * Wait for page to load completely
     */
    protected void waitForPageLoad() {
        try {
            wait.until(webDriver -> jsExecutor.executeScript("return document.readyState").equals("complete"));
            logger.debug("Page loaded completely");
        } catch (Exception e) {
            logger.error("Page load timeout", e);
        }
    }
    
    /**
     * Get current page title
     * 
     * @return Page title
     */
    protected String getPageTitle() {
        try {
            String title = driver.getTitle();
            logger.debug("Page title: '{}'", title);
            return title;
        } catch (Exception e) {
            logger.error("Failed to get page title", e);
            return "";
        }
    }
    
    /**
     * Get current URL
     * 
     * @return Current URL
     */
    protected String getCurrentUrl() {
        try {
            String url = driver.getCurrentUrl();
            logger.debug("Current URL: '{}'", url);
            return url;
        } catch (Exception e) {
            logger.error("Failed to get current URL", e);
            return "";
        }
    }
    
    /**
     * Get all elements matching locator
     * 
     * @param locator By locator
     * @return List of WebElements
     */
    protected List<WebElement> findElements(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            logger.debug("Found {} elements with locator: {}", elements.size(), locator);
            return elements;
        } catch (Exception e) {
            logger.error("Failed to find elements with locator: {}", locator, e);
            throw new RuntimeException("Find elements failed", e);
        }
    }
    
    /**
     * Wait for specified time
     * 
     * @param seconds Time to wait in seconds
     */
    protected void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            logger.debug("Waited for {} seconds", seconds);
        } catch (InterruptedException e) {
            logger.error("Wait interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
