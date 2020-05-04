Feature: TradeMe Sandbox Tests

  Scenario: Return how many named brands of used car are available in the TradeMe Used Cars Category
    When I send a GET request to retrieve used car makes
    Then I assert that the response code is  200
    And I print the number of makes returned to the console

  Scenario: Check that the brand ‘Kia’ exists and return the current number of Kia cars listed
    When I send a GET request to retrieve used car makes
    Then I assert that the response code is  200
    And I assert that the brand 'Kia' exists
    Then I print the current number of Kia cars listed

  Scenario: Check that the brand "Hispano Suiza" does not exist
    When I send a GET request to retrieve used car makes
    Then I assert that the response code is  200
    And I assert that the make "Hispano Suiza" does not exist
