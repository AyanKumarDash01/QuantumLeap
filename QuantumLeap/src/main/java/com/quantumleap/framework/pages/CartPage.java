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
 * CartPage class representing the Sauce Demo shopping cart page
 * Contains all locators and methods for cart functionality
 * 
 * @author QuantumLeap Team
 */
public class CartPage extends BasePage {
    
    private static final Logger logger = LoggerFactory.getLogger(CartPage.class);
    
    // Page Elements using Page Factory
    @FindBy(css = ".title")
    private WebElement pageTitle;
    
    @FindBy(css = ".cart_list")
    private WebElement cartList;
    
    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;
    
    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;
    
    @FindBy(id = "checkout")
    private WebElement checkoutButton;
    
    @FindBy(css = ".shopping_cart_link")
    private WebElement shoppingCartLink;
    
    @FindBy(css = ".cart_quantity_label")
    private WebElement quantityLabel;
    
    @FindBy(css = ".cart_desc_label")
    private WebElement descriptionLabel;
    
    // Constructor
    public CartPage(WebDriver driver) {
        super(driver);
        logger.info("CartPage initialized");
    }
    
    /**
     * Check if cart page is displayed
     * 
     * @return true if cart page is loaded correctly
     */
    public boolean isCartPageDisplayed() {
        try {
            waitForElementToBeVisible(pageTitle);
            return isElementDisplayed(pageTitle) && 
                   pageTitle.getText().equals("Your Cart");
        } catch (Exception e) {
            logger.error("Error checking if cart page is displayed", e);
            return false;
        }
    }
    
    /**
     * Get the page title text
     * 
     * @return Page title text
     */
    public String getPageTitle() {
        return getText(pageTitle);
    }
    
    /**
     * Get the number of items in cart
     * 
     * @return Number of items in cart
     */
    public int getCartItemCount() {
        return cartItems.size();
    }
    
    /**
     * Get list of all product names in cart
     * 
     * @return List of product names in cart
     */
    public List<String> getCartItemNames() {
        return cartItems.stream()
                .map(item -> item.findElement(By.cssSelector(".inventory_item_name")).getText())
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get list of all product prices in cart
     * 
     * @return List of product prices in cart
     */
    public List<String> getCartItemPrices() {
        return cartItems.stream()
                .map(item -> item.findElement(By.cssSelector(".inventory_item_price")).getText())
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get list of all product quantities in cart
     * 
     * @return List of product quantities in cart
     */
    public List<String> getCartItemQuantities() {
        return cartItems.stream()
                .map(item -> item.findElement(By.cssSelector(".cart_quantity")).getText())
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Remove item from cart by product name
     * 
     * @param productName Name of the product to remove
     * @return CartPage instance for method chaining
     */
    public CartPage removeItemFromCart(String productName) {
        try {
            WebElement cartItem = findCartItemByName(productName);
            WebElement removeButton = cartItem.findElement(By.cssSelector("button[id*='remove']"));
            clickElement(removeButton);
            waitFor(1); // Wait for item removal
            logger.info("Removed item from cart: {}", productName);
        } catch (Exception e) {
            logger.error("Failed to remove item from cart: {}", productName, e);
            throw new RuntimeException("Failed to remove item from cart", e);
        }
        return this;
    }
    
    /**
     * Find cart item element by product name
     * 
     * @param productName Name of the product
     * @return WebElement of the cart item
     */
    private WebElement findCartItemByName(String productName) {
        return cartItems.stream()
                .filter(item -> {
                    try {
                        String itemName = item.findElement(By.cssSelector(".inventory_item_name")).getText();
                        return itemName.equals(productName);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + productName));
    }
    
    /**
     * Get product quantity by name
     * 
     * @param productName Name of the product
     * @return Product quantity as string
     */
    public String getItemQuantity(String productName) {
        WebElement cartItem = findCartItemByName(productName);
        return cartItem.findElement(By.cssSelector(".cart_quantity")).getText();
    }
    
    /**
     * Get product price by name in cart
     * 
     * @param productName Name of the product
     * @return Product price as string
     */
    public String getItemPrice(String productName) {
        WebElement cartItem = findCartItemByName(productName);
        return cartItem.findElement(By.cssSelector(".inventory_item_price")).getText();
    }
    
    /**
     * Get product description by name in cart
     * 
     * @param productName Name of the product
     * @return Product description
     */
    public String getItemDescription(String productName) {
        WebElement cartItem = findCartItemByName(productName);
        return cartItem.findElement(By.cssSelector(".inventory_item_desc")).getText();
    }
    
    /**
     * Check if item exists in cart
     * 
     * @param productName Name of the product
     * @return true if item exists in cart
     */
    public boolean isItemInCart(String productName) {
        try {
            findCartItemByName(productName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Continue shopping - go back to products page
     * 
     * @return ProductsPage instance
     */
    public ProductsPage continueShopping() {
        clickElement(continueShoppingButton);
        logger.info("Clicked continue shopping button");
        return new ProductsPage(driver);
    }
    
    /**
     * Proceed to checkout
     * 
     * @return CheckoutPage instance
     */
    public CheckoutPage proceedToCheckout() {
        clickElement(checkoutButton);
        logger.info("Clicked checkout button");
        return new CheckoutPage(driver);
    }
    
    /**
     * Check if cart is empty
     * 
     * @return true if cart is empty
     */
    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }
    
    /**
     * Check if continue shopping button is enabled
     * 
     * @return true if continue shopping button is enabled
     */
    public boolean isContinueShoppingButtonEnabled() {
        return isElementEnabled(continueShoppingButton);
    }
    
    /**
     * Check if checkout button is enabled
     * 
     * @return true if checkout button is enabled
     */
    public boolean isCheckoutButtonEnabled() {
        return isElementEnabled(checkoutButton);
    }
    
    /**
     * Get all cart item details as a formatted string
     * 
     * @return Formatted string with cart details
     */
    public String getCartSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Cart Summary:\n");
        summary.append("Total Items: ").append(getCartItemCount()).append("\n");
        
        if (!isCartEmpty()) {
            List<String> names = getCartItemNames();
            List<String> quantities = getCartItemQuantities();
            List<String> prices = getCartItemPrices();
            
            for (int i = 0; i < names.size(); i++) {
                summary.append(String.format("%d. %s - Qty: %s - Price: %s\n", 
                    i + 1, names.get(i), quantities.get(i), prices.get(i)));
            }
        } else {
            summary.append("Cart is empty\n");
        }
        
        logger.info("Generated cart summary");
        return summary.toString();
    }
    
    /**
     * Remove all items from cart
     * 
     * @return CartPage instance for method chaining
     */
    public CartPage clearCart() {
        while (!isCartEmpty()) {
            List<String> itemNames = getCartItemNames();
            if (!itemNames.isEmpty()) {
                removeItemFromCart(itemNames.get(0));
                waitFor(1);
            } else {
                break;
            }
        }
        logger.info("Cleared all items from cart");
        return this;
    }
    
    /**
     * Verify cart contains expected items
     * 
     * @param expectedItems Array of expected product names
     * @return true if cart contains all expected items
     */
    public boolean verifyCartContains(String... expectedItems) {
        List<String> cartItemNames = getCartItemNames();
        
        for (String expectedItem : expectedItems) {
            if (!cartItemNames.contains(expectedItem)) {
                logger.warn("Expected item not found in cart: {}", expectedItem);
                return false;
            }
        }
        
        logger.info("Cart contains all expected items");
        return true;
    }
    
    /**
     * Calculate total price of items in cart (if price calculation is needed)
     * Note: This assumes prices are in format "$X.XX"
     * 
     * @return Total price as double
     */
    public double calculateTotalPrice() {
        double total = 0.0;
        List<String> prices = getCartItemPrices();
        
        for (String price : prices) {
            try {
                // Remove dollar sign and convert to double
                String cleanPrice = price.replace("$", "");
                total += Double.parseDouble(cleanPrice);
            } catch (NumberFormatException e) {
                logger.warn("Could not parse price: {}", price);
            }
        }
        
        logger.info("Calculated total price: ${}", total);
        return total;
    }
}