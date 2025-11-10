package apiTests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ReqResApiCRUDTest {

    @BeforeClass
    public void setup() {
        // Set base URI for all requests
        RestAssured.baseURI = "https://reqres.in/api";
    }

    @Test(priority = 1)
    public void getUsersList() {
        Response response =
            given()
            .when()
                .get("/users?page=2")
            .then()
                .statusCode(200)
                .body("data", not(empty()))
                .body("data[0].email", containsString("@reqres.in"))
                .extract().response();

        System.out.println("GET Response:\n" + response.prettyPrint());

        // Extra verification using TestNG assertion
        Assert.assertEquals(response.statusCode(), 200);
    }

    @Test(priority = 2)
    public void createUser() {
        String requestBody = """
        {
            "name": "Ayan Dash",
            "job": "QA Engineer"
        }
        """;

        Response response =
            given()
                .header("Content-Type", "application/json")
                .body(requestBody)
            .when()
                .post("/users")
            .then()
                .statusCode(201)
                .body("name", equalTo("Ayan Dash"))
                .body("job", equalTo("QA Engineer"))
                .extract().response();

        System.out.println("POST Response:\n" + response.prettyPrint());

        Assert.assertEquals(response.statusCode(), 201);
        Assert.assertEquals(response.jsonPath().getString("name"), "Ayan Dash");
    }

    @Test(priority = 3)
    public void updateUser() {
        String requestBody = """
        {
            "name": "Ayan Dash",
            "job": "QA Engineer"
        }
        """;

        Response response =
            given()
                .header("Content-Type", "application/json")
                .body(requestBody)
            .when()
                .put("/users/2")
            .then()
                .statusCode(200)
                .body("name", equalTo("Ayan Dash"))
                .body("job", equalTo("QA Engineer"))
                .extract().response();

        System.out.println("PUT Response:\n" + response.prettyPrint());

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("job"), "QA Engineer");
    }

    @Test(priority = 4)
    public void deleteUser() {
        Response response =
            given()
            .when()
                .delete("/users/2")
            .then()
                .statusCode(204)
                .extract().response();

        System.out.println("DELETE Response status: " + response.statusCode());
        Assert.assertEquals(response.statusCode(), 204);
    }
}
