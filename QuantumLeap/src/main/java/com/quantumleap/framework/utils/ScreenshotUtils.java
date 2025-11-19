package com.quantumleap.framework.utils;

import com.quantumleap.framework.base.WebDriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for handling screenshot operations
 * Provides methods to capture and save screenshots during test execution
 * 
 * @author QuantumLeap Team
 */
public class ScreenshotUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "screenshots";
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    
    static {
        // Create screenshots directory if it doesn't exist
        createScreenshotDirectory();
    }
    
    /**
     * Create screenshots directory if it doesn't exist
     */
    private static void createScreenshotDirectory() {
        try {
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
                logger.info("Created screenshots directory: {}", screenshotDir.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.error("Failed to create screenshots directory", e);
        }
    }
    
    /**
     * Capture screenshot and save to file
     * 
     * @param testName Name of the test for screenshot filename
     * @return Path to the saved screenshot file
     */
    public static String captureScreenshot(String testName) {
        return captureScreenshot(testName, WebDriverFactory.getDriver());
    }
    
    /**
     * Capture screenshot with specific WebDriver instance
     * 
     * @param testName Name of the test for screenshot filename
     * @param driver WebDriver instance to capture screenshot from
     * @return Path to the saved screenshot file
     */
    public static String captureScreenshot(String testName, WebDriver driver) {
        try {
            if (driver == null) {
                logger.warn("WebDriver is null, cannot capture screenshot");
                return null;
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            String fileName = String.format("%s_%s.png", testName, timestamp);
            File destFile = new File(SCREENSHOT_DIR, fileName);
            
            java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            String screenshotPath = destFile.getAbsolutePath();
            logger.info("Screenshot captured: {}", screenshotPath);
            
            return screenshotPath;
            
        } catch (IOException e) {
            logger.error("Failed to capture screenshot for test: {}", testName, e);
            return null;
        }
    }
    
    /**
     * Capture screenshot as base64 string for embedding in reports
     * 
     * @param driver WebDriver instance
     * @return Base64 encoded screenshot string
     */
    public static String captureScreenshotAsBase64(WebDriver driver) {
        try {
            if (driver == null) {
                logger.warn("WebDriver is null, cannot capture screenshot");
                return null;
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BASE64);
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as base64", e);
            return null;
        }
    }
    
    /**
     * Capture screenshot and return as byte array
     * 
     * @param driver WebDriver instance
     * @return Screenshot as byte array
     */
    public static byte[] captureScreenshotAsBytes(WebDriver driver) {
        try {
            if (driver == null) {
                logger.warn("WebDriver is null, cannot capture screenshot");
                return null;
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BYTES);
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as bytes", e);
            return null;
        }
    }
    
    /**
     * Clean up old screenshots (older than specified days)
     * 
     * @param daysToKeep Number of days to keep screenshots
     */
    public static void cleanupOldScreenshots(int daysToKeep) {
        try {
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                return;
            }
            
            long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60L * 60L * 1000L);
            File[] files = screenshotDir.listFiles();
            
            if (files != null) {
                int deletedCount = 0;
                for (File file : files) {
                    if (file.isFile() && file.lastModified() < cutoffTime) {
                        if (file.delete()) {
                            deletedCount++;
                        }
                    }
                }
                
                if (deletedCount > 0) {
                    logger.info("Cleaned up {} old screenshots", deletedCount);
                }
            }
            
        } catch (Exception e) {
            logger.error("Failed to cleanup old screenshots", e);
        }
    }
    
    /**
     * Capture screenshot on test failure
     * 
     * @param testName Name of the failed test
     * @return Path to the saved screenshot file
     */
    public static String captureScreenshotOnFailure(String testName) {
        try {
            String failureTestName = testName + "_FAILED";
            return captureScreenshot(failureTestName);
        } catch (Exception e) {
            logger.error("Failed to capture failure screenshot for test: {}", testName, e);
            return null;
        }
    }
    
    /**
     * Get screenshots directory path
     * 
     * @return Screenshots directory path
     */
    public static String getScreenshotDirectory() {
        return new File(SCREENSHOT_DIR).getAbsolutePath();
    }
}
