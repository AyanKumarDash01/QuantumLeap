package com.quantumleap.tests.api;

import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Comprehensive CRUD API Tests for ReqRes API
 * Tests GET, POST, PUT, DELETE operations with proper assertions
 * 
 * @author QuantumLeap Team
 */
@Test(groups = {"api", "crud", "reqres"})
public class ReqResApiTests extends BaseApiTest {
    
    private static final String USERS_ENDPOINT = "/users";
    private static final String SINGLE_USER_ENDPOINT = "/users/{id}";
    private static final long MAX_RESPONSE_TIME = 3000; // 3 seconds
    
    /**
     * Test GET request to fetch list of users
     * Validates response structure, status code, and data format
     */
    @Test(priority = 1, description = "GET - Fetch list of users with validation")
    public void testGetListOfUsers() {
        logApiStep("Testing GET request to fetch list of users");
        
        Response response = givenRequest()
            .queryParam("page", 2)
            .when()
            .get(USERS_ENDPOINT)
            .then()
            .statusCode(200)
            .contentType("application/json; charset=utf-8")
            .body("page", equalTo(2))
            .body("per_page", greaterThan(0))
            .body("total", greaterThan(0))
            .body("total_pages", greaterThan(0))
            .body("data", not(empty()))
            .body("data[0]", hasKey("id"))
            .body("data[0]", hasKey("email"))
            .body("data[0]", hasKey("first_name"))
            .body("data[0]", hasKey("last_name"))
            .body("data[0]", hasKey("avatar"))
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        printResponse(response, "GET Users List Response");
        validateResponseTime(response, MAX_RESPONSE_TIME);
        validateStandardHeaders(response);
        
        // Additional validations
        List<Map<String, Object>> users = response.jsonPath().getList("data");
        logApiStep("Validating user data structure");
        
        SoftAssert softAssert = new SoftAssert();
        for (Map<String, Object> user : users) {
            softAssert.assertNotNull(user.get("id"), "User ID should not be null");
            softAssert.assertNotNull(user.get("email"), "User email should not be null");
            softAssert.assertTrue(user.get("email").toString().contains("@"), "Email should contain @");
            softAssert.assertNotNull(user.get("first_name"), "First name should not be null");
            softAssert.assertNotNull(user.get("last_name"), "Last name should not be null");
            softAssert.assertNotNull(user.get("avatar"), "Avatar URL should not be null");
        }
        softAssert.assertAll();
        
        logApiAssertion("GET users list validation completed", true);
    }
    
    /**
     * Test GET request for single user
     * Validates individual user data retrieval
     */
    @Test(priority = 2, description = "GET - Fetch single user by ID")
    public void testGetSingleUser() {
        logApiStep("Testing GET request to fetch single user");
        
        int userId = 2;
        Response response = givenRequest()
            .pathParam("id", userId)
            .when()
            .get(SINGLE_USER_ENDPOINT)
            .then()
            .statusCode(200)
            .contentType("application/json; charset=utf-8")
            .body("data.id", equalTo(userId))
            .body("data.email", notNullValue())
            .body("data.first_name", notNullValue())
            .body("data.last_name", notNullValue())
            .body("data.avatar", containsString("https://"))
            .body("support", hasKey("url"))
            .body("support", hasKey("text"))
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        printResponse(response, "GET Single User Response");
        validateResponseTime(response, MAX_RESPONSE_TIME);
        
        // Validate specific user data
        String email = response.jsonPath().getString("data.email");
        String firstName = response.jsonPath().getString("data.first_name");
        String lastName = response.jsonPath().getString("data.last_name");
        
        Assert.assertTrue(email.contains("@"), "Email should be valid format");
        Assert.assertFalse(firstName.isEmpty(), "First name should not be empty");
        Assert.assertFalse(lastName.isEmpty(), "Last name should not be empty");
        
        logApiAssertion("GET single user validation completed", true);
    }
    
    /**
     * Test GET request for non-existent user
     * Validates proper error handling
     */
    @Test(priority = 3, description = "GET - Handle non-existent user (404)")
    public void testGetNonExistentUser() {
        logApiStep("Testing GET request for non-existent user");
        
        int nonExistentUserId = 999;
        Response response = givenRequest()
            .pathParam("id", nonExistentUserId)
            .when()
            .get(SINGLE_USER_ENDPOINT)
            .then()
            .statusCode(404)
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        printResponse(response, "GET Non-Existent User Response");
        validateResponseTime(response, MAX_RESPONSE_TIME);
        
        // Verify empty response body for 404
        String responseBody = response.getBody().asString();
        Assert.assertEquals(responseBody, "{}", "Response body should be empty for 404");
        
        logApiAssertion("Non-existent user handling validated", true);
    }
    
    /**
     * Test POST request to create new user
     * Validates user creation and response data
     */
    @Test(priority = 4, description = "POST - Create new user")
    public void testCreateNewUser() {
        logApiStep("Testing POST request to create new user");
        
        Map<String, Object> newUser = createTestData(
            "name", "John Doe",
            "job", "Software Engineer"
        );
        
        Response response = givenRequest()
            .body(newUser)
            .when()
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(201)
            .contentType("application/json; charset=utf-8")
            .body("name", equalTo("John Doe"))
            .body("job", equalTo("Software Engineer"))
            .body("id", notNullValue())
            .body("createdAt", notNullValue())
            .body("createdAt", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z"))
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        printResponse(response, "POST Create User Response");
        validateResponseTime(response, MAX_RESPONSE_TIME);
        
        // Extract and validate created user data
        String createdId = response.jsonPath().getString("id");
        String createdAt = response.jsonPath().getString("createdAt");
        String name = response.jsonPath().getString("name");
        String job = response.jsonPath().getString("job");
        
        Assert.assertNotNull(createdId, "Created user should have an ID");
        Assert.assertNotNull(createdAt, "Created user should have creation timestamp");
        Assert.assertEquals(name, "John Doe", "Name should match request data");
        Assert.assertEquals(job, "Software Engineer", "Job should match request data");
        
        logApiAssertion("POST create user validation completed", true);
    }
    
    /**
     * Test POST request with additional user data
     * Validates creation with more complex data
     */
    @Test(priority = 5, description = "POST - Create user with additional data")
    public void testCreateUserWithAdditionalData() {
        logApiStep("Testing POST request with additional user data");
        
        Map<String, Object> userData = createTestData(
            "name", "Jane Smith",
            "job", "QA Engineer",
            "email", "jane.smith@example.com",
            "age", 28
        );
        
        Response response = givenRequest()
            .body(userData)
            .when()
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(201)
            .body("name", equalTo("Jane Smith"))
            .body("job", equalTo("QA Engineer"))
            .body("email", equalTo("jane.smith@example.com"))
            .body("age", equalTo(28))
            .body("id", notNullValue())
            .body("createdAt", notNullValue())
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        printResponse(response, "POST Create User with Additional Data Response");
        validateResponseTime(response, MAX_RESPONSE_TIME);
        
        logApiAssertion("POST create user with additional data completed", true);
    }
    
    /**
     * Test PUT request to update existing user
     * Validates complete user data update
     */
    @Test(priority = 6, description = "PUT - Update existing user")
    public void testUpdateUser() {
        logApiStep("Testing PUT request to update existing user");
        
        int userId = 2;
        Map<String, Object> updatedUser = createTestData(
            "name", "Updated Name",
            "job", "Senior Developer"
        );
        
        Response response = givenRequest()
            .pathParam("id", userId)
            .body(updatedUser)
            .when()
            .put(SINGLE_USER_ENDPOINT)
            .then()
            .statusCode(200)
            .contentType("application/json; charset=utf-8")
            .body("name", equalTo("Updated Name"))
            .body("job", equalTo("Senior Developer"))
            .body("updatedAt", notNullValue())
            .body("updatedAt", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z"))
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        printResponse(response, "PUT Update User Response");
        validateResponseTime(response, MAX_RESPONSE_TIME);
        
        // Validate update timestamp
        String updatedAt = response.jsonPath().getString("updatedAt");
        Assert.assertNotNull(updatedAt, "Updated user should have update timestamp");
        
        logApiAssertion("PUT update user validation completed", true);
    }
    
    /**
     * Test PATCH request for partial user update
     * Validates partial data modification
     */
    @Test(priority = 7, description = "PATCH - Partial update of user")
    public void testPartialUpdateUser() {
        logApiStep("Testing PATCH request for partial user update");
        
        int userId = 2;
        Map<String, Object> partialUpdate = createTestData("job", "Lead QA Engineer");
        
        Response response = givenRequest()
            .pathParam("id", userId)
            .body(partialUpdate)
            .when()
            .patch(SINGLE_USER_ENDPOINT)
            .then()
            .statusCode(200)
            .contentType("application/json; charset=utf-8")
            .body("job", equalTo("Lead QA Engineer"))
            .body("updatedAt", notNullValue())
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        printResponse(response, "PATCH Partial Update Response");
        validateResponseTime(response, MAX_RESPONSE_TIME);
        
        logApiAssertion("PATCH partial update validation completed", true);
    }
    
    /**
     * Test DELETE request to remove user
     * Validates user deletion
     */
    @Test(priority = 8, description = "DELETE - Remove user")
    public void testDeleteUser() {
        logApiStep("Testing DELETE request to remove user");
        
        int userId = 2;
        Response response = givenRequest()
            .pathParam("id", userId)
            .when()
            .delete(SINGLE_USER_ENDPOINT)
            .then()
            .statusCode(204)
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        printResponse(response, "DELETE User Response");
        validateResponseTime(response, MAX_RESPONSE_TIME);
        
        // Verify empty response body for successful deletion
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.isEmpty() || responseBody.equals(""), 
            "Response body should be empty for successful deletion");
        
        logApiAssertion("DELETE user validation completed", true);
    }
    
    /**
     * Test API response time performance
     * Validates all endpoints respond within acceptable time limits
     */
    @Test(priority = 9, description = "Performance test for API response times")
    public void testApiPerformance() {
        logApiStep("Testing API performance across endpoints");
        
        long startTime = System.currentTimeMillis();
        
        // Test multiple endpoints performance
        Response getUsersResponse = givenRequest()
            .queryParam("page", 1)
            .when()
            .get(USERS_ENDPOINT);
        
        Response getSingleUserResponse = givenRequest()
            .pathParam("id", 1)
            .when()
            .get(SINGLE_USER_ENDPOINT);
        
        Response createUserResponse = givenRequest()
            .body(createTestData("name", "Performance Test", "job", "Tester"))
            .when()
            .post(USERS_ENDPOINT);
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        // Validate individual response times
        validateResponseTime(getUsersResponse, 2000);
        validateResponseTime(getSingleUserResponse, 2000);
        validateResponseTime(createUserResponse, 2000);
        
        // Validate total time for all operations
        Assert.assertTrue(totalTime < 10000, 
            String.format("Total API operations should complete within 10 seconds, took %d ms", totalTime));
        
        logApiStep(String.format("Performance test completed in %d ms", totalTime));
        logApiAssertion("API performance validation completed", true);
    }
    
    /**
     * Test API with invalid data
     * Validates error handling for malformed requests
     */
    @Test(priority = 10, description = "Test API error handling with invalid data")
    public void testApiErrorHandling() {
        logApiStep("Testing API error handling with invalid data");
        
        // Test with invalid JSON
        Response invalidJsonResponse = givenRequest()
            .body("{invalid json}")
            .when()
            .post(USERS_ENDPOINT)
            .then()
            .statusCode(400)
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        printResponse(invalidJsonResponse, "Invalid JSON Response");
        
        // Test with empty body
        Response emptyBodyResponse = givenRequest()
            .body("")
            .when()
            .post(USERS_ENDPOINT)
            .then()
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        printResponse(emptyBodyResponse, "Empty Body Response");
        
        logApiAssertion("API error handling validation completed", true);
    }
    
    /**
     * Test API pagination functionality
     * Validates proper pagination handling
     */
    @Test(priority = 11, description = "Test API pagination")
    public void testApiPagination() {
        logApiStep("Testing API pagination functionality");
        
        // Test first page
        Response firstPageResponse = givenRequest()
            .queryParam("page", 1)
            .queryParam("per_page", 3)
            .when()
            .get(USERS_ENDPOINT)
            .then()
            .statusCode(200)
            .body("page", equalTo(1))
            .body("per_page", equalTo(3))
            .body("data.size()", equalTo(3))
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        // Test second page
        Response secondPageResponse = givenRequest()
            .queryParam("page", 2)
            .queryParam("per_page", 3)
            .when()
            .get(USERS_ENDPOINT)
            .then()
            .statusCode(200)
            .body("page", equalTo(2))
            .body("per_page", equalTo(3))
            .time(lessThan(MAX_RESPONSE_TIME))
            .extract().response();
        
        // Validate pagination data consistency
        int firstPageTotal = firstPageResponse.jsonPath().getInt("total");
        int secondPageTotal = secondPageResponse.jsonPath().getInt("total");
        
        Assert.assertEquals(firstPageTotal, secondPageTotal, 
            "Total count should be consistent across pages");
        
        printResponse(firstPageResponse, "First Page Response");
        printResponse(secondPageResponse, "Second Page Response");
        
        logApiAssertion("API pagination validation completed", true);
    }
}