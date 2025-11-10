package ui;

import org.testng.Assert;
import org.testng.annotations.Test;
import base.BaseTest;
import pages.LoginPage;
import pages.InventoryPage;
import pages.CartPage;
import pages.CheckoutPage;

public class EndToEndPurchaseTest extends BaseTest {

    @Test
    public void testEndToEndPurchaseFlow() throws InterruptedException {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLogin();

        Thread.sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"), "Login failed!");

        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.addFirstItemToCart();
        inventoryPage.goToCart();

        Thread.sleep(1000);
        Assert.assertTrue(driver.getCurrentUrl().contains("cart.html"), "Cart page not opened!");

        CartPage cartPage = new CartPage(driver);
        String itemName = cartPage.getCartItemName();
        System.out.println("🛒 Product added to cart: " + itemName);
        cartPage.clickCheckout();

        Thread.sleep(1000);
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one.html"), "Checkout Step 1 not opened!");

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.enterUserInfo("Ayan", "Dash", "700106");

        Thread.sleep(1000);
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-two.html"), "Checkout Step 2 not opened!");

        checkoutPage.clickFinish();
        Thread.sleep(1500);

        String successMsg = checkoutPage.getSuccessMessage();
        Assert.assertEquals(successMsg, "Thank you for your order!", "❌ Order not completed!");
        System.out.println("✅ Order placed successfully!");
    }
}
