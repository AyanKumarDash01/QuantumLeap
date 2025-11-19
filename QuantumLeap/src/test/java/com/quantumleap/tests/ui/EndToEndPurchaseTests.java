package com.quantumleap.tests.ui;

import com.quantumleap.framework.pages.*;
import com.quantumleap.tests.BaseTest;
import com.quantumleap.tests.TestDataProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * End-to-End Purchase Flow Tests
 * Tests complete user journey from login to order completion
 * 
 * @author QuantumLeap Team
 */
@Test(groups = {"e2e", "ui", "purchase", "regression"})
public class EndToEndPurchaseTests extends BaseTest {
    
    /**
     * Complete end-to-end purchase flow test
     * Tests the entire user journey: Login → Browse → Add to Cart → Checkout → Complete
     */
    @Test(priority = 1,
          description = "Complete end-to-end purchase flow with single product",
          dataProvider = "endToEndScenarios",
          dataProviderClass = TestDataProvider.class)
    public void testCompleteECommercePurchaseFlow(String username, String password, 
                                                  String[] productsToAdd, String firstName, 
                                                  String lastName, String postalCode, 
                                                  String scenario) {
        
        logTestStep("Starting E2E test: " + scenario);
        logTestInfo(String.format("User: %s, Products: %d, Customer: %s %s", 
            username, productsToAdd.length, firstName, lastName));
        
        // Step 1: Login
        logTestStep("Step 1: Performing user login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage();
        
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), 
            "Login page should be displayed");
        
        Object loginResult = loginPage.login(username, password);
        Assert.assertTrue(loginResult instanceof ProductsPage, 
            "Login should be successful");
        logAssertion("User login successful", true);
        
        // Step 2: Browse Products and Add to Cart
        logTestStep("Step 2: Browsing products and adding to cart");
        ProductsPage productsPage = (ProductsPage) loginResult;
        
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), 
            "Products page should be displayed");
        Assert.assertTrue(productsPage.areProductsDisplayedCorrectly(), 
            "Products should be displayed correctly");
        
        // Verify initial cart state
        Assert.assertEquals(productsPage.getShoppingCartItemCount(), 0, 
            "Cart should be empty initially");
        
        // Add products to cart
        for (String productName : productsToAdd) {
            logTestStep("Adding product to cart: " + productName);
            
            // Take screenshot of product before adding
            takeScreenshot("before_adding_" + productName.replaceAll("\\s+", "_"));
            
            productsPage.addProductToCart(productName);
            
            // Verify product was added
            Assert.assertTrue(productsPage.isProductInCart(productName), 
                String.format("Product '%s' should be marked as in cart", productName));
        }
        
        // Verify cart count
        int expectedCartCount = productsToAdd.length;
        Assert.assertEquals(productsPage.getShoppingCartItemCount(), expectedCartCount, 
            String.format("Cart should contain %d items", expectedCartCount));
        logAssertion(String.format("Cart contains %d items as expected", expectedCartCount), true);
        
        // Step 3: Navigate to Cart and Verify Contents
        logTestStep("Step 3: Navigating to cart and verifying contents");
        CartPage cartPage = productsPage.openShoppingCart();
        
        Assert.assertTrue(cartPage.isCartPageDisplayed(), 
            "Cart page should be displayed");
        Assert.assertEquals(cartPage.getCartItemCount(), expectedCartCount, 
            String.format("Cart should contain %d items", expectedCartCount));
        
        // Verify all products are in cart
        Assert.assertTrue(cartPage.verifyCartContains(productsToAdd), 
            "Cart should contain all expected products");
        logAssertion("All products verified in cart", true);
        
        // Log cart summary
        String cartSummary = cartPage.getCartSummary();
        logTestInfo("Cart Summary:\\n" + cartSummary);
        
        // Take screenshot of cart
        takeScreenshot("cart_contents_" + scenario.replaceAll("\\s+", "_"));
        
        // Step 4: Proceed to Checkout
        logTestStep("Step 4: Proceeding to checkout");
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        
        Assert.assertTrue(checkoutPage.isCheckoutInformationPageDisplayed(), 
            "Checkout information page should be displayed");
        Assert.assertTrue(checkoutPage.areCheckoutFieldsAccessible(), 
            "Checkout fields should be accessible");
        
        // Step 5: Fill Checkout Information
        logTestStep("Step 5: Filling checkout information");
        checkoutPage.fillCheckoutInformation(firstName, lastName, postalCode);
        checkoutPage.clickContinue();
        
        // Step 6: Review Order
        logTestStep("Step 6: Reviewing order on checkout overview");
        Assert.assertTrue(checkoutPage.isCheckoutOverviewPageDisplayed(), 
            "Checkout overview page should be displayed");
        
        // Verify order items
        Assert.assertTrue(checkoutPage.verifyOrderItems(productsToAdd), 
            "Order should contain all expected items");
        
        // Verify pricing information is displayed
        SoftAssert softAssert = new SoftAssert();
        String subtotal = checkoutPage.getSubtotal();
        String tax = checkoutPage.getTax();
        String total = checkoutPage.getTotal();
        
        softAssert.assertFalse(subtotal.isEmpty(), "Subtotal should be displayed");
        softAssert.assertFalse(tax.isEmpty(), "Tax should be displayed");
        softAssert.assertFalse(total.isEmpty(), "Total should be displayed");
        softAssert.assertAll();
        
        // Log order summary
        String orderSummary = checkoutPage.getCheckoutSummary();
        logTestInfo("Order Summary:\\n" + orderSummary);
        
        // Take screenshot of order review
        takeScreenshot("order_review_" + scenario.replaceAll("\\s+", "_"));
        
        // Step 7: Complete Order
        logTestStep("Step 7: Completing the order");
        checkoutPage.clickFinish();
        
        // Step 8: Verify Order Completion
        logTestStep("Step 8: Verifying order completion");
        Assert.assertTrue(checkoutPage.isCheckoutCompletePageDisplayed(), 
            "Order completion page should be displayed");
        
        String completionHeader = checkoutPage.getCompletionHeader();
        String completionText = checkoutPage.getCompletionText();
        
        Assert.assertFalse(completionHeader.isEmpty(), 
            "Completion header should be displayed");
        Assert.assertFalse(completionText.isEmpty(), 
            "Completion text should be displayed");
        
        logTestInfo("Order completion header: " + completionHeader);
        logTestInfo("Order completion text: " + completionText);
        
        // Take screenshot of successful completion
        takeScreenshot("order_completed_" + scenario.replaceAll("\\s+", "_"));
        
        // Step 9: Return to Products
        logTestStep("Step 9: Returning to products page");
        ProductsPage returnedProductsPage = checkoutPage.backToProducts();
        
        Assert.assertTrue(returnedProductsPage.isProductsPageDisplayed(), 
            "Should return to products page successfully");
        
        // Verify cart is reset (optional - depends on application behavior)
        int finalCartCount = returnedProductsPage.getShoppingCartItemCount();
        logTestInfo("Final cart count after order completion: " + finalCartCount);
        
        logAssertion("Complete E2E purchase flow successful", true);
    }
    
    /**
     * Test E2E flow with cart modifications
     */
    @Test(priority = 2,
          description = "E2E flow with cart item removal and re-addition")
    public void testECommercePurchaseFlowWithCartModifications() {
        logTestStep("Starting E2E test with cart modifications");
        
        // Login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage();
        ProductsPage productsPage = (ProductsPage) loginPage.login(
            config.getDefaultUsername(), config.getDefaultPassword());
        
        // Add multiple products
        String[] initialProducts = {"Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt"};
        productsPage.addMultipleProductsToCart(initialProducts);
        
        // Go to cart
        CartPage cartPage = productsPage.openShoppingCart();
        Assert.assertEquals(cartPage.getCartItemCount(), 3, "Cart should have 3 items");
        
        // Remove one item
        logTestStep("Removing item from cart");
        cartPage.removeItemFromCart("Sauce Labs Bike Light");
        Assert.assertEquals(cartPage.getCartItemCount(), 2, "Cart should have 2 items after removal");
        
        // Continue shopping and add another item
        logTestStep("Continuing shopping to add another item");
        ProductsPage returnedProductsPage = cartPage.continueShopping();
        returnedProductsPage.addProductToCart("Sauce Labs Onesie");
        
        // Complete purchase with modified cart
        cartPage = returnedProductsPage.openShoppingCart();
        Assert.assertEquals(cartPage.getCartItemCount(), 3, "Cart should have 3 items after modification");
        
        // Proceed with checkout
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.fillCheckoutInformation("Modified", "Cart", "12345");
        checkoutPage.clickContinue();
        
        // Verify final items
        String[] expectedFinalItems = {"Sauce Labs Backpack", "Sauce Labs Bolt T-Shirt", "Sauce Labs Onesie"};
        Assert.assertTrue(checkoutPage.verifyOrderItems(expectedFinalItems), 
            "Final order should contain modified cart items");
        
        checkoutPage.clickFinish();
        Assert.assertTrue(checkoutPage.isCheckoutCompletePageDisplayed(), 
            "Order should complete successfully");
        
        logAssertion("E2E flow with cart modifications successful", true);
    }
    
    /**
     * Test E2E flow with sorting and product selection
     */
    @Test(priority = 3,
          description = "E2E flow with product sorting before selection")
    public void testECommercePurchaseFlowWithSorting() {
        logTestStep("Starting E2E test with product sorting");
        
        // Login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage();
        ProductsPage productsPage = (ProductsPage) loginPage.login(
            config.getDefaultUsername(), config.getDefaultPassword());
        
        // Test different sorting options
        logTestStep("Testing product sorting");
        productsPage.sortProducts("Price (low to high)");
        waitFor(1); // Wait for sort to complete
        
        // Get products after sorting
        java.util.List<String> productNames = productsPage.getAllProductNames();
        java.util.List<String> productPrices = productsPage.getAllProductPrices();
        
        logTestInfo("Products after sorting by price (low to high):");
        for (int i = 0; i < productNames.size(); i++) {
            logTestInfo(String.format("%d. %s - %s", i+1, productNames.get(i), productPrices.get(i)));
        }
        
        // Add first two products (should be cheapest)
        String firstProduct = productNames.get(0);
        String secondProduct = productNames.get(1);
        
        productsPage.addProductToCart(firstProduct);
        productsPage.addProductToCart(secondProduct);
        
        // Complete purchase
        CartPage cartPage = productsPage.openShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.completeCheckout("Sorted", "Purchase", "54321");
        
        Assert.assertTrue(checkoutPage.isCheckoutCompletePageDisplayed(), 
            "Order with sorted products should complete successfully");
        
        logAssertion("E2E flow with sorting successful", true);
    }
    
    /**
     * Test E2E flow error recovery
     */
    @Test(priority = 4,
          description = "E2E flow with error recovery during checkout")
    public void testECommercePurchaseFlowErrorRecovery() {
        logTestStep("Starting E2E test with error recovery");
        
        // Login and add product
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage();
        ProductsPage productsPage = (ProductsPage) loginPage.login(
            config.getDefaultUsername(), config.getDefaultPassword());
        
        productsPage.addProductToCart("Sauce Labs Backpack");
        
        // Go to checkout
        CartPage cartPage = productsPage.openShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        
        // First, try with invalid data to trigger error
        logTestStep("Testing error recovery with invalid checkout data");
        checkoutPage.fillCheckoutInformation("", "", ""); // Empty fields
        checkoutPage.clickContinue();
        
        // Verify error is displayed
        Assert.assertTrue(checkoutPage.isErrorMessageDisplayed(), 
            "Error message should be displayed for empty fields");
        
        String errorMessage = checkoutPage.getErrorMessage();
        logTestInfo("Error message: " + errorMessage);
        
        // Now fill with valid data and proceed
        logTestStep("Recovering from error with valid data");
        checkoutPage.fillCheckoutInformation("Recovery", "Test", "99999");
        checkoutPage.clickContinue();
        
        // Should now proceed to overview
        Assert.assertTrue(checkoutPage.isCheckoutOverviewPageDisplayed(), 
            "Should proceed to overview after providing valid data");
        
        // Complete the order
        checkoutPage.clickFinish();
        Assert.assertTrue(checkoutPage.isCheckoutCompletePageDisplayed(), 
            "Order should complete after error recovery");
        
        logAssertion("E2E flow with error recovery successful", true);
    }
    
    /**
     * Test performance during E2E flow
     */
    @Test(priority = 5,
          description = "E2E flow performance measurement",
          timeOut = 60000) // 1 minute timeout
    public void testECommercePurchaseFlowPerformance() {
        logTestStep("Starting E2E performance test");
        
        long startTime = System.currentTimeMillis();
        
        // Complete E2E flow
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage();
        
        long loginStart = System.currentTimeMillis();
        ProductsPage productsPage = (ProductsPage) loginPage.login(
            config.getDefaultUsername(), config.getDefaultPassword());
        long loginTime = System.currentTimeMillis() - loginStart;
        
        long addToCartStart = System.currentTimeMillis();
        productsPage.addProductToCart("Sauce Labs Backpack");
        long addToCartTime = System.currentTimeMillis() - addToCartStart;
        
        long checkoutStart = System.currentTimeMillis();
        CartPage cartPage = productsPage.openShoppingCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.completeCheckout("Performance", "Test", "12345");
        long checkoutTime = System.currentTimeMillis() - checkoutStart;
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        // Log performance metrics
        logTestInfo(String.format("Performance Metrics:"));
        logTestInfo(String.format("Login Time: %d ms", loginTime));
        logTestInfo(String.format("Add to Cart Time: %d ms", addToCartTime));
        logTestInfo(String.format("Checkout Time: %d ms", checkoutTime));
        logTestInfo(String.format("Total E2E Time: %d ms", totalTime));
        
        // Performance assertions (adjust thresholds as needed)
        Assert.assertTrue(loginTime < 15000, "Login should complete within 15 seconds");
        Assert.assertTrue(addToCartTime < 5000, "Add to cart should complete within 5 seconds");
        Assert.assertTrue(checkoutTime < 20000, "Checkout should complete within 20 seconds");
        Assert.assertTrue(totalTime < 45000, "Complete E2E flow should complete within 45 seconds");
        
        Assert.assertTrue(checkoutPage.isCheckoutCompletePageDisplayed(), 
            "Order should complete successfully");
        
        logAssertion("E2E performance test successful", true);
    }
}