Feature: Add Item to Cart

  As a SauceDemo user
  I want to log in and add an item to the cart
  So that I can purchase it successfully

  Scenario: Add a single item to the cart
    Given User is on the SauceDemo login page
    When User logs in with valid credentials
    And User adds the first product to the cart
    And User navigates to the cart page
    Then The product should be visible in the cart
