Feature: Enforce permissions on the server
  In order to stop anyone from bypassing the interface to cheat or read other users' data
  As a student
  I want the server to check the role and ownership of every request

  Background:
    Given There is a registered user with username "marquinhos_malote69" and password "password" and email "alice@sample.app"
    And There is a published official test "Exam 2024" with one question
    And "demo" has a "IN_PROGRESS" attempt with one answer on test "Exam 2024"

  Scenario Outline: Visitors cannot read an attempt or its answer
    Given I'm not logged in
    When I retrieve the "<resource>" of that attempt
    Then The response code is 401
    Examples:
      | resource |
      | attempt  |
      | answer   |

  Scenario Outline: Only the owner and administrators can read an attempt by its id
    Given I login as "<user>" with password "password"
    When I retrieve the "<resource>" of that attempt
    Then The response code is <status>
    Examples:
      | user                | resource | status |
      | demo                | attempt  | 200    |
      | marquinhos_malote69 | attempt  | 404    |
      | admin               | attempt  | 200    |
      | demo                | answer   | 200    |
      | marquinhos_malote69 | answer   | 404    |
      | admin               | answer   | 200    |

  Scenario Outline: The owner of an attempt cannot be reassigned
    Given I login as "<user>" with password "password"
    When I link the user of that attempt to "marquinhos_malote69"
    Then The response code is 403
    Examples:
      | user                |
      | demo                |
      | marquinhos_malote69 |
      | admin               |

  Scenario Outline: The author of a test cannot be reassigned
    Given I login as "<user>" with password "password"
    When I link the author of test "Exam 2024" to "marquinhos_malote69"
    Then The response code is 403
    Examples:
      | user                |
      | demo                |
      | marquinhos_malote69 |
      | admin               |

  Scenario: A student cannot set their own score
    Given I login as "demo" with password "password"
    When I change the score of that attempt to 100
    Then That attempt has no score

  Scenario: A student cannot promote themselves to administrator
    Given I login as "demo" with password "password"
    When I change my role to "ADMIN"
    Then The account "demo" has role "STUDENT" and email "demo@sample.app"

  Scenario: A student cannot award themselves a badge
    Given I login as "demo" with password "password"
    When I award the user "demo" a badge
    Then The response code is 403

  Scenario Outline: Only administrators can edit an official test
    Given I login as "<user>" with password "password"
    When I change the title of test "Exam 2024" to "Hacked"
    Then The response code is <status>
    Examples:
      | user                | status |
      | demo                | 403    |
      | marquinhos_malote69 | 403    |
      | admin               | 200    |
