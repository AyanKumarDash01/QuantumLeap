package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage {
    private WebDriver driver;

    private By cartItemName = By.className("inventory_item_name");
    private By checkoutButton = By.cssSelector("button[data-test='checkout']");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getCartItemName() {
        return driver.findElement(cartItemName).getText();
    }

    public void clickCheckout() {
        driver.findElement(checkoutButton).click();
    }
    public boolean isProductInCart() {
        return driver.findElement(By.className("inventory_item_name")).isDisplayed();
    }

}
