@cart @bdd @ui
Feature: Add Item to Cart
  As a customer
  I want to add items to my shopping cart
  So that I can purchase products I'm interested in

  Background:
    Given the user is on the login page
    And the user logs in with valid credentials

  @smoke @positive
  Scenario: Successfully add a single item to cart
    Given the user is on the products page
    When the user adds "Sauce Labs Backpack" to the cart
    Then the cart should contain 1 item
    And the product "Sauce Labs Backpack" should be marked as added to cart
    And the cart badge should display "1"

  @positive
  Scenario: Add multiple items to cart
    Given the user is on the products page
    When the user adds the following items to the cart:
      | Product Name               |
      | Sauce Labs Backpack        |
      | Sauce Labs Bike Light      |
      | Sauce Labs Bolt T-Shirt    |
    Then the cart should contain 3 items
    And the cart badge should display "3"
    And all selected products should be marked as added to cart

  @positive
  Scenario Outline: Add different products to cart
    Given the user is on the products page
    When the user adds "<productName>" to the cart
    Then the cart should contain 1 item
    And the product "<productName>" should be marked as added to cart
    And the cart badge should display "1"

    Examples:
      | productName                    |
      | Sauce Labs Backpack           |
      | Sauce Labs Bike Light         |
      | Sauce Labs Bolt T-Shirt       |
      | Sauce Labs Fleece Jacket      |
      | Sauce Labs Onesie             |
      | Test.allTheThings() T-Shirt (Red) |

  @positive
  Scenario: Add item and verify in cart page
    Given the user is on the products page
    When the user adds "Sauce Labs Fleece Jacket" to the cart
    And the user navigates to the cart page
    Then the cart should contain "Sauce Labs Fleece Jacket"
    And the cart total should show the correct price

  @positive
  Scenario: Add item after sorting products
    Given the user is on the products page
    When the user sorts products by "Price (low to high)"
    And the user adds the first product to the cart
    Then the cart should contain 1 item
    And the cart badge should display "1"

  @edge-case
  Scenario: Add item, remove it, then add again
    Given the user is on the products page
    When the user adds "Sauce Labs Onesie" to the cart
    Then the cart badge should display "1"
    When the user removes "Sauce Labs Onesie" from the cart
    Then the cart badge should not be displayed
    When the user adds "Sauce Labs Onesie" to the cart again
    Then the cart badge should display "1"

  @integration
  Scenario: Add items to cart and proceed to checkout
    Given the user is on the products page
    When the user adds "Sauce Labs Backpack" to the cart
    And the user adds "Sauce Labs Bike Light" to the cart
    And the user navigates to the cart page
    And the user proceeds to checkout
    Then the user should be on the checkout information page
    And the checkout should contain the added items

  @negative
  Scenario: Verify cart behavior when no items added
    Given the user is on the products page
    When the user navigates to the cart page without adding items
    Then the cart should be empty
    And the continue shopping button should be available
    And the checkout button should be disabled or not functional

  @performance
  Scenario: Add item to cart with performance measurement
    Given the user is on the products page
    When the user adds "Sauce Labs Backpack" to the cart and measures the time
    Then the add to cart operation should complete within 3 seconds
    And the cart should contain 1 item