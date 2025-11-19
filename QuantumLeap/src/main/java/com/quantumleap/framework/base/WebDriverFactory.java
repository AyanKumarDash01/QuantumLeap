package com.quantumleap.framework.base;

import com.quantumleap.framework.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

/**
 * WebDriver Factory to create and manage WebDriver instances
 * Supports multiple browsers with various configurations
 * 
 * @author QuantumLeap Team
 */
public class WebDriverFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConfigManager config = ConfigManager.getInstance();
    
    /**
     * Create WebDriver instance based on configuration
     * 
     * @return WebDriver instance
     */
    public static WebDriver createDriver() {
        String browserName = config.getBrowser().toLowerCase();
        boolean headless = config.isHeadless();
        
        WebDriver driver;
        
        switch (browserName) {
            case "chrome":
                driver = createChromeDriver(headless);
                break;
            case "firefox":
                driver = createFirefoxDriver(headless);
                break;
            case "edge":
                driver = createEdgeDriver(headless);
                break;
            default:
                logger.warn("Browser '{}' not supported. Using Chrome as default.", browserName);
                driver = createChromeDriver(headless);
                break;
        }
        
        configureDriver(driver);
        setDriver(driver);
        
        logger.info("WebDriver created successfully: {}", browserName);
        return driver;
    }
    
    /**
     * Create Chrome WebDriver with enhanced options to prevent dialog blocking
     * 
     * @param headless Whether to run in headless mode
     * @return ChromeDriver instance
     */
    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        // Basic stability options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        
        // Disable password manager and autofill features (CRITICAL FOR FIXING THE STUCK ISSUE)
        options.addArguments("--disable-password-generation");
        options.addArguments("--disable-password-manager-reauthentication");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=VizDisplayCompositor,PasswordManager,AutofillServerCommunication");
        
        // Disable notifications and popups
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-background-mode");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-background-timer-throttling");
        
        // Disable automation detection
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-features=TranslateUI");
        options.addArguments("--no-first-run");
        options.addArguments("--no-service-autorun");
        options.addArguments("--password-store=basic");
        options.addArguments("--use-mock-keychain");
        
        // Performance optimizations
        options.addArguments("--disable-hang-monitor");
        options.addArguments("--disable-prompt-on-repost");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-sync");
        options.addArguments("--metrics-recording-only");
        options.addArguments("--safebrowsing-disable-auto-update");
        options.addArguments("--enable-automation");
        
        // Set preferences to disable password manager completely
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("profile.managed_default_content_settings.images", 1);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_leak_detection_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        
        // Disable automation extension and features
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        
        return new ChromeDriver(options);
    }
    
    /**
     * Create Firefox WebDriver
     * 
     * @param headless Whether to run in headless mode
     * @return FirefoxDriver instance
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        
        return new FirefoxDriver(options);
    }
    
    /**
     * Create Edge WebDriver
     * 
     * @param headless Whether to run in headless mode
     * @return EdgeDriver instance
     */
    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        
        return new EdgeDriver(options);
    }
    
    /**
     * Configure WebDriver with timeouts and settings
     * 
     * @param driver WebDriver instance to configure
     */
    private static void configureDriver(WebDriver driver) {
        // Set timeouts to prevent hanging
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30)); // Add script timeout
        
        // Maximize window for consistent behavior
        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            logger.warn("Could not maximize window: {}", e.getMessage());
            // Try setting a specific size as fallback
            driver.manage().window().setSize(new Dimension(1920, 1080));
        }
    }
    
    /**
     * Set WebDriver instance in ThreadLocal for thread-safe operations
     * 
     * @param driver WebDriver instance
     */
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }
    
    /**
     * Get current WebDriver instance from ThreadLocal
     * 
     * @return Current WebDriver instance
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new RuntimeException("WebDriver not initialized. Call createDriver() first.");
        }
        return driver;
    }
    
    /**
     * Quit WebDriver and clean up ThreadLocal
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver quit successfully");
            } catch (Exception e) {
                logger.error("Error while quitting WebDriver", e);
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
    
    /**
     * Check if WebDriver is initialized
     * 
     * @return true if driver is initialized, false otherwise
     */
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }
    
    /**
     * Dismiss any browser dialogs or popups that might block automation
     * This should be called when tests get stuck on browser dialogs
     */
    public static void dismissBrowserDialogs() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                // Try to dismiss any alert dialogs
                driver.switchTo().alert().dismiss();
                logger.info("Dismissed browser alert dialog");
            } catch (Exception e) {
                // No alert present, which is fine
                logger.debug("No alert dialog to dismiss: {}", e.getMessage());
            }
            
            try {
                // Execute JavaScript to close any password manager popups
                ((JavascriptExecutor) driver).executeScript(
                    "var elements = document.querySelectorAll('[data-testid], [role=\"dialog\"], .password-bubble, .save-password');" +
                    "elements.forEach(function(el) { el.style.display = 'none'; el.remove(); });"
                );
                logger.info("Attempted to dismiss password manager dialogs via JavaScript");
            } catch (Exception e) {
                logger.debug("Could not execute JavaScript to dismiss dialogs: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Force quit all browser processes (use as last resort)
     */
    public static void forceQuitAllBrowsers() {
        try {
            // Kill any remaining Chrome processes
            Runtime.getRuntime().exec("pkill -f chrome");
            Runtime.getRuntime().exec("pkill -f chromedriver");
            Thread.sleep(2000); // Wait for processes to be killed
            logger.info("Forced quit all browser processes");
        } catch (Exception e) {
            logger.error("Error during force quit of browsers", e);
        }
    }
}
