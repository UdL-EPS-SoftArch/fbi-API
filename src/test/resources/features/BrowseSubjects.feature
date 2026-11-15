@US-COM-01
Feature: Browse subjects through the API
  Background:
    Given the subject catalogue contains public and non-public tests

  Scenario: A visitor lists subjects and their published test counts
    Given I'm not logged in
    When I request the subjects catalogue
    Then The response code is 200
    And the catalogue contains subject "Architecture" with description "Software design" and 2 published tests
    And the catalogue contains subject "Empty" with description "No tests" and 0 published tests

  Scenario: A visitor opens a subject
    Given I'm not logged in
    When I open subject "Architecture" from the catalogue
    Then The response code is 200
    And the subject detail has description "Software design" and links to published tests and leaderboard

  Scenario Outline: Published tests are public for every role
    Given the catalogue requester is "<role>"
    When I follow the published tests link for subject "Architecture"
    Then The response code is 200
    And the published tests are exactly "Official exam" of type "OFFICIAL" and "Custom exam" of type "CUSTOM"
    Examples:
      | role    |
      | visitor |
      | student |
      | admin   |

  Scenario: An empty subject has no published tests
    Given I'm not logged in
    When I follow the published tests link for subject "Empty"
    Then The response code is 200
    And the published tests collection is empty

  Scenario: The subject leaderboard contains only its entries
    Given I'm not logged in
    When I follow the leaderboard link for subject "Architecture"
    Then The response code is 200
    And the leaderboard contains only the entry for "Architecture"

  Scenario: Archived subjects are excluded from browsing
    Given a subject named "Archived" is archived
    And I'm not logged in
    When I request the subjects catalogue
    Then The response code is 200
    And the catalogue does not contain subject "Archived"
