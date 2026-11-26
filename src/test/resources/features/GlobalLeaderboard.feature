@US-COM-07
Feature: View the global leaderboard through the API
  Background:
    Given global leaderboard results exist

  Scenario Outline: The current ranking is available to everyone
    Given the catalogue requester is "<role>"
    When I request global leaderboard page 0 of size 10
    Then The response code is 200
    And the global leaderboard contains
      | username | rank | score |
      | global-a | 1    | 100   |
      | global-b | 2    | 80    |
      | global-c | 2    | 80    |
    And the global leaderboard total is 3
    Examples:
      | role    |
      | visitor |
      | student |
      | admin   |

  Scenario: A shared rank remains stable across pages
    Given I'm not logged in
    When I request global leaderboard page 0 of size 2
    Then The response code is 200
    And the global leaderboard contains
      | username | rank | score |
      | global-a | 1    | 100   |
      | global-b | 2    | 80    |
    And the global leaderboard total is 3
    When I request global leaderboard page 1 of size 2
    Then The response code is 200
    And the global leaderboard contains
      | username | rank | score |
      | global-c | 2    | 80    |
    And the global leaderboard total is 3

  Scenario: Closed periods retain results after a test is withdrawn
    Given a closed period snapshot exists
    And I'm not logged in
    When I select the closed period after withdrawing an official test
    Then the original positions and scores are shown

  Scenario: An anonymous student keeps their position
    Given a student chooses leaderboard anonymity
    And I'm not logged in
    When I request global leaderboard page 0 of size 10
    Then the student is shown as "Anonymous student" without an identifying link

  Scenario: Anonymity also protects a closed period
    Given a closed period snapshot exists
    And a student chooses leaderboard anonymity after the period closes
    And I'm not logged in
    When I select the closed period after withdrawing an official test
    Then the student is shown as "Anonymous student" without an identifying link

  Scenario: Other public ranking routes protect anonymous identities
    Given a student chooses leaderboard anonymity
    And I'm not logged in
    Then public ranking routes do not disclose the anonymous student

  Scenario: A student can enable anonymity through their profile
    When the student enables leaderboard anonymity in their profile
    And I'm not logged in
    And I request global leaderboard page 0 of size 10
    Then the student is shown as "Anonymous student" without an identifying link
