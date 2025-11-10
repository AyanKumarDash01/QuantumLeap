package ui;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.LoginPage;
import utils.LoginDataProvider;

public class LoginTest extends BaseTest {

    @Test(dataProvider = "LoginCredentials", dataProviderClass = LoginDataProvider.class)
    public void verifyLoginWithMultipleUsers(String username, String password) throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        Thread.sleep(2000);

        if (loginPage.getCurrentUrl().contains("inventory.html")) {
            System.out.println(username + " → ✅ Login Successful");
            Assert.assertTrue(true);
            driver.navigate().to("https://www.saucedemo.com/"); // go back for next iteration
        } else {
            System.out.println(username + " → ❌ Login Failed");
            Assert.assertTrue(loginPage.isErrorDisplayed(), "Expected error not displayed!");
        }
    }
}
