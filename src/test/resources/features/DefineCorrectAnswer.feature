Feature: Define the correct answer
  In order to grade tests automatically
  As a test author
  I want to mark exactly one option of each question as the correct answer

  Background:
    Given There is a subject named "Databases"
    And There is a registered user with username "student" and password "password" and email "student@sample.app"

  Scenario: The author marks an option as correct
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 3 options, 0 of them correct
    When I mark option 2 of question 1 of test "Practice SQL" as correct
    Then The response code is 200
    And Only option 2 of question 1 of test "Practice SQL" is correct

  Scenario: Marking another option unmarks the previous one
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 3 options, 1 of them correct
    When I mark option 3 of question 1 of test "Practice SQL" as correct
    Then The response code is 200
    And Only option 3 of question 1 of test "Practice SQL" is correct

  Scenario: Adding a new correct option unmarks the previous one
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 1 of them correct
    When I add a correct option to question 1 of test "Practice SQL"
    Then The response code is 201
    And Only option 3 of question 1 of test "Practice SQL" is correct

  Scenario: Marking an option does not change other questions
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 1 of them correct
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 1 of them correct
    When I mark option 2 of question 1 of test "Practice SQL" as correct
    Then Only option 2 of question 1 of test "Practice SQL" is correct
    And Only option 1 of question 2 of test "Practice SQL" is correct

  Scenario: A draft can have a question without a correct answer but cannot be published
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 0 of them correct
    When I publish the test "Practice SQL" with public visibility "true"
    Then The response code is 400
    And The error message is "must have exactly one correct option"
    And The test "Practice SQL" has status "DRAFT"

  Scenario: The author sees which option is correct
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 1 of them correct
    When I retrieve option 1 of question 1 of test "Practice SQL" with the answer key
    Then The response code is 200
    And The retrieved option is shown as correct

  Scenario Outline: Students never see the correct option
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 1 of them correct
    And I publish the test "Practice SQL" with public visibility "true"
    And I login as "student" with password "password"
    When I retrieve option 1 of question 1 of test "Practice SQL" <view>
    Then The response code is 200
    And The retrieved option does not reveal whether it is correct
    Examples:
      | view                |
      | without answer key  |
      | with the answer key |

  Scenario Outline: Visitors cannot read answer options
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 1 of them correct
    And I publish the test "Practice SQL" with public visibility "true"
    And I'm not logged in
    When I retrieve option 1 of question 1 of test "Practice SQL" <view>
    Then The response code is 401
    Examples:
      | view                |
      | without answer key  |
      | with the answer key |

  Scenario Outline: Only the author or an administrator can mark the correct option
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Practice SQL" in subject "Databases"
    And I add a question to test "Practice SQL" worth 1 points with 2 options, 1 of them correct
    And I publish the test "Practice SQL" with public visibility "true"
    And <login>
    When I mark option 2 of question 1 of test "Practice SQL" as correct
    Then The response code is <status>
    And Only option <correct> of question 1 of test "Practice SQL" is correct
    Examples:
      | login                                          | status | correct |
      | I login as "admin" with password "password"   | 200    | 2       |
      | I login as "student" with password "password" | 403    | 1       |
      | I'm not logged in                              | 401    | 1       |
