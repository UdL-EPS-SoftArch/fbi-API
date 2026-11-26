@US-COM-06
Feature: View a subject leaderboard through the API
  Background:
    Given subject leaderboard results exist

  Scenario Outline: The leaderboard is public and contains eligible subject scores
    Given the catalogue requester is "<role>"
    When I request subject leaderboard page 0 of size 10
    Then The response code is 200
    And the subject leaderboard page contains
      | username | rank | score |
      | demo     | 1    | 100   |
      | test     | 2    | 80    |
      | ztie     | 2    | 80    |
    And the subject leaderboard total is 3
    Examples:
      | role    |
      | visitor |
      | student |
      | admin   |

  Scenario: A tied position remains stable across pages
    Given I'm not logged in
    When I request subject leaderboard page 0 of size 2
    Then the subject leaderboard page contains
      | username | rank | score |
      | demo     | 1    | 100   |
      | test     | 2    | 80    |
    And the subject leaderboard total is 3
    When I request subject leaderboard page 1 of size 2
    Then the subject leaderboard page contains
      | username | rank | score |
      | ztie     | 2    | 80    |
    And the subject leaderboard total is 3

  Scenario: A student finds their own position outside the visible page
    Given I login as "test" with password "password"
    When I request subject leaderboard page 0 of size 1
    Then the subject leaderboard page contains
      | username | rank | score |
      | demo     | 1    | 100   |
    When I request my subject leaderboard position using my identity
    Then my subject leaderboard position is 2 with score 80

  Scenario: Fewer attempts break a score tie
    Given student "test" needed two attempts to reach 80 points
    And I'm not logged in
    When I request subject leaderboard page 0 of size 10
    Then the subject leaderboard page contains
      | username | rank | score |
      | demo     | 1    | 100   |
      | ztie     | 2    | 80    |
      | test     | 3    | 80    |

  Scenario: Lower duration breaks a score and attempt tie
    Given student "test" needed 60 seconds to reach 80 points
    And I'm not logged in
    When I request subject leaderboard page 0 of size 10
    Then the subject leaderboard page contains
      | username | rank | score |
      | demo     | 1    | 100   |
      | ztie     | 2    | 80    |
      | test     | 3    | 80    |

  @US-COM-11
  Scenario: Withdrawing an official test removes its scores and republishing restores them
    Given I login as "admin" with password "password"
    When the administrator sets the official subject test to "WITHDRAWN"
    Then The response code is 200
    When I request subject leaderboard page 0 of size 10
    Then the subject leaderboard total is 0
    When the administrator sets the official subject test to "PUBLISHED"
    Then The response code is 200
    When I request subject leaderboard page 0 of size 10
    Then the subject leaderboard page contains
      | username | rank | score |
      | demo     | 1    | 100   |
      | test     | 2    | 80    |
      | ztie     | 2    | 80    |

  Scenario: A subject without results has an empty leaderboard
    Given I'm not logged in
    When I request the empty subject leaderboard
    Then The response code is 200
    And the subject leaderboard total is 0
    And the subject leaderboard is empty
