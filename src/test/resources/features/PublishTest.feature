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
    And The error message is "Question 1 needs at least two answer options"

  Scenario: A question without a correct answer is reported by its number
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 1 of them correct
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 0 of them correct
    When I publish the test "Practice SQL" with public visibility "true"
    Then The response code is 400
    And The error message is "Question 2 has no correct answer"

  Scenario: A question with several correct answers is reported by its number
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 3 options, 2 of them correct
    When I publish the test "Practice SQL" with public visibility "true"
    Then The response code is 400
    And The error message is "Question 1 has more than one correct answer"

  Scenario: The publication check lists every requirement as passed or failed
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 1 of them correct
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 0 of them correct
    When I open the publication check of test "Practice SQL"
    Then The response code is 200
    And The publication check is not publishable
    And The requirement "Question 1 has exactly one correct answer" is passed
    And The requirement "Question 2 has no correct answer" is failed and points to question 2

  Scenario: A refused publication returns the same checklist as the publication check
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 1 options, 0 of them correct
    When I publish the test "Practice SQL" with public visibility "true"
    Then The response code is 400
    And The publication check is not publishable
    And The requirement "Question 1 needs at least two answer options" is failed and points to question 1
    And The requirement "Question 1 has no correct answer" is failed and points to question 1

  Scenario: A missing question is reported on the questions field of the test
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    When I open the publication check of test "Practice SQL"
    Then The requirement "A test needs at least one question to be published" is failed and points to the field "questions"

  Scenario: The publication check of someone else's draft is not found
    Given There is a registered user with username "other" and password "password" and email "other@sample.app"
    And I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I login as "other" with password "password"
    When I open the publication check of test "Practice SQL"
    Then The response code is 404

  Scenario: The publication check requires authentication
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I'm not logged in
    When I open the publication check of test "Practice SQL"
    Then The response code is 401

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
    And The requirement "A published official test must be public" is failed and points to the field "isPublic"
