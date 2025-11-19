package com.quantumleap.framework.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.quantumleap.framework.utils.ExtentReportManager;
import com.quantumleap.framework.utils.ScreenshotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * TestNG Listener for Extent Reports integration
 * Handles test lifecycle events and reporting
 * 
 * @author QuantumLeap Team
 */
public class ExtentTestListener implements ITestListener, ISuiteListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ExtentTestListener.class);
    
    @Override
    public void onStart(ISuite suite) {
        logger.info("Starting test suite: {}", suite.getName());
        ExtentReportManager.initReports();
        ExtentReportManager.logInfo("Test Suite Started: " + suite.getName());
    }
    
    @Override
    public void onFinish(ISuite suite) {
        logger.info("Finishing test suite: {}", suite.getName());
        ExtentReportManager.logInfo("Test Suite Completed: " + suite.getName());
        ExtentReportManager.flushReports();
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String testDescription = getTestDescription(result);
        String testCategory = getTestCategory(result);
        String testAuthor = getTestAuthor(result);
        
        logger.info("Starting test: {} - {}", testName, testDescription);
        
        ExtentTest extentTest = ExtentReportManager.createTest(testName, testDescription, testCategory);
        
        // Add test metadata
        if (testAuthor != null && !testAuthor.isEmpty()) {
            ExtentReportManager.addAuthor(testAuthor);
        }
        
        // Log test start
        ExtentReportManager.logInfo("🚀 Test Started: " + testName);
        ExtentReportManager.addTestStep("Initializing test: " + testName);
        
        // Log test parameters if any
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            ExtentReportManager.logInfo("Test Parameters: " + Arrays.toString(parameters));
        }
        
        // Log test groups
        String[] groups = result.getMethod().getGroups();
        if (groups != null && groups.length > 0) {
            ExtentReportManager.logInfo("Test Groups: " + Arrays.toString(groups));
            for (String group : groups) {
                ExtentReportManager.addCategory(group);
            }
        }
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.info("Test passed: {} (Duration: {}ms)", testName, duration);
        
        ExtentReportManager.addTestStep("Test execution completed successfully");
        ExtentReportManager.logPass("✅ Test Passed: " + testName);
        ExtentReportManager.logInfo("⏱️ Execution Time: " + duration + "ms");
        ExtentReportManager.markTestPassed("Test completed successfully in " + duration + "ms");
        
        // Clean up
        ExtentReportManager.removeTest();
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.error("Test failed: {} (Duration: {}ms)", testName, duration, throwable);
        
        // Log failure details
        ExtentReportManager.addTestStep("Test execution failed");
        ExtentReportManager.logFail("❌ Test Failed: " + testName);
        ExtentReportManager.logInfo("⏱️ Execution Time: " + duration + "ms");
        
        String failureMessage = throwable != null ? throwable.getMessage() : "Unknown error";
        ExtentReportManager.logFail("Error: " + failureMessage);
        
        // Capture screenshot for UI tests
        String screenshotPath = captureScreenshotOnFailure(testName);
        if (screenshotPath != null) {
            ExtentReportManager.addFailureScreenshot(screenshotPath, "Test Failure Screenshot");
        }
        
        // Mark test as failed
        ExtentReportManager.markTestFailed("Test failed: " + failureMessage, throwable);
        
        // Clean up
        ExtentReportManager.removeTest();
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        
        logger.info("Test skipped: {}", testName);
        
        String skipReason = throwable != null ? throwable.getMessage() : "Test skipped";
        
        ExtentReportManager.addTestStep("Test was skipped");
        ExtentReportManager.logSkip("⏭️ Test Skipped: " + testName);
        ExtentReportManager.logInfo("Skip Reason: " + skipReason);
        ExtentReportManager.markTestSkipped("Test skipped: " + skipReason);
        
        // Clean up
        ExtentReportManager.removeTest();
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.info("Test failed but within success percentage: {}", testName);
        
        ExtentReportManager.logWarning("⚠️ Test failed but within success percentage: " + testName);
        
        // Clean up
        ExtentReportManager.removeTest();
    }
    
    /**
     * Get test description from TestNG annotations or method name
     * 
     * @param result Test result
     * @return Test description
     */
    private String getTestDescription(ITestResult result) {
        String description = result.getMethod().getDescription();
        if (description == null || description.isEmpty()) {
            // Try to get from Test annotation
            Test testAnnotation = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class);
            if (testAnnotation != null && !testAnnotation.description().isEmpty()) {
                description = testAnnotation.description();
            } else {
                // Generate description from method name
                description = generateDescriptionFromMethodName(result.getMethod().getMethodName());
            }
        }
        return description;
    }
    
    /**
     * Get test category from TestNG groups or class name
     * 
     * @param result Test result
     * @return Test category
     */
    private String getTestCategory(ITestResult result) {
        String[] groups = result.getMethod().getGroups();
        if (groups != null && groups.length > 0) {
            return groups[0]; // Use first group as primary category
        }
        
        // Use class name as category
        String className = result.getTestClass().getName();
        String[] classParts = className.split("\\.");
        return classParts[classParts.length - 1]; // Get simple class name
    }
    
    /**
     * Get test author from system property or default
     * 
     * @param result Test result
     * @return Test author
     */
    private String getTestAuthor(ITestResult result) {
        // Check for author in system properties
        String author = System.getProperty("test.author");
        if (author == null || author.isEmpty()) {
            author = "QuantumLeap Team";
        }
        return author;
    }
    
    /**
     * Generate human-readable description from camelCase method name
     * 
     * @param methodName Method name
     * @return Human-readable description
     */
    private String generateDescriptionFromMethodName(String methodName) {
        // Convert camelCase to readable format
        String description = methodName.replaceAll("([a-z])([A-Z])", "$1 $2");
        
        // Capitalize first letter
        if (!description.isEmpty()) {
            description = Character.toUpperCase(description.charAt(0)) + description.substring(1);
        }
        
        return description;
    }
    
    /**
     * Capture screenshot on test failure
     * 
     * @param testName Test name for screenshot file
     * @return Screenshot file path or null if capture failed
     */
    private String captureScreenshotOnFailure(String testName) {
        try {
            // Only capture screenshot for UI tests (when WebDriver is available)
            return ScreenshotUtils.captureScreenshotOnFailure(testName);
        } catch (Exception e) {
            logger.warn("Failed to capture screenshot for test: {}", testName, e);
            return null;
        }
    }
}
