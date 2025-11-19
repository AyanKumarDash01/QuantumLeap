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
 * ProductsPage class representing the Sauce Demo inventory/products page
 * Contains all locators and methods for product functionality
 * 
 * @author QuantumLeap Team
 */
public class ProductsPage extends BasePage {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductsPage.class);
    
    // Page Elements using Page Factory
    @FindBy(css = ".app_logo")
    private WebElement appLogo;
    
    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;
    
    @FindBy(css = ".shopping_cart_link")
    private WebElement shoppingCartLink;
    
    @FindBy(css = ".shopping_cart_badge")
    private WebElement shoppingCartBadge;
    
    @FindBy(css = ".product_sort_container")
    private WebElement sortDropdown;
    
    @FindBy(css = ".inventory_list")
    private WebElement inventoryList;
    
    @FindBy(css = ".inventory_item")
    private List<WebElement> inventoryItems;
    
    @FindBy(css = ".title")
    private WebElement pageTitle;
    
    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;
    
    @FindBy(id = "inventory_sidebar_link")
    private WebElement allItemsLink;
    
    @FindBy(id = "about_sidebar_link")
    private WebElement aboutLink;
    
    @FindBy(id = "reset_sidebar_link")
    private WebElement resetAppStateLink;
    
    @FindBy(css = ".bm-cross-button")
    private WebElement closeMenuButton;
    
    // Constructor
    public ProductsPage(WebDriver driver) {
        super(driver);
        logger.info("ProductsPage initialized");
    }
    
    /**
     * Check if products page is displayed
     * 
     * @return true if products page is loaded correctly
     */
    public boolean isProductsPageDisplayed() {
        try {
            waitForElementToBeVisible(pageTitle);
            return isElementDisplayed(appLogo) && 
                   isElementDisplayed(inventoryList) && 
                   pageTitle.getText().equals("Products");
        } catch (Exception e) {
            logger.error("Error checking if products page is displayed", e);
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
     * Get the number of products displayed
     * 
     * @return Number of products
     */
    public int getProductCount() {
        return inventoryItems.size();
    }
    
    /**
     * Get list of all product names
     * 
     * @return List of product names
     */
    public List<String> getAllProductNames() {
        return inventoryItems.stream()
                .map(item -> item.findElement(By.cssSelector(".inventory_item_name")).getText())
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get list of all product prices
     * 
     * @return List of product prices
     */
    public List<String> getAllProductPrices() {
        return inventoryItems.stream()
                .map(item -> item.findElement(By.cssSelector(".inventory_item_price")).getText())
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Add product to cart by product name
     * 
     * @param productName Name of the product to add
     * @return ProductsPage instance for method chaining
     */
    public ProductsPage addProductToCart(String productName) {
        try {
            WebElement product = findProductByName(productName);
            WebElement addToCartButton = product.findElement(By.cssSelector("button[id*='add-to-cart']"));
            scrollToElement(addToCartButton);
            clickElement(addToCartButton);
            logger.info("Added product to cart: {}", productName);
        } catch (Exception e) {
            logger.error("Failed to add product to cart: {}", productName, e);
            throw new RuntimeException("Failed to add product to cart", e);
        }
        return this;
    }
    
    /**
     * Remove product from cart by product name
     * 
     * @param productName Name of the product to remove
     * @return ProductsPage instance for method chaining
     */
    public ProductsPage removeProductFromCart(String productName) {
        try {
            WebElement product = findProductByName(productName);
            WebElement removeButton = product.findElement(By.cssSelector("button[id*='remove']"));
            scrollToElement(removeButton);
            clickElement(removeButton);
            logger.info("Removed product from cart: {}", productName);
        } catch (Exception e) {
            logger.error("Failed to remove product from cart: {}", productName, e);
            throw new RuntimeException("Failed to remove product from cart", e);
        }
        return this;
    }
    
    /**
     * Find product element by name
     * 
     * @param productName Name of the product
     * @return WebElement of the product
     */
    private WebElement findProductByName(String productName) {
        return inventoryItems.stream()
                .filter(item -> {
                    try {
                        String itemName = item.findElement(By.cssSelector(".inventory_item_name")).getText();
                        return itemName.equals(productName);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found: " + productName));
    }
    
    /**
     * Get product price by name
     * 
     * @param productName Name of the product
     * @return Product price as string
     */
    public String getProductPrice(String productName) {
        WebElement product = findProductByName(productName);
        return product.findElement(By.cssSelector(".inventory_item_price")).getText();
    }
    
    /**
     * Get product description by name
     * 
     * @param productName Name of the product
     * @return Product description
     */
    public String getProductDescription(String productName) {
        WebElement product = findProductByName(productName);
        return product.findElement(By.cssSelector(".inventory_item_desc")).getText();
    }
    
    /**
     * Click on product to view details
     * 
     * @param productName Name of the product
     * @return ProductDetailsPage instance
     */
    public ProductDetailsPage clickProduct(String productName) {
        WebElement product = findProductByName(productName);
        WebElement productLink = product.findElement(By.cssSelector(".inventory_item_name"));
        scrollToElement(productLink);
        clickElement(productLink);
        logger.info("Clicked on product: {}", productName);
        return new ProductDetailsPage(driver);
    }
    
    /**
     * Sort products using the dropdown
     * 
     * @param sortOption Sort option (Name (A to Z), Name (Z to A), Price (low to high), Price (high to low))
     * @return ProductsPage instance for method chaining
     */
    public ProductsPage sortProducts(String sortOption) {
        selectDropdownByVisibleText(sortDropdown, sortOption);
        waitFor(1); // Wait for sorting to complete
        logger.info("Sorted products by: {}", sortOption);
        return this;
    }
    
    /**
     * Get current sort option
     * 
     * @return Current selected sort option
     */
    public String getCurrentSortOption() {
        return getAttributeValue(sortDropdown, "value");
    }
    
    /**
     * Open shopping cart
     * 
     * @return CartPage instance
     */
    public CartPage openShoppingCart() {
        clickElement(shoppingCartLink);
        logger.info("Opened shopping cart");
        return new CartPage(driver);
    }
    
    /**
     * Get shopping cart item count
     * 
     * @return Number of items in cart, 0 if no badge displayed
     */
    public int getShoppingCartItemCount() {
        try {
            if (isElementDisplayed(shoppingCartBadge)) {
                String badgeText = getText(shoppingCartBadge);
                return Integer.parseInt(badgeText);
            }
        } catch (Exception e) {
            logger.debug("No cart badge or unable to parse count");
        }
        return 0;
    }
    
    /**
     * Check if product is in cart (button shows 'Remove')
     * 
     * @param productName Name of the product
     * @return true if product is in cart
     */
    public boolean isProductInCart(String productName) {
        try {
            WebElement product = findProductByName(productName);
            List<WebElement> removeButtons = product.findElements(By.cssSelector("button[id*='remove']"));
            return !removeButtons.isEmpty();
        } catch (Exception e) {
            logger.debug("Error checking if product is in cart: {}", productName);
            return false;
        }
    }
    
    /**
     * Open menu
     * 
     * @return ProductsPage instance for method chaining
     */
    public ProductsPage openMenu() {
        clickElement(menuButton);
        waitFor(1); // Wait for menu animation
        logger.info("Opened menu");
        return this;
    }
    
    /**
     * Close menu
     * 
     * @return ProductsPage instance for method chaining
     */
    public ProductsPage closeMenu() {
        if (isElementDisplayed(closeMenuButton)) {
            clickElement(closeMenuButton);
            waitFor(1); // Wait for menu animation
            logger.info("Closed menu");
        }
        return this;
    }
    
    /**
     * Logout from the application
     * 
     * @return LoginPage instance
     */
    public LoginPage logout() {
        openMenu();
        clickElement(logoutLink);
        logger.info("Logged out successfully");
        return new LoginPage(driver);
    }
    
    /**
     * Reset application state
     * 
     * @return ProductsPage instance for method chaining
     */
    public ProductsPage resetAppState() {
        openMenu();
        clickElement(resetAppStateLink);
        closeMenu();
        logger.info("Reset application state");
        return this;
    }
    
    /**
     * Navigate to All Items
     * 
     * @return ProductsPage instance for method chaining
     */
    public ProductsPage goToAllItems() {
        openMenu();
        clickElement(allItemsLink);
        closeMenu();
        logger.info("Navigated to All Items");
        return this;
    }
    
    /**
     * Get all available product names for data-driven tests
     * 
     * @return Array of product names
     */
    public String[] getAvailableProductNames() {
        List<String> productNames = getAllProductNames();
        return productNames.toArray(new String[0]);
    }
    
    /**
     * Add multiple products to cart
     * 
     * @param productNames Array of product names to add
     * @return ProductsPage instance for method chaining
     */
    public ProductsPage addMultipleProductsToCart(String... productNames) {
        for (String productName : productNames) {
            addProductToCart(productName);
        }
        logger.info("Added {} products to cart", productNames.length);
        return this;
    }
    
    /**
     * Verify products are displayed correctly
     * 
     * @return true if all products have name, price, and add to cart button
     */
    public boolean areProductsDisplayedCorrectly() {
        try {
            for (WebElement item : inventoryItems) {
                // Check if each product has name, price, and add button
                if (item.findElements(By.cssSelector(".inventory_item_name")).isEmpty() ||
                    item.findElements(By.cssSelector(".inventory_item_price")).isEmpty() ||
                    item.findElements(By.cssSelector("button[id*='add-to-cart']")).isEmpty()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Error verifying products display", e);
            return false;
        }
    }
}
