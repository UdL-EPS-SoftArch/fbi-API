Feature: Explore the public test catalog
  In order to discover the available content
  As a platform user
  I want to explore the catalog of published, publicly visible tests

  Scenario: The catalog lists the published tests with their details
    Given these tests exist in the platform
      | title              | subject   | type     | status    | public | questions | year |
      | Databases exam     | Databases | OFFICIAL | PUBLISHED | true   | 3         | 2025 |
      | SQL practice       | Databases | CUSTOM   | PUBLISHED | true   | 2         |      |
      | Algebra basics     | Maths     | CUSTOM   | PUBLISHED | true   | 1         | 2024 |
    And I'm not logged in
    When I open the catalog
    Then The response code is 200
    And The catalog lists these tests
      | title          | subject   | type     | questions | year |
      | Algebra basics | Maths     | CUSTOM   | 1         | 2024 |
      | Databases exam | Databases | OFFICIAL | 3         | 2025 |
      | SQL practice   | Databases | CUSTOM   | 2         |      |

  Scenario: Draft, withdrawn and privately published tests are not listed, not even for their author
    Given these tests exist in the platform
      | title            | subject   | type     | status    | public | questions | year |
      | Visible test     | Databases | CUSTOM   | PUBLISHED | true   | 1         |      |
      | Draft test       | Databases | CUSTOM   | DRAFT     | false  | 1         |      |
      | Withdrawn exam   | Databases | OFFICIAL | WITHDRAWN | true   | 1         | 2023 |
      | Private practice | Databases | CUSTOM   | PUBLISHED | false  | 1         |      |
    And I login as "demo" with password "password"
    When I open the catalog
    Then The catalog lists these tests
      | title        | subject   | type   | questions | year |
      | Visible test | Databases | CUSTOM | 1         |      |

  Scenario: The catalog is paginated
    Given these tests exist in the platform
      | title  | subject   | type   | status    | public | questions | year |
      | Test A | Databases | CUSTOM | PUBLISHED | true   | 1         |      |
      | Test B | Databases | CUSTOM | PUBLISHED | true   | 1         |      |
      | Test C | Databases | CUSTOM | PUBLISHED | true   | 1         |      |
    And I'm not logged in
    When I open page 0 of the catalog with 2 tests per page
    Then The catalog has 3 tests in 2 pages
    And The catalog page lists "Test A, Test B"
    When I open page 1 of the catalog with 2 tests per page
    Then The catalog page lists "Test C"

  Scenario: Selecting a test in the catalog leads to its public detail page
    Given these tests exist in the platform
      | title          | subject   | type     | status    | public | questions | year |
      | Databases exam | Databases | OFFICIAL | PUBLISHED | true   | 3         | 2025 |
    And I'm not logged in
    When I open the catalog
    And I select the catalog test "Databases exam"
    Then The response code is 200
    And I see the details of the test "Databases exam"

  Scenario: An empty catalog returns no tests
    Given I'm not logged in
    When I open the catalog
    Then The response code is 200
    And The catalog has no results
