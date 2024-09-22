Feature: Checkout Page Validation

  Scenario: Validate Invalid Customer ID
    Given the user opens the checkout page
    When the user selects an invalid customer ID
    Then the system should display an error

  Scenario: Validate Currency Selection
    Given the user opens the checkout page
    When the user selects multiple currencies
    Then the selected currencies should be reflected correctly
