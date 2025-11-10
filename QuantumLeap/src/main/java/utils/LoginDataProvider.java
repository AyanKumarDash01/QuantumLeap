package utils;

import org.testng.annotations.DataProvider;

public class LoginDataProvider {

    @DataProvider(name = "LoginCredentials")
    public Object[][] getData() {
        return new Object[][]{
            {"standard_user", "secret_sauce"},
            {"locked_out_user", "secret_sauce"},
            {"problem_user", "secret_sauce"},
            {"performance_glitch_user", "secret_sauce"}
        };
    }
}
