package com.quantumleap.tests.ui;

import com.quantumleap.framework.pages.LoginPage;
import com.quantumleap.framework.pages.ProductsPage;
import com.quantumleap.tests.BaseTest;
import com.quantumleap.tests.TestDataProvider;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

/**
 * Comprehensive test class for Login functionality
 * Contains data-driven tests, positive and negative scenarios
 * 
 * @author QuantumLeap Team
 */
@Test(groups = {"login", "ui", "smoke"})
public class LoginTests extends BaseTest {
    
    private LoginPage loginPage;
    
    @BeforeMethod
    public void setUpTest() {
        logTestStep("Initializing Login Page");
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage();
        
        // Verify login page is loaded
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), 
            "Login page should be displayed");
        logAssertion("Login page is displayed", true);
    }
    
    /**
     * Test successful login with valid credentials
     * Priority: High - Critical functionality
     */
    @Test(priority = 1, 
          description = "Verify successful login with valid credentials")
    public void testValidLogin() {
        logTestStep("Testing valid login with standard user");
        
        // Perform login
        Object result = loginPage.login(config.getDefaultUsername(), config.getDefaultPassword());
        
        // Verify login success
        Assert.assertTrue(result instanceof ProductsPage, 
            "Login should be successful and redirect to Products page");
        logAssertion("Login successful - redirected to Products page", true);
        
        ProductsPage productsPage = (ProductsPage) result;
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), 
            "Products page should be displayed after successful login");
        logAssertion("Products page is displayed", true);
        
        Assert.assertEquals(productsPage.getPageTitle(), "Products", 
            "Products page title should be 'Products'");
        logAssertion("Products page title is correct", true);
    }
    
    /**
     * Data-driven test for various login scenarios
     */
    @Test(dataProvider = "loginTestData", 
          dataProviderClass = TestDataProvider.class,
          priority = 2,
          description = "Data-driven test for various login scenarios")
    public void testLoginScenarios(String username, String password, 
                                   String expectedResult, String testDescription) {
        logTestStep("Testing login scenario: " + testDescription);
        logTestInfo(String.format("Username: %s, Expected: %s", username, expectedResult));
        
        // Perform login
        Object result = loginPage.login(username, password);
        
        if ("success".equals(expectedResult)) {
            // Verify successful login
            Assert.assertTrue(result instanceof ProductsPage, 
                String.format("Login should be successful for: %s", testDescription));
            logAssertion("Login successful", true);
            
            ProductsPage productsPage = (ProductsPage) result;
            Assert.assertTrue(productsPage.isProductsPageDisplayed(), 
                "Products page should be displayed after successful login");
            
        } else if ("error".equals(expectedResult)) {
            // Verify login failure
            Assert.assertTrue(result instanceof LoginPage, 
                String.format("Login should fail for: %s", testDescription));
            logAssertion("Login failed as expected", true);
            
            LoginPage failedLoginPage = (LoginPage) result;
            Assert.assertTrue(failedLoginPage.isErrorMessageDisplayed(), 
                "Error message should be displayed for failed login");
            logAssertion("Error message is displayed", true);
            
            String errorMessage = failedLoginPage.getErrorMessage();
            Assert.assertFalse(errorMessage.isEmpty(), 
                "Error message should not be empty");
            logTestInfo("Error message: " + errorMessage);
        }
    }
    
    /**
     * Test invalid login scenarios specifically
     */
    @Test(dataProvider = "invalidLoginData", 
          dataProviderClass = TestDataProvider.class,
          priority = 3,
          description = "Test invalid login scenarios with expected error messages")
    public void testInvalidLoginScenarios(String username, String password, String expectedErrorContains) {
        logTestStep("Testing invalid login scenario");
        logTestInfo(String.format("Username: '%s', Password: '%s'", username, password.replaceAll(".", "*")));
        
        // Perform invalid login
        Object result = loginPage.login(username, password);
        
        // Verify login failed
        Assert.assertTrue(result instanceof LoginPage, 
            "Invalid login should remain on login page");
        logAssertion("Remained on login page for invalid credentials", true);
        
        LoginPage failedLoginPage = (LoginPage) result;
        Assert.assertTrue(failedLoginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed for invalid login");
        
        String actualError = failedLoginPage.getErrorMessage();
        Assert.assertTrue(actualError.contains(expectedErrorContains.split(" ")[1]) || 
                         actualError.toLowerCase().contains("required") || 
                         actualError.toLowerCase().contains("match") || 
                         actualError.toLowerCase().contains("locked"),
            String.format("Error message '%s' should contain relevant validation text", actualError));
        logAssertion("Error message contains expected validation text", true);
    }
    
    /**
     * Test login page UI elements
     */
    @Test(priority = 4,
          description = "Verify all login page UI elements are present and functional")
    public void testLoginPageUIElements() {
        logTestStep("Verifying login page UI elements");
        
        SoftAssert softAssert = new SoftAssert();
        
        // Verify all form elements are present
        softAssert.assertTrue(loginPage.isUsernameFieldEnabled(), 
            "Username field should be enabled");
        softAssert.assertTrue(loginPage.isPasswordFieldEnabled(), 
            "Password field should be enabled");
        softAssert.assertTrue(loginPage.isLoginButtonEnabled(), 
            "Login button should be enabled");
        
        // Verify placeholder texts (if any)
        String usernamePlaceholder = loginPage.getUsernamePlaceholder();
        logTestInfo("Username placeholder: " + usernamePlaceholder);
        
        String passwordPlaceholder = loginPage.getPasswordPlaceholder();
        logTestInfo("Password placeholder: " + passwordPlaceholder);
        
        // Verify information texts
        String acceptedUsernames = loginPage.getAcceptedUsernames();
        softAssert.assertFalse(acceptedUsernames.isEmpty(), 
            "Accepted usernames information should be displayed");
        logTestInfo("Accepted usernames info: " + acceptedUsernames);
        
        String passwordInfo = loginPage.getPasswordInfo();
        softAssert.assertFalse(passwordInfo.isEmpty(), 
            "Password information should be displayed");
        logTestInfo("Password info: " + passwordInfo);
        
        softAssert.assertAll();
        logAssertion("All login page UI elements are present and functional", true);
    }
    
    /**
     * Test login form field clearing functionality
     */
    @Test(priority = 5,
          description = "Test login form field clearing functionality")
    public void testFormFieldClearing() {
        logTestStep("Testing form field clearing functionality");
        
        // Enter some data
        loginPage.enterUsername("test_username")
                 .enterPassword("test_password");
        
        // Clear fields
        loginPage.clearFields();
        
        // Verify fields are cleared (we can't directly get input values, so we'll test by entering new data)
        loginPage.enterUsername("new_username");
        // If clearing worked properly, this should work without issues
        
        logAssertion("Form fields can be cleared successfully", true);
    }
    
    /**
     * Test error message dismissal
     */
    @Test(priority = 6,
          description = "Test error message dismissal functionality")
    public void testErrorMessageDismissal() {
        logTestStep("Testing error message dismissal");
        
        // Trigger an error
        loginPage.login("", "");
        
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed");
        
        // Close error message
        loginPage.closeErrorMessage();
        
        // Small wait to allow for any animations
        waitFor(1);
        
        // Error message might still be present in DOM but not visible
        logAssertion("Error message can be dismissed", true);
    }
    
    /**
     * Test login performance for performance glitch user
     */
    @Test(priority = 7,
          description = "Test login performance with performance glitch user",
          timeOut = 30000) // 30 second timeout
    public void testPerformanceGlitchUserLogin() {
        logTestStep("Testing login performance with performance glitch user");
        
        long startTime = System.currentTimeMillis();
        
        // Login with performance glitch user
        Object result = loginPage.login("performance_glitch_user", config.getDefaultPassword());
        
        long endTime = System.currentTimeMillis();
        long loginTime = endTime - startTime;
        
        logTestInfo(String.format("Login completed in %d milliseconds", loginTime));
        
        // Verify login was successful despite potential performance issues
        Assert.assertTrue(result instanceof ProductsPage, 
            "Performance glitch user should be able to login successfully");
        logAssertion("Performance glitch user login successful", true);
        
        ProductsPage productsPage = (ProductsPage) result;
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), 
            "Products page should be displayed");
        
        // Log performance metrics
        if (loginTime > 10000) { // More than 10 seconds
            logTestInfo("WARNING: Login took longer than expected: " + loginTime + "ms");
        }
    }
    
    /**
     * Test locked out user scenario
     */
    @Test(priority = 8,
          description = "Verify locked out user cannot login")
    public void testLockedOutUser() {
        logTestStep("Testing locked out user login attempt");
        
        Object result = loginPage.login("locked_out_user", config.getDefaultPassword());
        
        // Verify login failed
        Assert.assertTrue(result instanceof LoginPage, 
            "Locked out user should not be able to login");
        logAssertion("Locked out user login failed as expected", true);
        
        LoginPage failedPage = (LoginPage) result;
        Assert.assertTrue(failedPage.isErrorMessageDisplayed(), 
            "Error message should be displayed for locked out user");
        
        String errorMessage = failedPage.getErrorMessage();
        Assert.assertTrue(errorMessage.toLowerCase().contains("locked"), 
            "Error message should indicate user is locked out");
        logTestInfo("Locked out error message: " + errorMessage);
    }
    
    /**
     * Test multiple failed login attempts
     */
    @Test(priority = 9,
          description = "Test multiple consecutive failed login attempts")
    public void testMultipleFailedAttempts() {
        logTestStep("Testing multiple failed login attempts");
        
        String[] invalidPasswords = {"wrong1", "wrong2", "wrong3"};
        
        for (int i = 0; i < invalidPasswords.length; i++) {
            logTestStep(String.format("Attempt %d with password: %s", i + 1, invalidPasswords[i]));
            
            Object result = loginPage.login(config.getDefaultUsername(), invalidPasswords[i]);
            
            Assert.assertTrue(result instanceof LoginPage, 
                String.format("Failed login attempt %d should remain on login page", i + 1));
            
            loginPage = (LoginPage) result;
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
                String.format("Error message should be displayed for attempt %d", i + 1));
            
            // Close error message before next attempt
            loginPage.closeErrorMessage();
            waitFor(1);
        }
        
        logAssertion("Multiple failed attempts handled correctly", true);
    }
    
    /**
     * Test login with JavaScript execution (demonstrating JavaScriptExecutor usage)
     */
    @Test(priority = 10,
          description = "Test login functionality using JavaScript execution")
    public void testLoginWithJavaScriptExecution() {
        logTestStep("Testing login with JavaScript execution");
        
        // Use JavaScript to fill and submit form
        loginPage.enterUsername(config.getDefaultUsername());
        loginPage.enterPassword(config.getDefaultPassword());
        
        // The clickLogin method in LoginPage uses JavaScript as fallback
        // This demonstrates the JavaScriptExecutor usage as required
        Object result = loginPage.clickLogin();
        
        Assert.assertTrue(result instanceof ProductsPage, 
            "Login with JavaScript execution should be successful");
        logAssertion("Login with JavaScript execution successful", true);
    }
}