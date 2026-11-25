@us-est-19
Feature: Define question scoring
  In order to calculate the final result correctly
  As the author of a test
  I want to assign a score of 0 or more to each question

  Background:
    Given There is a subject named "Databases"
    And I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Scored test" in subject "Databases"

  Scenario: A question without an explicit score is worth 1 point
    When I add a question to test "Scored test" without a score
    Then The response code is 201
    And Question 1 of test "Scored test" has a score of 1
    And The test "Scored test" has a maximum score of 1

  Scenario: The maximum score is updated when a question score changes
    Given I add a question to test "Scored test" worth 2 points with 2 options, 1 of them correct
    And I add a question to test "Scored test" worth 3 points with 2 options, 1 of them correct
    When I PATCH the score of question 1 of test "Scored test" to 5
    Then The response code is 200
    And The test "Scored test" has a maximum score of 8

  Scenario: A question with 0 points is not scored
    Given I add a question to test "Scored test" worth 2 points with 2 options, 1 of them correct
    When I add a question to test "Scored test" with a score of 0
    Then The response code is 201
    And Question 2 of test "Scored test" is shown as not scored
    And The test "Scored test" has a maximum score of 2

  Scenario: A negative score is rejected when creating a question
    When I add a question to test "Scored test" with a score of -1
    Then The response code is 400
    And The test "Scored test" has a maximum score of 0

  Scenario Outline: A negative score is rejected when editing a question
    Given I add a question to test "Scored test" worth 2 points with 2 options, 1 of them correct
    When I <method> the score of question 1 of test "Scored test" to -1
    Then The response code is 400
    And Question 1 of test "Scored test" has a score of 2
    And The test "Scored test" has a maximum score of 2
    Examples:
      | method |
      | PUT    |
      | PATCH  |
