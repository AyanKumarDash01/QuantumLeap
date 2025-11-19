package com.quantumleap.framework.pages;

import com.quantumleap.framework.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoginPage class representing the Sauce Demo login page
 * Contains all locators and methods for login functionality
 * 
 * @author QuantumLeap Team
 */
public class LoginPage extends BasePage {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);
    
    // Page Elements using Page Factory
    @FindBy(id = "user-name")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "login-button")
    private WebElement loginButton;
    
    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;
    
    @FindBy(className = "error-button")
    private WebElement errorButton;
    
    @FindBy(css = ".login_logo")
    private WebElement loginLogo;
    
    @FindBy(css = ".login_credentials")
    private WebElement acceptedUsernames;
    
    @FindBy(css = ".login_password")
    private WebElement passwordForAllUsers;
    
    @FindBy(css = ".bot_column")
    private WebElement loginContainer;
    
    // Constructor
    public LoginPage(WebDriver driver) {
        super(driver);
        logger.info("LoginPage initialized");
    }
    
    /**
     * Navigate to the login page
     * 
     * @return LoginPage instance for method chaining
     */
    public LoginPage navigateToLoginPage() {
        String loginUrl = config.getWebBaseUrl();
        navigateTo(loginUrl);
        waitForPageLoad();
        logger.info("Navigated to login page: {}", loginUrl);
        return this;
    }
    
    /**
     * Enter username in the username field
     * 
     * @param username Username to enter
     * @return LoginPage instance for method chaining
     */
    public LoginPage enterUsername(String username) {
        waitForElementToBeVisible(usernameField);
        enterText(usernameField, username);
        logger.info("Entered username: {}", username);
        return this;
    }
    
    /**
     * Enter password in the password field
     * 
     * @param password Password to enter
     * @return LoginPage instance for method chaining
     */
    public LoginPage enterPassword(String password) {
        waitForElementToBeVisible(passwordField);
        enterText(passwordField, password);
        logger.info("Entered password");
        return this;
    }
    
    /**
     * Click the login button
     * 
     * @return LoginPage or ProductsPage depending on login success
     */
    public Object clickLogin() {
        waitForElementToBeClickable(loginButton);
        clickElement(loginButton);
        logger.info("Clicked login button");
        
        // Wait a moment for the page to process
        waitFor(2);
        
        // Check if login was successful by checking for error message
        if (isErrorMessageDisplayed()) {
            logger.warn("Login failed - error message displayed");
            return this; // Return LoginPage if login failed
        } else {
            logger.info("Login successful - navigating to products page");
            return new ProductsPage(driver); // Return ProductsPage if login successful
        }
    }
    
    /**
     * Perform complete login with username and password
     * 
     * @param username Username
     * @param password Password
     * @return ProductsPage if successful, LoginPage if failed
     */
    public Object login(String username, String password) {
        logger.info("Attempting to login with username: {}", username);
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }
    
    /**
     * Get error message text
     * 
     * @return Error message text, empty string if no error
     */
    public String getErrorMessage() {
        try {
            if (isElementDisplayed(errorMessage)) {
                String error = getText(errorMessage);
                logger.info("Error message retrieved: {}", error);
                return error;
            }
        } catch (Exception e) {
            logger.debug("No error message found");
        }
        return "";
    }
    
    /**
     * Check if error message is displayed
     * 
     * @return true if error message is visible, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        try {
            return isElementDisplayed(errorMessage);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Close error message by clicking the error button
     * 
     * @return LoginPage instance for method chaining
     */
    public LoginPage closeErrorMessage() {
        if (isElementDisplayed(errorButton)) {
            clickElement(errorButton);
            logger.info("Closed error message");
        }
        return this;
    }
    
    /**
     * Clear username field
     * 
     * @return LoginPage instance for method chaining
     */
    public LoginPage clearUsername() {
        waitForElementToBeVisible(usernameField);
        usernameField.clear();
        logger.info("Cleared username field");
        return this;
    }
    
    /**
     * Clear password field
     * 
     * @return LoginPage instance for method chaining
     */
    public LoginPage clearPassword() {
        waitForElementToBeVisible(passwordField);
        passwordField.clear();
        logger.info("Cleared password field");
        return this;
    }
    
    /**
     * Clear both username and password fields
     * 
     * @return LoginPage instance for method chaining
     */
    public LoginPage clearFields() {
        clearUsername();
        clearPassword();
        logger.info("Cleared both username and password fields");
        return this;
    }
    
    /**
     * Check if login page is displayed
     * 
     * @return true if login page elements are visible
     */
    public boolean isLoginPageDisplayed() {
        try {
            return isElementDisplayed(loginLogo) && 
                   isElementDisplayed(usernameField) && 
                   isElementDisplayed(passwordField) && 
                   isElementDisplayed(loginButton);
        } catch (Exception e) {
            logger.error("Error checking if login page is displayed", e);
            return false;
        }
    }
    
    /**
     * Get accepted usernames text
     * 
     * @return Accepted usernames information
     */
    public String getAcceptedUsernames() {
        try {
            if (isElementDisplayed(acceptedUsernames)) {
                return getText(acceptedUsernames);
            }
        } catch (Exception e) {
            logger.debug("Could not get accepted usernames text");
        }
        return "";
    }
    
    /**
     * Get password information text
     * 
     * @return Password information
     */
    public String getPasswordInfo() {
        try {
            if (isElementDisplayed(passwordForAllUsers)) {
                return getText(passwordForAllUsers);
            }
        } catch (Exception e) {
            logger.debug("Could not get password information text");
        }
        return "";
    }
    
    /**
     * Check if username field is enabled
     * 
     * @return true if username field is enabled
     */
    public boolean isUsernameFieldEnabled() {
        return isElementEnabled(usernameField);
    }
    
    /**
     * Check if password field is enabled
     * 
     * @return true if password field is enabled
     */
    public boolean isPasswordFieldEnabled() {
        return isElementEnabled(passwordField);
    }
    
    /**
     * Check if login button is enabled
     * 
     * @return true if login button is enabled
     */
    public boolean isLoginButtonEnabled() {
        return isElementEnabled(loginButton);
    }
    
    /**
     * Get the placeholder text for username field
     * 
     * @return Username field placeholder text
     */
    public String getUsernamePlaceholder() {
        return getAttributeValue(usernameField, "placeholder");
    }
    
    /**
     * Get the placeholder text for password field
     * 
     * @return Password field placeholder text
     */
    public String getPasswordPlaceholder() {
        return getAttributeValue(passwordField, "placeholder");
    }
}