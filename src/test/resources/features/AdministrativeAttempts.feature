@us-adm-15
Feature: Administrative attempts do not participate in competition
  Background:
    Given There is a subject named "Administrative scoring"
    And I login as "admin" with password "password"
    And I have a draft "OFFICIAL" test "Administrative exam" in subject "Administrative scoring"
    And I add a question to test "Administrative exam" worth 2 points with 2 options, 1 of them correct
    And I add a question to test "Administrative exam" worth 3 points with 2 options, 1 of them correct
    And I publish the test "Administrative exam" with public visibility "true"

  Scenario: An administrator receives a stored graded result without competing
    Given An active badge rewards completion of an official test
    When I submit the same answers in a real attempt on "Administrative exam" as "admin"
    Then The administrative attempt is graded and excluded from competition

  Scenario Outline: An administrator profile is not publicly accessible
    Given <viewer>
    When I retrieve the profile of user "admin"
    Then The response code is 404
    Examples:
      | viewer                                      |
      | I'm not logged in                           |
      | I login as "demo" with password "password"  |

  Scenario: A student cannot find an administrator by username
    Given I login as "demo" with password "password"
    When I search users whose username contains "admin"
    Then The user search returns 0 users without visible emails

  Scenario: A student cannot find an administrator by exact username
    Given I login as "demo" with password "password"
    When I search for the exact administrator username
    Then The response code is 404

  @US-COM-02
  Scenario: A saved administrator entry is omitted from the public leaderboard
    Given these saved rankings exist for the home feed
      | username   | role  | scope  | rank | score |
      | hidden-adm | ADMIN | GLOBAL | 1    | 100   |
    And I'm not logged in
    When I request the home resource "/leaderboards?projection=leaderboardSummary"
    Then The response code is 200
    And the home collection "leaderboards" is empty
