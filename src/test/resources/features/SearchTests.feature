Feature: Search and filter tests
  In order to quickly find the tests that interest me
  As a platform user
  I want to search tests by keyword, filter them and sort them by popularity

  Background:
    Given these catalog tests exist
      | title                   | subject   | type     | difficulty | tags         | status    | public | completed attempts | unfinished attempts |
      | Àlgebra lineal bàsica   | Maths     | OFFICIAL | EASY       | algebra,exam | PUBLISHED | true   | 1                  | 0                   |
      | Advanced algebra drills | Maths     | CUSTOM   | HARD       | algebra      | PUBLISHED | true   | 3                  | 0                   |
      | SQL joins practice      | Databases | CUSTOM   | MEDIUM     | sql,joins    | PUBLISHED | true   | 5                  | 0                   |
      | SQL final exam 2025     | Databases | OFFICIAL | HARD       | sql,exam     | PUBLISHED | true   | 0                  | 9                   |
      | Draft algebra test      | Maths     | CUSTOM   | EASY       | algebra      | DRAFT     | false  | 0                  | 0                   |
      | Private SQL notes       | Databases | CUSTOM   | EASY       | sql          | PUBLISHED | false  | 0                  | 0                   |

  Scenario: A visitor searches by keyword, ignoring case and accents
    Given I'm not logged in
    When I search the catalog with "text=ALGEBRA"
    Then The response code is 200
    And The catalog results are "Àlgebra lineal bàsica, Advanced algebra drills"
    When I search the catalog with "text=bàsica"
    Then The catalog results are "Àlgebra lineal bàsica"

  Scenario: Only published public tests are listed
    Given I login as "demo" with password "password"
    When I search the catalog with ""
    Then The catalog results are "Àlgebra lineal bàsica, Advanced algebra drills, SQL joins practice, SQL final exam 2025"

  Scenario: Filters are combined, and only tests matching all of them are listed
    Given I'm not logged in
    When I search the catalog with "subject=Databases&type=OFFICIAL&difficulty=HARD"
    Then The catalog results are "SQL final exam 2025"
    When I search the catalog with "text=algebra&difficulty=EASY"
    Then The catalog results are "Àlgebra lineal bàsica"

  Scenario: A test matches the tag filter if it has any of the selected tags
    Given I'm not logged in
    When I search the catalog with "tags=JOINS&tags=exam"
    Then The catalog results are "Àlgebra lineal bàsica, SQL joins practice, SQL final exam 2025"

  Scenario: Sorting by popularity orders by completed attempts, descending
    Given I'm not logged in
    When I search the catalog by popularity with ""
    Then The catalog results in order are "SQL joins practice, Advanced algebra drills, Àlgebra lineal bàsica, SQL final exam 2025"
    When I search the catalog by popularity with "subject=Maths"
    Then The catalog results in order are "Advanced algebra drills, Àlgebra lineal bàsica"

  Scenario: No matching test returns an empty result
    Given I'm not logged in
    When I search the catalog with "text=chemistry"
    Then The response code is 200
    And The catalog has no results

  Scenario: Tags are normalized and limited to five
    Given I login as "demo" with password "password"
    When I create a test "Normalization test" in subject "Databases" with difficulty "MEDIUM" and tags " SQL ,sql,Joins"
    Then The response code is 201
    And The test "Normalization test" has difficulty "MEDIUM" and tags "sql,joins"
    When I create a test "Too many tags" in subject "Databases" with difficulty "EASY" and tags "a,b,c,d,e,f"
    Then The response code is 400
    And The error message is "A test can have at most 5 tags"
