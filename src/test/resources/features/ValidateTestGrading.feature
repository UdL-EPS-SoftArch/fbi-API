@validate-test-grading
Feature: Validate test grading
  Background:
    Given There is a subject named "Grading"
    And There is a registered user with username "previewStudent" and password "password" and email "previewStudent@sample.app"

  Scenario Outline: An author grades a preview using the same rules as a real attempt
    Given I login as "<author>" with password "password"
    And I have a draft "<type>" test "Grading test" in subject "Grading"
    And I add a question to test "Grading test" worth 2 points with 2 options, 1 of them correct
    And I add a question to test "Grading test" worth 3 points with 2 options, 1 of them correct
    And I add a question to test "Grading test" worth 5 points with 2 options, 1 of them correct
    When I submit a grading preview of "Grading test"
    Then The response code is 201
    And The preview score is 2 and no attempt or answer was saved
    When I publish the test "Grading test" with public visibility "true"
    Then The response code is 200
    When I submit the same answers in a real attempt on "Grading test" as "previewStudent"
    Then The real attempt has the preview score
    Examples:
      | author | type     |
      | demo   | CUSTOM   |
      | admin  | OFFICIAL |

  Scenario: The author reads the answer key and maximum score
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Key test" in subject "Grading"
    And I add a question to test "Key test" worth 2 points with 2 options, 1 of them correct
    And I add a question to test "Key test" worth 3 points with 2 options, 1 of them correct
    And I add a question to test "Key test" worth 5 points with 2 options, 1 of them correct
    Then The answer key for "Key test" shows scores 2, 3 and 5 and maximum 10

  Scenario: A question without a correct answer remains visible in the answer key
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Incomplete key" in subject "Grading"
    And I add a question to test "Incomplete key" worth 2 points with 2 options, 0 of them correct
    Then The answer key for "Incomplete key" shows a question without a correct answer

  Scenario Outline: Another user cannot grade an author's preview
    Given I login as "demo" with password "password"
    And I have a draft "CUSTOM" test "Public grading" in subject "Grading"
    And I add a question to test "Public grading" worth 2 points with 2 options, 1 of them correct
    And I add a question to test "Public grading" worth 3 points with 2 options, 1 of them correct
    And I publish the test "Public grading" with public visibility "true"
    And I login as "<user>" with password "password"
    When I submit a grading preview of "Public grading"
    Then The response code is 403
    Examples:
      | user           |
      | previewStudent |
      | admin          |
