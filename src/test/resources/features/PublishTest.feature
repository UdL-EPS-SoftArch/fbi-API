Feature: Publish a test
  In order to offer only usable tests
  As a test author
  I want a test to be validated before it is published and its maximum score kept up to date

  Background:
    Given There is a subject named "Databases"

  Scenario: The maximum score is the sum of the question scores
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    When I add a question to test "Practice SQL" worth 2 points with 2 options, 1 of them correct
    And I add a question to test "Practice SQL" worth 3 points with 2 options, 1 of them correct
    Then The test "Practice SQL" has a maximum score of 5

  Scenario: A test without questions cannot be published
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    When I publish the test "Practice SQL" with public visibility "true"
    Then The response code is 400
    And The error message is "A test needs at least one question to be published"
    And The test "Practice SQL" has status "DRAFT"

  Scenario: A question needs at least two options to be published
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 1 options, 1 of them correct
    When I publish the test "Practice SQL" with public visibility "true"
    Then The response code is 400
    And The error message is "needs at least two answer options"

  Scenario: A question needs exactly one correct option to be published
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 3 options, 2 of them correct
    When I publish the test "Practice SQL" with public visibility "true"
    Then The response code is 400
    And The error message is "must have exactly one correct option"

  Scenario: A valid custom test can be published
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 1 of them correct
    When I publish the test "Practice SQL" with public visibility "true"
    Then The response code is 200
    And The test "Practice SQL" has status "PUBLISHED"

  Scenario: A published official test must be public
    Given I login as "admin" with password "password"
    And I have a draft "OFFICIAL" test "Exam 2025" in subject "Databases"
    And I add a question to test "Exam 2025" worth 1 points with 2 options, 1 of them correct
    When I publish the test "Exam 2025" with public visibility "false"
    Then The response code is 400
    And The error message is "A published official test must be public"
