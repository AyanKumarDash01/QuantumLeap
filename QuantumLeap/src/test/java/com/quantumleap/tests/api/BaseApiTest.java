package com.quantumleap.tests.api;

import com.quantumleap.framework.config.ConfigManager;
import com.quantumleap.framework.utils.ExtentReportManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Base API Test class providing common setup and utilities for REST Assured tests
 * All API test classes should extend this base class
 * 
 * @author QuantumLeap Team
 */
public abstract class BaseApiTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiTest.class);
    protected ConfigManager config;
    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;
    
    @BeforeClass(alwaysRun = true)
    public void setUpApiTest() {
        logger.info("Setting up API test environment");
        config = ConfigManager.getInstance();
        
        // Configure RestAssured
        configureRestAssured();
        
        // Set up request specification
        setupRequestSpecification();
        
        // Set up response specification
        setupResponseSpecification();
        
        logger.info("API test environment configured successfully");
        logger.info("Base API URL: {}", config.getApiBaseUrl());
        logger.info("API Timeout: {} ms", config.getApiTimeout());
    }
    
    /**
     * Configure RestAssured global settings
     */
    private void configureRestAssured() {
        RestAssured.baseURI = config.getApiBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
        
        // Set default timeout
        RestAssured.config = RestAssured.config()
            .httpClient(RestAssured.config().getHttpClientConfig()
                .setParam("http.connection.timeout", config.getApiTimeout())
                .setParam("http.socket.timeout", config.getApiTimeout()));
    }
    
    /**
     * Set up default request specification
     */
    private void setupRequestSpecification() {
        requestSpec = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addHeader("User-Agent", "QuantumLeap-E-Commerce-Test-Automation/1.0")
            .log(LogDetail.ALL)
            .build();
        
        logger.debug("Request specification configured");
    }
    
    /**
     * Set up default response specification
     */
    private void setupResponseSpecification() {
        responseSpec = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();
        
        logger.debug("Response specification configured");
    }
    
    /**
     * Create a request with default specifications
     * 
     * @return RequestSpecification with default settings
     */
    protected RequestSpecification givenRequest() {
        return RestAssured.given()
            .spec(requestSpec);
    }
    
    /**
     * Create a request with custom content type
     * 
     * @param contentType Content type for the request
     * @return RequestSpecification with custom content type
     */
    protected RequestSpecification givenRequestWithContentType(ContentType contentType) {
        return RestAssured.given()
            .spec(requestSpec)
            .contentType(contentType);
    }
    
    /**
     * Log API test step
     * 
     * @param step Description of the API test step
     */
    protected void logApiStep(String step) {
        logger.info("API TEST STEP: {}", step);
        ExtentReportManager.addTestStep(step);
    }
    
    /**
     * Log API assertion
     * 
     * @param assertion Description of the assertion
     * @param result Result of the assertion
     */
    protected void logApiAssertion(String assertion, boolean result) {
        if (result) {
            logger.info("API ASSERTION PASSED: {}", assertion);
        } else {
            logger.error("API ASSERTION FAILED: {}", assertion);
        }
        ExtentReportManager.addAssertion(assertion, result);
    }
    
    /**
     * Validate response time
     * 
     * @param response Response to validate
     * @param maxTimeInMs Maximum acceptable response time in milliseconds
     */
    protected void validateResponseTime(Response response, long maxTimeInMs) {
        long responseTime = response.getTime();
        logApiStep(String.format("Validating response time: %d ms (max: %d ms)", responseTime, maxTimeInMs));
        
        if (responseTime <= maxTimeInMs) {
            logApiAssertion(String.format("Response time %d ms is within acceptable limit", responseTime), true);
        } else {
            logApiAssertion(String.format("Response time %d ms exceeds limit of %d ms", responseTime, maxTimeInMs), false);
            throw new AssertionError(String.format("Response time %d ms exceeds maximum allowed %d ms", 
                responseTime, maxTimeInMs));
        }
    }
    
    /**
     * Validate standard response headers
     * 
     * @param response Response to validate
     */
    protected void validateStandardHeaders(Response response) {
        logApiStep("Validating standard response headers");
        
        // Validate Content-Type
        String contentType = response.getHeader("Content-Type");
        if (contentType != null && contentType.contains("application/json")) {
            logApiAssertion("Content-Type header is valid", true);
        }
        
        // Log other important headers
        logger.info("Response headers - Content-Type: {}, Status: {}", 
            contentType, response.getStatusCode());
    }
    
    /**
     * Print formatted response for debugging
     * 
     * @param response Response to print
     * @param description Description of the response
     */
    protected void printResponse(Response response, String description) {
        logger.info("=== {} ===", description);
        logger.info("Status Code: {}", response.getStatusCode());
        logger.info("Response Time: {} ms", response.getTime());
        logger.info("Response Body: {}", response.asPrettyString());
        logger.info("===============================");
        
        // Log to Extent Report
        ExtentReportManager.logInfo(String.format("=== %s ===", description));
        ExtentReportManager.logInfo("Status Code: " + response.getStatusCode());
        ExtentReportManager.logInfo("Response Time: " + response.getTime() + " ms");
        ExtentReportManager.logInfo("Response Body: " + response.asPrettyString());
    }
    
    /**
     * Validate JSON schema if available
     * 
     * @param response Response to validate
     * @param schemaPath Path to JSON schema file
     */
    protected void validateJsonSchema(Response response, String schemaPath) {
        try {
            if (config.getBooleanProperty("json.schema.validation")) {
                logApiStep("Validating JSON schema: " + schemaPath);
                response.then().assertThat().body(io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));
                logApiAssertion("JSON schema validation passed", true);
            }
        } catch (Exception e) {
            logApiAssertion("JSON schema validation failed", false);
            logger.warn("JSON schema validation failed for: {}", schemaPath, e);
        }
    }
    
    /**
     * Create test data map for API requests
     * 
     * @param keyValuePairs Key-value pairs for the map
     * @return Map containing the test data
     */
    protected Map<String, Object> createTestData(Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Key-value pairs must be even number of arguments");
        }
        
        Map<String, Object> data = new java.util.HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            data.put(keyValuePairs[i].toString(), keyValuePairs[i + 1]);
        }
        
        return data;
    }
    
    /**
     * Wait for specified duration (use sparingly)
     * 
     * @param duration Duration to wait
     * @param timeUnit Time unit for the duration
     */
    protected void waitFor(long duration, TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(duration));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted", e);
        }
    }
}