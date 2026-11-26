@us-com-05
Feature: View test details
  In order to understand a test before deciding whether to take it
  As a platform user (visitor, student or administrator)
  I want to view the public information of a test

  Background:
    Given There is a registered user with username "student" and password "password" and email "student@sample.app"
    And these tests exist in the platform
      | title            | subject   | type     | status    | public | questions | year | difficulty | tags          |
      | Databases exam   | Databases | OFFICIAL | PUBLISHED | true   | 3         | 2025 | HARD       | sql, joins    |
      | SQL practice     | Databases | CUSTOM   | PUBLISHED | true   | 2         |      | EASY       | sql           |
      | Draft test       | Databases | CUSTOM   | DRAFT     | false  | 1         |      | MEDIUM     |               |
      | Withdrawn exam   | Databases | OFFICIAL | WITHDRAWN | true   | 1         | 2023 | MEDIUM     |               |
      | Private practice | Databases | CUSTOM   | PUBLISHED | false  | 1         |      | EASY       | private       |

  Scenario Outline: A visitor sees the public details of a published test
    Given I'm not logged in
    When I open the detail page of the test "<title>"
    Then The response code is 200
    And I see these details of the test
      | title   | subject   | year   | type   | difficulty   | tags   | questions   | author   |
      | <title> | Databases | <year> | <type> | <difficulty> | <tags> | <questions> | <author> |
    Examples:
      | title          | year | type     | difficulty | tags       | questions | author |
      | Databases exam | 2025 | OFFICIAL | HARD       | sql, joins | 3        |        |
      | SQL practice   |      | CUSTOM   | EASY       | sql        | 2         | demo   |

  Scenario: The details never reveal questions or answers
    Given I login as "student" with password "password"
    When I open the detail page of the test "Databases exam"
    Then The response code is 200
    And The test details do not include questions, answer options or correct answers

  Scenario: A visitor needs an account to take a test
    Given I'm not logged in
    When I try to take the test "Databases exam"
    Then The response code is 401

  Scenario Outline: Draft and withdrawn tests are not available
    Given I login as "student" with password "password"
    When I open the detail page of the test "<title>"
    Then The response code is 404
    Examples:
      | title          |
      | Draft test     |
      | Withdrawn exam |

  Scenario: A privately published test is shown to logged-in users through its direct link
    Given I login as "student" with password "password"
    When I open the detail page of the test "Private practice"
    Then The response code is 200
    And I see these details of the test
      | title            | subject   | year | type   | difficulty | tags    | questions | author |
      | Private practice | Databases |      | CUSTOM | EASY       | private | 1         | demo   |

  Scenario: A privately published test is not shown to visitors
    Given I'm not logged in
    When I open the detail page of the test "Private practice"
    Then The response code is 404
