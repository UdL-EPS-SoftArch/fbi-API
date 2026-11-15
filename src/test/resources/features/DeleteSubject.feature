Feature: Delete or archive a subject
  In order to remove subjects that are no longer part of the academic path without losing results
  As an administrator
  I want to delete a subject without tests, or archive it if it already has content

  Scenario: Administrator deletes a subject without tests
    Given There is a subject with name "Ancient History"
    And I login as "admin" with password "password"
    When I delete the subject "Ancient History"
    Then The response code is 204
    And There is no subject named "Ancient History"

  Scenario: A subject with tests cannot be deleted, it has to be archived instead
    Given There is a subject with name "Compilers"
    And The subject "Compilers" has a test "Parsing Exam"
    And I login as "admin" with password "password"
    When I delete the subject "Compilers"
    Then The response code is 409
    And The error message is "The subject has tests or results, archive it instead"
    And The subject "Compilers" exists and is active

  Scenario: An archived subject is hidden but keeps its tests and leaderboard
    Given There is a subject with name "Databases"
    And The subject "Databases" has a test "SQL Exam" and a leaderboard entry of 70 points for "demo"
    And I login as "admin" with password "password"
    When I archive the subject "Databases"
    Then The response code is 200
    And The subject "Databases" is archived
    And The subject "Databases" does not appear in the subjects list
    And The subject "Databases" does not appear when searching for "databa"
    And The subject "Databases" keeps its test "SQL Exam" and its leaderboard entry of 70 points

  Scenario: An archived subject can be restored by the administrator
    Given There is an archived subject with name "Robotics"
    And I login as "admin" with password "password"
    And The logged in user sees the subject "Robotics" in the subjects list
    When I restore the subject "Robotics"
    Then The response code is 200
    And The subject "Robotics" exists and is active
    And The subject "Robotics" appears in the subjects list

  Scenario: Cannot create a test in an archived subject
    Given There is an archived subject with name "Electronics"
    And I login as "demo" with password "password"
    When I create a test "Circuits Quiz" in the subject "Electronics"
    Then The response code is 409
    And The error message is "Tests cannot be created in an archived subject"

  Scenario Outline: A student cannot <action> a subject
    Given There is a subject with name "Geology"
    And I login as "demo" with password "password"
    When I <action> the subject "Geology"
    Then The response code is 403
    And The subject "Geology" exists and is active

    Examples:
      | action  |
      | delete  |
      | archive |
