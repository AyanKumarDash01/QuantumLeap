package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage {
    private WebDriver driver;

    // Locators
    private By firstItemAddToCart = By.xpath("(//button[@data-test='add-to-cart-sauce-labs-backpack'])[1]");
    private By cartIcon = By.cssSelector("a.shopping_cart_link");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
    }
    
    public void addFirstItemToCart() {
        driver.findElement(firstItemAddToCart).click();
    }

    public void goToCart() {
        driver.findElement(cartIcon).click();
    }
    
    public void addFirstProductToCart() {
        driver.findElement(By.cssSelector(".inventory_item button")).click();
    }

    public void openCart() {
        driver.findElement(By.cssSelector(".shopping_cart_link")).click();
    }

}
