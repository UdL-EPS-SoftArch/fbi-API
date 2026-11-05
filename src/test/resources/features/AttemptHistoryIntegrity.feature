Feature: Attempt history integrity
  In order to keep scores, rankings and history reliable
  As a student
  I cannot delete my submitted attempts or their answers

  Background:
    Given There is a published official test "Exam 2024" with one question

  Scenario: A student cannot delete a submitted attempt
    Given "demo" has a "SUBMITTED" attempt with one answer on test "Exam 2024"
    And I login as "demo" with password "password"
    When I delete that attempt
    Then The response code is 403
    And The attempt and its answer still exist

  Scenario: A student cannot delete an answer of a submitted attempt
    Given "demo" has a "SUBMITTED" attempt with one answer on test "Exam 2024"
    And I login as "demo" with password "password"
    When I delete the answer of that attempt
    Then The response code is 409
    And The attempt and its answer still exist

  Scenario: A student can clear an answer while the attempt is in progress
    Given "demo" has a "IN_PROGRESS" attempt with one answer on test "Exam 2024"
    And I login as "demo" with password "password"
    When I delete the answer of that attempt
    Then The response code is 200
