@US-COM-02
Feature: Discover home page content through the API

  Scenario: Visitors can discover the catalog and subjects at the API root
    Given I'm not logged in
    When I request the home resource "/"
    Then The response code is 200
    And the home response links to "tests" at "/tests"
    And the home response links to "subjects" at "/subjects"

  Scenario Outline: Visitors can read each home feed
    Given I'm not logged in
    When I request the home resource "<feed>"
    Then The response code is 200

    Examples:
      | feed                               |
      | /featuredTests             |
      | /leaderboardSummary |

  Scenario Outline: Signed-in users can read each home feed
    Given I login as "<username>" with password "password"
    When I request the home resource "<feed>"
    Then The response code is 200

    Examples:
      | username | feed                               |
      | demo     | /featuredTests             |
      | demo     | /leaderboardSummary |
      | admin    | /featuredTests             |
      | admin    | /leaderboardSummary |

  Scenario: The featured feed ranks public published tests by recent submissions
    Given these tests exist for the home feed
      | title           | subject               | type     | status    | public |
      | First tie       | Software Architecture | OFFICIAL | PUBLISHED | true   |
      | Second tie      | Software Architecture | CUSTOM   | PUBLISHED | true   |
      | Most taken      | Databases             | CUSTOM   | PUBLISHED | true   |
      | Old submission  | Databases             | OFFICIAL | PUBLISHED | true   |
      | Still taking    | Databases             | OFFICIAL | PUBLISHED | true   |
      | Abandoned test  | Databases             | OFFICIAL | PUBLISHED | true   |
      | Validation test | Databases             | OFFICIAL | PUBLISHED | true   |
      | Private test    | Databases             | CUSTOM   | PUBLISHED | false  |
      | Draft test      | Databases             | CUSTOM   | DRAFT     | true   |
      | Withdrawn test  | Databases             | OFFICIAL | WITHDRAWN | true   |
    And the same student has these attempts for the home feed
      | title           | type       | status      | days since start | days since submission | count |
      | First tie       | REGULAR    | SUBMITTED   | 3                | 2                     | 1     |
      | Second tie      | REGULAR    | SUBMITTED   | 3                | 2                     | 1     |
      | Most taken      | REGULAR    | SUBMITTED   | 32               | 29                    | 2     |
      | Old submission  | REGULAR    | SUBMITTED   | 33               | 31                    | 1     |
      | Still taking    | REGULAR    | IN_PROGRESS | 2                |                       | 1     |
      | Abandoned test  | REGULAR    | ABANDONED   | 2                |                       | 1     |
      | Validation test | VALIDATION | SUBMITTED   | 3                | 2                     | 1     |
      | Private test    | REGULAR    | SUBMITTED   | 3                | 2                     | 3     |
      | Draft test      | REGULAR    | SUBMITTED   | 3                | 2                     | 3     |
      | Withdrawn test  | REGULAR    | SUBMITTED   | 3                | 2                     | 3     |
    And I login as "admin" with password "password"
    When I request the home resource "/featuredTests"
    Then The response code is 200
    And the featured feed contains these tests in order
      | title      | subject               | type     |
      | Most taken | Databases             | CUSTOM   |
      | First tie  | Software Architecture | OFFICIAL |
      | Second tie | Software Architecture | CUSTOM   |

  Scenario: The featured feed is empty when no test has qualifying attempts
    Given these tests exist for the home feed
      | title              | subject   | type     | status    | public |
      | Published but idle | Databases | OFFICIAL | PUBLISHED | true   |
    And I'm not logged in
    When I request the home resource "/featuredTests"
    Then The response code is 200
    And the home collection "tests" is empty

  Scenario: The global leaderboard preview shows the five highest-ranked eligible students
    Given these saved rankings exist for the home feed
      | username    | role    | scope   | rank | score |
      | home-zara   | STUDENT | GLOBAL  | 6    | 55    |
      | home-amira  | STUDENT | GLOBAL  | 6    | 55    |
      | home-maria  | STUDENT | GLOBAL  | 3    | 80    |
      | home-pau    | STUDENT | GLOBAL  | 5    | 60    |
      | home-jana   | STUDENT | GLOBAL  | 2    | 90    |
      | home-joan   | STUDENT | GLOBAL  | 4    | 70    |
      | home-admin  | ADMIN   | GLOBAL  | 1    | 100   |
      | home-nuria  | STUDENT | SUBJECT | 1    | 99    |
    And I'm not logged in
    When I request the home resource "/leaderboardSummary"
    Then The response code is 200
    And the global summary contains these rankings in order
      | username   | rank | score |
      | home-jana  | 2    | 90    |
      | home-maria | 3    | 80    |
      | home-joan  | 4    | 70    |
      | home-pau   | 5    | 60    |
      | home-amira | 6    | 55    |
    And the home response links to "globalLeaderboard" at "/leaderboards/search/findByLeaderboardTypeOrderByRankAsc?type=GLOBAL"

  Scenario Outline: The global preview is empty when the only ranking is ineligible
    Given these saved rankings exist for the home feed
      | username     | role   | scope   | rank | score |
      | home-omitted | <role> | <scope> | 1    | 100   |
    And I'm not logged in
    When I request the home resource "/leaderboardSummary"
    Then The response code is 200
    And the home collection "leaderboards" is empty
    And the home response links to "globalLeaderboard" at "/leaderboards/search/findByLeaderboardTypeOrderByRankAsc?type=GLOBAL"

    Examples:
      | role    | scope   |
      | ADMIN   | GLOBAL  |
      | STUDENT | SUBJECT |

  Scenario: The tests collection retains its normal fields for a published test without an author
    Given these tests exist for the home feed
      | title          | subject   | type     | status    | public |
      | No author test | Databases | OFFICIAL | PUBLISHED | true   |
    And I'm not logged in
    When I request the home resource "/tests"
    Then The response code is 200
    And the tests collection includes "No author test" with its standard fields

  Scenario: The leaderboard collection retains its normal fields
    Given these saved rankings exist for the home feed
      | username     | role    | scope  | rank | score |
      | home-standard | STUDENT | GLOBAL | 1    | 100   |
    And I'm not logged in
    When I request the home resource "/leaderboards"
    Then The response code is 200
    And the leaderboard collection includes its standard fields

  Scenario Outline: A test without an author follows visibility rules
    Given these tests exist for the home feed
      | title         | subject   | type     | status    | public   |
      | No author     | Databases | OFFICIAL | <status>  | <public> |
    And the home requester is "<role>"
    When I request the home test "No author"
    Then The response code is <response>

    Examples:
      | status    | public | role    | response |
      | PUBLISHED | true   | visitor | 200      |
      | PUBLISHED | false  | visitor | 404      |
      | DRAFT     | true   | visitor | 404      |
      | DRAFT     | false  | admin   | 200      |

  Scenario: A title search includes a published public test without an author
    Given these tests exist for the home feed
      | title            | subject   | type     | status    | public |
      | Unique no author | Databases | OFFICIAL | PUBLISHED | true   |
    And I'm not logged in
    When I request the home resource "/tests/search/findByTitleContaining?text=Unique"
    Then The response code is 200
    And the tests collection includes "Unique no author" with its standard fields

  Scenario: The summary projection does not reveal an administrator username
    Given these saved rankings exist for the home feed
      | username    | role  | scope  | rank | score |
      | home-hidden | ADMIN | GLOBAL | 1    | 100   |
    And I'm not logged in
    When I request the home resource "/leaderboards?projection=leaderboardSummary"
    Then The response code is 200
    And the home collection "leaderboards" is empty
