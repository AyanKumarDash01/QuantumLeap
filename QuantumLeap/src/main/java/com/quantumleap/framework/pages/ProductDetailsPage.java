package com.quantumleap.framework.pages;

import com.quantumleap.framework.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ProductDetailsPage class representing individual product detail view
 * 
 * @author QuantumLeap Team
 */
public class ProductDetailsPage extends BasePage {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductDetailsPage.class);
    
    // Page Elements
    @FindBy(css = ".inventory_details_name")
    private WebElement productName;
    
    @FindBy(css = ".inventory_details_desc")
    private WebElement productDescription;
    
    @FindBy(css = ".inventory_details_price")
    private WebElement productPrice;
    
    @FindBy(css = ".inventory_details_img")
    private WebElement productImage;
    
    @FindBy(css = "button[id*='add-to-cart']")
    private WebElement addToCartButton;
    
    @FindBy(css = "button[id*='remove']")
    private WebElement removeButton;
    
    @FindBy(id = "back-to-products")
    private WebElement backToProductsButton;
    
    // Constructor
    public ProductDetailsPage(WebDriver driver) {
        super(driver);
        logger.info("ProductDetailsPage initialized");
    }
    
    /**
     * Get product name
     * 
     * @return Product name
     */
    public String getProductName() {
        return getText(productName);
    }
    
    /**
     * Get product description
     * 
     * @return Product description
     */
    public String getProductDescription() {
        return getText(productDescription);
    }
    
    /**
     * Get product price
     * 
     * @return Product price
     */
    public String getProductPrice() {
        return getText(productPrice);
    }
    
    /**
     * Add product to cart
     * 
     * @return ProductDetailsPage instance for method chaining
     */
    public ProductDetailsPage addToCart() {
        clickElement(addToCartButton);
        logger.info("Added product to cart from details page");
        return this;
    }
    
    /**
     * Remove product from cart
     * 
     * @return ProductDetailsPage instance for method chaining
     */
    public ProductDetailsPage removeFromCart() {
        if (isElementDisplayed(removeButton)) {
            clickElement(removeButton);
            logger.info("Removed product from cart from details page");
        }
        return this;
    }
    
    /**
     * Go back to products page
     * 
     * @return ProductsPage instance
     */
    public ProductsPage backToProducts() {
        clickElement(backToProductsButton);
        logger.info("Navigated back to products page");
        return new ProductsPage(driver);
    }
    
    /**
     * Check if product is in cart
     * 
     * @return true if remove button is displayed
     */
    public boolean isProductInCart() {
        return isElementDisplayed(removeButton);
    }
}