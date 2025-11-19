package com.quantumleap.framework.pages;

import com.quantumleap.framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * CheckoutPage class representing the Sauce Demo checkout process
 * Handles both checkout information and checkout overview pages
 * 
 * @author QuantumLeap Team
 */
public class CheckoutPage extends BasePage {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckoutPage.class);
    
    // Checkout Information Page Elements
    @FindBy(css = ".title")
    private WebElement pageTitle;
    
    @FindBy(id = "first-name")
    private WebElement firstNameField;
    
    @FindBy(id = "last-name")
    private WebElement lastNameField;
    
    @FindBy(id = "postal-code")
    private WebElement postalCodeField;
    
    @FindBy(id = "continue")
    private WebElement continueButton;
    
    @FindBy(id = "cancel")
    private WebElement cancelButton;
    
    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;
    
    // Checkout Overview Page Elements
    @FindBy(css = ".cart_item")
    private List<WebElement> orderItems;
    
    @FindBy(css = ".summary_subtotal_label")
    private WebElement subtotalLabel;
    
    @FindBy(css = ".summary_tax_label")
    private WebElement taxLabel;
    
    @FindBy(css = ".summary_total_label")
    private WebElement totalLabel;
    
    @FindBy(id = "finish")
    private WebElement finishButton;
    
    // Checkout Complete Page Elements
    @FindBy(css = ".complete-header")
    private WebElement completeHeader;
    
    @FindBy(css = ".complete-text")
    private WebElement completeText;
    
    @FindBy(id = "back-to-products")
    private WebElement backToProductsButton;
    
    // Constructor
    public CheckoutPage(WebDriver driver) {
        super(driver);
        logger.info("CheckoutPage initialized");
    }
    
    /**
     * Check if checkout information page is displayed
     * 
     * @return true if checkout information page is loaded correctly
     */
    public boolean isCheckoutInformationPageDisplayed() {
        try {
            waitForElementToBeVisible(pageTitle);
            return isElementDisplayed(pageTitle) && 
                   pageTitle.getText().contains("Checkout: Your Information");
        } catch (Exception e) {
            logger.error("Error checking if checkout information page is displayed", e);
            return false;
        }
    }
    
    /**
     * Check if checkout overview page is displayed
     * 
     * @return true if checkout overview page is loaded correctly
     */
    public boolean isCheckoutOverviewPageDisplayed() {
        try {
            waitForElementToBeVisible(pageTitle);
            return isElementDisplayed(pageTitle) && 
                   pageTitle.getText().contains("Checkout: Overview");
        } catch (Exception e) {
            logger.error("Error checking if checkout overview page is displayed", e);
            return false;
        }
    }
    
    /**
     * Check if checkout complete page is displayed
     * 
     * @return true if checkout complete page is loaded correctly
     */
    public boolean isCheckoutCompletePageDisplayed() {
        try {
            waitForElementToBeVisible(pageTitle);
            return isElementDisplayed(pageTitle) && 
                   pageTitle.getText().contains("Checkout: Complete!");
        } catch (Exception e) {
            logger.error("Error checking if checkout complete page is displayed", e);
            return false;
        }
    }
    
    /**
     * Get the current page title
     * 
     * @return Page title text
     */
    public String getPageTitle() {
        return getText(pageTitle);
    }
    
    // Checkout Information Page Methods
    
    /**
     * Enter first name
     * 
     * @param firstName First name to enter
     * @return CheckoutPage instance for method chaining
     */
    public CheckoutPage enterFirstName(String firstName) {
        waitForElementToBeVisible(firstNameField);
        enterText(firstNameField, firstName);
        logger.info("Entered first name: {}", firstName);
        return this;
    }
    
    /**
     * Enter last name
     * 
     * @param lastName Last name to enter
     * @return CheckoutPage instance for method chaining
     */
    public CheckoutPage enterLastName(String lastName) {
        waitForElementToBeVisible(lastNameField);
        enterText(lastNameField, lastName);
        logger.info("Entered last name: {}", lastName);
        return this;
    }
    
    /**
     * Enter postal code
     * 
     * @param postalCode Postal code to enter
     * @return CheckoutPage instance for method chaining
     */
    public CheckoutPage enterPostalCode(String postalCode) {
        waitForElementToBeVisible(postalCodeField);
        enterText(postalCodeField, postalCode);
        logger.info("Entered postal code: {}", postalCode);
        return this;
    }
    
    /**
     * Fill all checkout information at once
     * 
     * @param firstName First name
     * @param lastName Last name
     * @param postalCode Postal code
     * @return CheckoutPage instance for method chaining
     */
    public CheckoutPage fillCheckoutInformation(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        logger.info("Filled all checkout information");
        return this;
    }
    
    /**
     * Click continue button to proceed to overview
     * 
     * @return CheckoutPage instance (now showing overview)
     */
    public CheckoutPage clickContinue() {
        clickElement(continueButton);
        waitFor(1);
        logger.info("Clicked continue button");
        return this;
    }
    
    /**
     * Click cancel button to go back to cart
     * 
     * @return CartPage instance
     */
    public CartPage clickCancel() {
        clickElement(cancelButton);
        logger.info("Clicked cancel button");
        return new CartPage(driver);
    }
    
    /**
     * Get error message text from checkout information page
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
     * @return true if error message is visible
     */
    public boolean isErrorMessageDisplayed() {
        try {
            return isElementDisplayed(errorMessage);
        } catch (Exception e) {
            return false;
        }
    }
    
    // Checkout Overview Page Methods
    
    /**
     * Get list of items in checkout overview
     * 
     * @return List of item names
     */
    public List<String> getOrderItemNames() {
        return orderItems.stream()
                .map(item -> item.findElement(By.cssSelector(".inventory_item_name")).getText())
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get list of item prices in checkout overview
     * 
     * @return List of item prices
     */
    public List<String> getOrderItemPrices() {
        return orderItems.stream()
                .map(item -> item.findElement(By.cssSelector(".inventory_item_price")).getText())
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get subtotal amount
     * 
     * @return Subtotal amount as string
     */
    public String getSubtotal() {
        try {
            return getText(subtotalLabel);
        } catch (Exception e) {
            logger.error("Could not get subtotal", e);
            return "";
        }
    }
    
    /**
     * Get tax amount
     * 
     * @return Tax amount as string
     */
    public String getTax() {
        try {
            return getText(taxLabel);
        } catch (Exception e) {
            logger.error("Could not get tax", e);
            return "";
        }
    }
    
    /**
     * Get total amount
     * 
     * @return Total amount as string
     */
    public String getTotal() {
        try {
            return getText(totalLabel);
        } catch (Exception e) {
            logger.error("Could not get total", e);
            return "";
        }
    }
    
    /**
     * Get checkout summary information
     * 
     * @return Formatted checkout summary
     */
    public String getCheckoutSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Checkout Summary:\n");
        summary.append("Items: ").append(getOrderItemNames().size()).append("\n");
        summary.append("Subtotal: ").append(getSubtotal()).append("\n");
        summary.append("Tax: ").append(getTax()).append("\n");
        summary.append("Total: ").append(getTotal()).append("\n");
        
        List<String> itemNames = getOrderItemNames();
        List<String> itemPrices = getOrderItemPrices();
        
        for (int i = 0; i < itemNames.size(); i++) {
            summary.append(String.format("%d. %s - %s\n", 
                i + 1, itemNames.get(i), itemPrices.get(i)));
        }
        
        logger.info("Generated checkout summary");
        return summary.toString();
    }
    
    /**
     * Click finish button to complete the order
     * 
     * @return CheckoutPage instance (now showing complete page)
     */
    public CheckoutPage clickFinish() {
        clickElement(finishButton);
        waitFor(2);
        logger.info("Clicked finish button - order completed");
        return this;
    }
    
    // Checkout Complete Page Methods
    
    /**
     * Get completion header text
     * 
     * @return Completion header text
     */
    public String getCompletionHeader() {
        try {
            return getText(completeHeader);
        } catch (Exception e) {
            logger.error("Could not get completion header", e);
            return "";
        }
    }
    
    /**
     * Get completion message text
     * 
     * @return Completion message text
     */
    public String getCompletionText() {
        try {
            return getText(completeText);
        } catch (Exception e) {
            logger.error("Could not get completion text", e);
            return "";
        }
    }
    
    /**
     * Click back to products button to return to products page
     * 
     * @return ProductsPage instance
     */
    public ProductsPage backToProducts() {
        clickElement(backToProductsButton);
        logger.info("Clicked back to products button");
        return new ProductsPage(driver);
    }
    
    // Utility Methods
    
    /**
     * Complete full checkout process with provided information
     * 
     * @param firstName First name
     * @param lastName Last name
     * @param postalCode Postal code
     * @return CheckoutPage instance showing complete page
     */
    public CheckoutPage completeCheckout(String firstName, String lastName, String postalCode) {
        if (isCheckoutInformationPageDisplayed()) {
            fillCheckoutInformation(firstName, lastName, postalCode);
            clickContinue();
        }
        
        if (isCheckoutOverviewPageDisplayed()) {
            clickFinish();
        }
        
        logger.info("Completed full checkout process");
        return this;
    }
    
    /**
     * Verify checkout information fields are enabled and visible
     * 
     * @return true if all checkout fields are accessible
     */
    public boolean areCheckoutFieldsAccessible() {
        try {
            return isElementDisplayed(firstNameField) && isElementEnabled(firstNameField) &&
                   isElementDisplayed(lastNameField) && isElementEnabled(lastNameField) &&
                   isElementDisplayed(postalCodeField) && isElementEnabled(postalCodeField) &&
                   isElementDisplayed(continueButton) && isElementEnabled(continueButton);
        } catch (Exception e) {
            logger.error("Error checking checkout fields accessibility", e);
            return false;
        }
    }
    
    /**
     * Verify order items match expected items
     * 
     * @param expectedItems Array of expected item names
     * @return true if order contains all expected items
     */
    public boolean verifyOrderItems(String... expectedItems) {
        List<String> actualItems = getOrderItemNames();
        
        for (String expectedItem : expectedItems) {
            if (!actualItems.contains(expectedItem)) {
                logger.warn("Expected item not found in order: {}", expectedItem);
                return false;
            }
        }
        
        logger.info("Order contains all expected items");
        return true;
    }
}