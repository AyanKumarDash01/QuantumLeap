package com.quantumleap.framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.quantumleap.framework.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Extent Reports Manager for comprehensive test reporting
 * Manages report lifecycle and provides utilities for test logging
 * 
 * @author QuantumLeap Team
 */
public class ExtentReportManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ExtentReportManager.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static final ConfigManager config = ConfigManager.getInstance();
    
    /**
     * Initialize Extent Reports
     */
    public static synchronized void initReports() {
        if (extent == null) {
            String reportPath = getReportPath();
            
            // Create report directory if it doesn't exist
            File reportDir = new File(reportPath).getParentFile();
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            configureSparkReporter(sparkReporter);
            
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            setSystemInfo();
            
            logger.info("Extent Reports initialized: {}", reportPath);
        }
    }
    
    /**
     * Configure Spark Reporter settings
     * 
     * @param sparkReporter Spark reporter instance
     */
    private static void configureSparkReporter(ExtentSparkReporter sparkReporter) {
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("QuantumLeap E-Commerce Test Report");
        sparkReporter.config().setReportName(config.getExtentReportName());
        sparkReporter.config().setTimelineEnabled(true);
        sparkReporter.config().setEncoding("utf-8");
        
        // Custom CSS for better appearance
        sparkReporter.config().setCss(
            ".navbar-brand { color: #1f77b4 !important; }" +
            ".card-header { background-color: #f8f9fa !important; }" +
            ".test-content { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }"
        );
    }
    
    /**
     * Set system information for the report
     */
    private static void setSystemInfo() {
        extent.setSystemInfo("Application", "QuantumLeap E-Commerce");
        extent.setSystemInfo("Environment", "Test");
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Browser", config.getBrowser());
        extent.setSystemInfo("Headless Mode", String.valueOf(config.isHeadless()));
        extent.setSystemInfo("Base URL", config.getWebBaseUrl());
        extent.setSystemInfo("API URL", config.getApiBaseUrl());
        extent.setSystemInfo("Report Generated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
    
    /**
     * Get report file path with timestamp
     * 
     * @return Report file path
     */
    private static String getReportPath() {
        String baseReportPath = config.getExtentReportPath();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        
        // Insert timestamp before file extension
        String fileName = baseReportPath.substring(baseReportPath.lastIndexOf('/') + 1);
        String directory = baseReportPath.substring(0, baseReportPath.lastIndexOf('/'));
        String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        
        return directory + "/" + nameWithoutExt + "_" + timestamp + extension;
    }
    
    /**
     * Create a new test in the report
     * 
     * @param testName Name of the test
     * @param description Description of the test
     * @return ExtentTest instance
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest extentTest = extent.createTest(testName, description);
        test.set(extentTest);
        return extentTest;
    }
    
    /**
     * Create a new test with category
     * 
     * @param testName Name of the test
     * @param description Description of the test
     * @param category Test category
     * @return ExtentTest instance
     */
    public static ExtentTest createTest(String testName, String description, String category) {
        ExtentTest extentTest = extent.createTest(testName, description);
        extentTest.assignCategory(category);
        test.set(extentTest);
        return extentTest;
    }
    
    /**
     * Get current test instance
     * 
     * @return Current ExtentTest instance
     */
    public static ExtentTest getTest() {
        return test.get();
    }
    
    /**
     * Log info message to current test
     * 
     * @param message Message to log
     */
    public static void logInfo(String message) {
        if (test.get() != null) {
            test.get().log(Status.INFO, message);
        }
    }
    
    /**
     * Log pass message to current test
     * 
     * @param message Message to log
     */
    public static void logPass(String message) {
        if (test.get() != null) {
            test.get().log(Status.PASS, message);
        }
    }
    
    /**
     * Log fail message to current test
     * 
     * @param message Message to log
     */
    public static void logFail(String message) {
        if (test.get() != null) {
            test.get().log(Status.FAIL, message);
        }
    }
    
    /**
     * Log warning message to current test
     * 
     * @param message Message to log
     */
    public static void logWarning(String message) {
        if (test.get() != null) {
            test.get().log(Status.WARNING, message);
        }
    }
    
    /**
     * Log skip message to current test
     * 
     * @param message Message to log
     */
    public static void logSkip(String message) {
        if (test.get() != null) {
            test.get().log(Status.SKIP, message);
        }
    }
    
    /**
     * Add screenshot to current test
     * 
     * @param screenshotPath Path to screenshot file
     * @param description Screenshot description
     */
    public static void addScreenshot(String screenshotPath, String description) {
        if (test.get() != null && screenshotPath != null) {
            try {
                test.get().addScreenCaptureFromPath(screenshotPath, description);
                logInfo("Screenshot attached: " + description);
            } catch (Exception e) {
                logger.error("Failed to attach screenshot to report", e);
                logWarning("Failed to attach screenshot: " + e.getMessage());
            }
        }
    }
    
    /**
     * Add screenshot with failure message
     * 
     * @param screenshotPath Path to screenshot file
     * @param failureMessage Failure message
     */
    public static void addFailureScreenshot(String screenshotPath, String failureMessage) {
        if (test.get() != null) {
            logFail(failureMessage);
            if (screenshotPath != null) {
                addScreenshot(screenshotPath, "Failure Screenshot - " + failureMessage);
            }
        }
    }
    
    /**
     * Add test step to current test
     * 
     * @param stepDescription Step description
     */
    public static void addTestStep(String stepDescription) {
        logInfo("🔸 " + stepDescription);
    }
    
    /**
     * Add test assertion to current test
     * 
     * @param assertion Assertion description
     * @param result Assertion result
     */
    public static void addAssertion(String assertion, boolean result) {
        if (result) {
            logPass("✅ ASSERTION PASSED: " + assertion);
        } else {
            logFail("❌ ASSERTION FAILED: " + assertion);
        }
    }
    
    /**
     * Mark test as passed
     * 
     * @param message Success message
     */
    public static void markTestPassed(String message) {
        if (test.get() != null) {
            test.get().pass(message);
        }
    }
    
    /**
     * Mark test as failed
     * 
     * @param message Failure message
     * @param throwable Exception that caused the failure
     */
    public static void markTestFailed(String message, Throwable throwable) {
        if (test.get() != null) {
            test.get().fail(message);
            if (throwable != null) {
                test.get().fail(throwable);
            }
        }
    }
    
    /**
     * Mark test as skipped
     * 
     * @param message Skip message
     */
    public static void markTestSkipped(String message) {
        if (test.get() != null) {
            test.get().skip(message);
        }
    }
    
    /**
     * Add author information to current test
     * 
     * @param authorName Author name
     */
    public static void addAuthor(String authorName) {
        if (test.get() != null) {
            test.get().assignAuthor(authorName);
        }
    }
    
    /**
     * Add device information to current test
     * 
     * @param deviceName Device name
     */
    public static void addDevice(String deviceName) {
        if (test.get() != null) {
            test.get().assignDevice(deviceName);
        }
    }
    
    /**
     * Add category to current test
     * 
     * @param category Category name
     */
    public static void addCategory(String category) {
        if (test.get() != null) {
            test.get().assignCategory(category);
        }
    }
    
    /**
     * Create child test under current test
     * 
     * @param childTestName Child test name
     * @param description Child test description
     * @return Child ExtentTest instance
     */
    public static ExtentTest createChildTest(String childTestName, String description) {
        if (test.get() != null) {
            ExtentTest childTest = test.get().createNode(childTestName, description);
            return childTest;
        }
        return null;
    }
    
    /**
     * Flush and finalize the report
     */
    public static synchronized void flushReports() {
        if (extent != null) {
            extent.flush();
            logger.info("Extent Reports flushed successfully");
        }
    }
    
    /**
     * Clean up thread local test instance
     */
    public static void removeTest() {
        test.remove();
    }
    
    /**
     * Get report file path for external access
     * 
     * @return Current report file path
     */
    public static String getReportFilePath() {
        return getReportPath();
    }
}