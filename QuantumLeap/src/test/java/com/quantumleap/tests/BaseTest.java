package com.quantumleap.tests;

import com.quantumleap.framework.base.WebDriverFactory;
import com.quantumleap.framework.config.ConfigManager;
import com.quantumleap.framework.utils.ExtentReportManager;
import com.quantumleap.framework.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

/**
 * BaseTest class providing common test setup and teardown functionality
 * All test classes should extend this base class
 * 
 * @author QuantumLeap Team
 */
public abstract class BaseTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected ConfigManager config;
    
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("=== Starting Test Suite Execution ===");
        config = ConfigManager.getInstance();
        
        // Clean up old screenshots
        ScreenshotUtils.cleanupOldScreenshots(7); // Keep screenshots for 7 days
        
        logger.info("Browser: {}", config.getBrowser());
        logger.info("Base URL: {}", config.getWebBaseUrl());
        logger.info("Headless mode: {}", config.isHeadless());
        logger.info("Screenshots on failure: {}", config.isScreenshotOnFailure());
    }
    
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {
        logger.info("=== Starting Test: {} ===", method.getName());
        
        try {
            // Initialize config if not already done
            if (config == null) {
                config = ConfigManager.getInstance();
            }
            
            // Clean up any existing browser processes
            WebDriverFactory.forceQuitAllBrowsers();
            
            // Create WebDriver instance
            driver = WebDriverFactory.createDriver();
            logger.info("WebDriver initialized successfully for test: {}", method.getName());
            
            // Dismiss any initial browser dialogs
            Thread.sleep(1000); // Brief pause to let browser load
            WebDriverFactory.dismissBrowserDialogs();
            
        } catch (Exception e) {
            logger.error("Failed to initialize WebDriver for test: {}", method.getName(), e);
            // Try to clean up on failure
            try {
                WebDriverFactory.quitDriver();
                WebDriverFactory.forceQuitAllBrowsers();
            } catch (Exception cleanupEx) {
                logger.error("Error during cleanup after initialization failure", cleanupEx);
            }
            throw new RuntimeException("WebDriver initialization failed", e);
        }
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                logger.error("Test FAILED: {}", testName);
                
                // Capture screenshot on failure if enabled
                if (config.isScreenshotOnFailure() && WebDriverFactory.isDriverInitialized()) {
                    String screenshotPath = ScreenshotUtils.captureScreenshot(testName + "_FAILED");
                    if (screenshotPath != null) {
                        logger.info("Failure screenshot saved: {}", screenshotPath);
                        
                        // Set screenshot path in test result for reporting
                        System.setProperty("screenshot.path", screenshotPath);
                    }
                }
                
                // Log failure reason
                if (result.getThrowable() != null) {
                    logger.error("Failure reason: {}", result.getThrowable().getMessage());
                }
                
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                logger.info("Test PASSED: {}", testName);
                
            } else if (result.getStatus() == ITestResult.SKIP) {
                logger.warn("Test SKIPPED: {}", testName);
                if (result.getThrowable() != null) {
                    logger.warn("Skip reason: {}", result.getThrowable().getMessage());
                }
            }
            
        } catch (Exception e) {
            logger.error("Error in afterMethod for test: {}", testName, e);
        } finally {
            // Always quit the driver
            try {
                WebDriverFactory.quitDriver();
                logger.info("WebDriver quit successfully for test: {}", testName);
            } catch (Exception e) {
                logger.error("Failed to quit WebDriver for test: {}", testName, e);
            }
        }
        
        logger.info("=== Finished Test: {} ===", testName);
    }
    
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        logger.info("=== Test Suite Execution Completed ===");
        
        // Final cleanup
        try {
            if (WebDriverFactory.isDriverInitialized()) {
                WebDriverFactory.quitDriver();
            }
        } catch (Exception e) {
            logger.error("Error during final cleanup", e);
        }
    }
    
    /**
     * Get the current WebDriver instance
     * 
     * @return WebDriver instance
     */
    protected WebDriver getDriver() {
        return driver;
    }
    
    /**
     * Get configuration manager instance
     * 
     * @return ConfigManager instance
     */
    protected ConfigManager getConfig() {
        return config;
    }
    
    /**
     * Take screenshot manually during test execution
     * 
     * @param testName Name for the screenshot
     * @return Path to the screenshot file
     */
    protected String takeScreenshot(String testName) {
        String screenshotPath = ScreenshotUtils.captureScreenshot(testName, driver);
        if (screenshotPath != null) {
            ExtentReportManager.addScreenshot(screenshotPath, "Manual Screenshot: " + testName);
        }
        return screenshotPath;
    }
    
    /**
     * Log test step information
     * 
     * @param stepDescription Description of the test step
     */
    protected void logTestStep(String stepDescription) {
        logger.info("TEST STEP: {}", stepDescription);
        ExtentReportManager.addTestStep(stepDescription);
    }
    
    /**
     * Log test assertion information
     * 
     * @param assertion Description of the assertion
     * @param result Result of the assertion
     */
    protected void logAssertion(String assertion, boolean result) {
        if (result) {
            logger.info("ASSERTION PASSED: {}", assertion);
        } else {
            logger.error("ASSERTION FAILED: {}", assertion);
        }
        ExtentReportManager.addAssertion(assertion, result);
    }
    
    /**
     * Add test information to logs
     * 
     * @param info Test information to log
     */
    protected void logTestInfo(String info) {
        logger.info("TEST INFO: {}", info);
        ExtentReportManager.logInfo(info);
    }
    
    /**
     * Wait for specified time (use sparingly, prefer explicit waits)
     * 
     * @param seconds Time to wait in seconds
     */
    protected void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted", e);
        }
    }
    
    /**
     * Handle stuck scenarios by dismissing browser dialogs and refreshing
     */
    protected void handleStuckScenario() {
        logger.warn("Handling potentially stuck scenario...");
        
        try {
            // First, try to dismiss any browser dialogs
            WebDriverFactory.dismissBrowserDialogs();
            Thread.sleep(2000);
            
            // Try to refresh the page
            if (driver != null) {
                driver.navigate().refresh();
                Thread.sleep(3000);
            }
            
            // Dismiss dialogs again after refresh
            WebDriverFactory.dismissBrowserDialogs();
            
            logger.info("Successfully handled stuck scenario");
            
        } catch (Exception e) {
            logger.error("Error while handling stuck scenario", e);
        }
    }
    
    /**
     * Emergency recovery method for completely stuck tests
     */
    protected void emergencyRecovery() {
        logger.error("Performing emergency recovery...");
        
        try {
            // Force quit current driver
            WebDriverFactory.quitDriver();
            
            // Kill all browser processes
            WebDriverFactory.forceQuitAllBrowsers();
            
            // Wait a bit
            Thread.sleep(3000);
            
            // Create new driver instance
            driver = WebDriverFactory.createDriver();
            Thread.sleep(2000);
            WebDriverFactory.dismissBrowserDialogs();
            
            logger.info("Emergency recovery completed");
            
        } catch (Exception e) {
            logger.error("Emergency recovery failed", e);
            throw new RuntimeException("Cannot recover from stuck scenario", e);
        }
    }
}
