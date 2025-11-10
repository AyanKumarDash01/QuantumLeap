package stepDefinitions;

import io.cucumber.java.en.*;
import org.testng.Assert;
import base.BaseTest;
import pages.LoginPage;
import pages.InventoryPage;
import pages.CartPage;

public class AddToCartSteps extends BaseTest {

    LoginPage loginPage;
    InventoryPage inventoryPage;
    CartPage cartPage;

    @Given("User is on the SauceDemo login page")
    public void user_is_on_the_saucedemo_login_page() {
        setup();
        driver.get("https://www.saucedemo.com/");
    }

    @When("User logs in with valid credentials")
    public void user_logs_in_with_valid_credentials() {
        loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");
    }

    @When("User adds the first product to the cart")
    public void user_adds_the_first_product_to_the_cart() {
        inventoryPage = new InventoryPage(driver);
        inventoryPage.addFirstProductToCart();
    }

    @When("User navigates to the cart page")
    public void user_navigates_to_the_cart_page() {
        inventoryPage.openCart();
    }

    @Then("The product should be visible in the cart")
    public void the_product_should_be_visible_in_the_cart() {
        cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isProductInCart(), "Product not found in cart!");
        tearDown();
    }
}
