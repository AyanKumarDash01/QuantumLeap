package com.quantumleap.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager to handle application configuration properties
 * Implements Singleton pattern for global access to configuration
 * 
 * @author QuantumLeap Team
 */
public class ConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private Properties properties;
    
    private static final String CONFIG_FILE_PATH = "config/config.properties";
    
    private ConfigManager() {
        loadProperties();
    }
    
    /**
     * Get singleton instance of ConfigManager
     * 
     * @return ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Load properties from configuration file
     */
    private void loadProperties() {
        properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(CONFIG_FILE_PATH)) {
            
            if (inputStream == null) {
                throw new RuntimeException("Configuration file not found: " + CONFIG_FILE_PATH);
            }
            
            properties.load(inputStream);
            logger.info("Configuration properties loaded successfully");
            
        } catch (IOException e) {
            logger.error("Error loading configuration properties", e);
            throw new RuntimeException("Failed to load configuration properties", e);
        }
    }
    
    /**
     * Get property value as String
     * 
     * @param key Property key
     * @return Property value
     */
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property not found: {}", key);
        }
        return value;
    }
    
    /**
     * Get property value as String with default value
     * 
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get property value as Integer
     * 
     * @param key Property key
     * @return Property value as Integer
     */
    public int getIntProperty(String key) {
        String value = getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.error("Invalid integer property: {} = {}", key, value);
            throw new RuntimeException("Invalid integer property: " + key, e);
        }
    }
    
    /**
     * Get property value as Boolean
     * 
     * @param key Property key
     * @return Property value as Boolean
     */
    public boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return Boolean.parseBoolean(value);
    }
    
    // Specific configuration getters
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    public boolean isHeadless() {
        return getBooleanProperty("headless");
    }
    
    public int getImplicitWait() {
        return getIntProperty("implicit.wait");
    }
    
    public int getExplicitWait() {
        return getIntProperty("explicit.wait");
    }
    
    public int getPageLoadTimeout() {
        return getIntProperty("page.load.timeout");
    }
    
    public String getWebBaseUrl() {
        return getProperty("web.base.url");
    }
    
    public String getApiBaseUrl() {
        return getProperty("api.base.url");
    }
    
    public String getDefaultUsername() {
        return getProperty("default.username");
    }
    
    public String getDefaultPassword() {
        return getProperty("default.password");
    }
    
    public String getLockedUsername() {
        return getProperty("locked.username");
    }
    
    public String getPerformanceUsername() {
        return getProperty("performance.username");
    }
    
    public boolean isScreenshotOnFailure() {
        return getBooleanProperty("screenshot.on.failure");
    }
    
    public String getExtentReportPath() {
        return getProperty("extent.report.path");
    }
    
    public String getExtentReportName() {
        return getProperty("extent.report.name");
    }
    
    public int getApiTimeout() {
        return getIntProperty("api.timeout");
    }
}