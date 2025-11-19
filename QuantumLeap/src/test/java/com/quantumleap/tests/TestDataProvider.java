package com.quantumleap.tests;

import com.quantumleap.framework.config.ConfigManager;
import org.testng.annotations.DataProvider;

/**
 * Test Data Provider class for supplying test data to data-driven tests
 * Contains various data providers for different test scenarios
 * 
 * @author QuantumLeap Team
 */
public class TestDataProvider {
    
    private static final ConfigManager config = ConfigManager.getInstance();
    
    /**
     * Data provider for login functionality tests
     * Provides different user credentials for testing various login scenarios
     * 
     * @return Object array containing test data for login tests
     */
    @DataProvider(name = "loginTestData")
    public static Object[][] getLoginTestData() {
        return new Object[][] {
            // Format: {username, password, expectedResult, testDescription}
            {config.getDefaultUsername(), config.getDefaultPassword(), "success", "Valid standard user login"},
            {"locked_out_user", config.getDefaultPassword(), "error", "Locked out user login attempt"},
            {"problem_user", config.getDefaultPassword(), "success", "Problem user login"},
            {"performance_glitch_user", config.getDefaultPassword(), "success", "Performance glitch user login"},
            {"", config.getDefaultPassword(), "error", "Empty username login attempt"},
            {config.getDefaultUsername(), "", "error", "Empty password login attempt"},
            {"", "", "error", "Empty credentials login attempt"},
            {"invalid_user", config.getDefaultPassword(), "error", "Invalid username login attempt"},
            {config.getDefaultUsername(), "invalid_password", "error", "Invalid password login attempt"},
            {"invalid_user", "invalid_password", "error", "Invalid credentials login attempt"}
        };
    }
    
    /**
     * Data provider for valid login scenarios only
     * Used for tests that require successful login as a prerequisite
     * 
     * @return Object array containing valid login credentials
     */
    @DataProvider(name = "validLoginData")
    public static Object[][] getValidLoginData() {
        return new Object[][] {
            {config.getDefaultUsername(), config.getDefaultPassword(), "Standard User"},
            {"problem_user", config.getDefaultPassword(), "Problem User"},
            {"performance_glitch_user", config.getDefaultPassword(), "Performance Glitch User"}
        };
    }
    
    /**
     * Data provider for invalid login scenarios
     * Used for negative testing of login functionality
     * 
     * @return Object array containing invalid login credentials
     */
    @DataProvider(name = "invalidLoginData")
    public static Object[][] getInvalidLoginData() {
        return new Object[][] {
            {"", config.getDefaultPassword(), "Username is required"},
            {config.getDefaultUsername(), "", "Password is required"},
            {"", "", "Username is required"},
            {"invalid_user", config.getDefaultPassword(), "Username and password do not match"},
            {config.getDefaultUsername(), "wrong_password", "Username and password do not match"},
            {"locked_out_user", config.getDefaultPassword(), "Sorry, this user has been locked out"}
        };
    }
    
    /**
     * Data provider for product names
     * Provides common product names available on Sauce Demo
     * 
     * @return Object array containing product names
     */
    @DataProvider(name = "productNames")
    public static Object[][] getProductNames() {
        return new Object[][] {
            {"Sauce Labs Backpack"},
            {"Sauce Labs Bike Light"},
            {"Sauce Labs Bolt T-Shirt"},
            {"Sauce Labs Fleece Jacket"},
            {"Sauce Labs Onesie"},
            {"Test.allTheThings() T-Shirt (Red)"}
        };
    }
    
    /**
     * Data provider for multiple products for cart testing
     * 
     * @return Object array containing arrays of product names
     */
    @DataProvider(name = "multipleProducts")
    public static Object[][] getMultipleProducts() {
        return new Object[][] {
            {new String[]{"Sauce Labs Backpack", "Sauce Labs Bike Light"}},
            {new String[]{"Sauce Labs Bolt T-Shirt", "Sauce Labs Fleece Jacket", "Sauce Labs Onesie"}},
            {new String[]{"Test.allTheThings() T-Shirt (Red)", "Sauce Labs Backpack"}}
        };
    }
    
    /**
     * Data provider for sorting options
     * 
     * @return Object array containing sort options
     */
    @DataProvider(name = "sortOptions")
    public static Object[][] getSortOptions() {
        return new Object[][] {
            {"Name (A to Z)"},
            {"Name (Z to A)"},
            {"Price (low to high)"},
            {"Price (high to low)"}
        };
    }
    
    /**
     * Data provider for checkout information
     * 
     * @return Object array containing checkout details
     */
    @DataProvider(name = "checkoutData")
    public static Object[][] getCheckoutData() {
        return new Object[][] {
            {"John", "Doe", "12345", "Valid checkout information"},
            {"Jane", "Smith", "54321", "Valid checkout information 2"},
            {"Bob", "Johnson", "98765", "Valid checkout information 3"}
        };
    }
    
    /**
     * Data provider for invalid checkout information
     * 
     * @return Object array containing invalid checkout details
     */
    @DataProvider(name = "invalidCheckoutData")
    public static Object[][] getInvalidCheckoutData() {
        return new Object[][] {
            {"", "Doe", "12345", "Error: First Name is required"},
            {"John", "", "12345", "Error: Last Name is required"},
            {"John", "Doe", "", "Error: Postal Code is required"},
            {"", "", "", "Error: First Name is required"},
            {"", "", "12345", "Error: First Name is required"}
        };
    }
    
    /**
     * Data provider for browser and environment combinations
     * Used for cross-browser testing scenarios
     * 
     * @return Object array containing browser configurations
     */
    @DataProvider(name = "browserData")
    public static Object[][] getBrowserData() {
        return new Object[][] {
            {"chrome"},
            {"firefox"},
            {"edge"}
        };
    }
    
    /**
     * Data provider for performance testing users
     * Users that might have different performance characteristics
     * 
     * @return Object array containing performance test user data
     */
    @DataProvider(name = "performanceUsers")
    public static Object[][] getPerformanceUsers() {
        return new Object[][] {
            {config.getDefaultUsername(), config.getDefaultPassword(), "standard_user"},
            {"performance_glitch_user", config.getDefaultPassword(), "performance_glitch_user"}
        };
    }
    
    /**
     * Data provider for end-to-end test scenarios
     * Complete workflow test data
     * 
     * @return Object array containing complete test scenarios
     */
    @DataProvider(name = "endToEndScenarios")
    public static Object[][] getEndToEndScenarios() {
        return new Object[][] {
            {
                config.getDefaultUsername(), 
                config.getDefaultPassword(), 
                new String[]{"Sauce Labs Backpack"}, 
                "John", "Doe", "12345",
                "Single product purchase"
            },
            {
                config.getDefaultUsername(), 
                config.getDefaultPassword(), 
                new String[]{"Sauce Labs Backpack", "Sauce Labs Bike Light"}, 
                "Jane", "Smith", "54321",
                "Multiple products purchase"
            },
            {
                "problem_user", 
                config.getDefaultPassword(), 
                new String[]{"Sauce Labs Bolt T-Shirt"}, 
                "Bob", "Johnson", "98765",
                "Problem user purchase workflow"
            }
        };
    }
}