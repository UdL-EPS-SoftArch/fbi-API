@US-EST-46
Feature: Configure my privacy settings
  As a student
  I want to control who sees my profile details and my name in leaderboards

  Background:
    Given There is a registered user with username "privacy-student" and password "password" and email "privacy-student@sample.app"

  Scenario: An existing student starts with a public profile and a visible leaderboard name
    Given I login as "privacy-student" with password "password"
    When I retrieve the profile of user "privacy-student"
    Then The response code is 200
    And the saved privacy settings are profile public true and leaderboard anonymous false

  Scenario: A newly registered student has the same privacy defaults
    Given I'm not logged in
    When I register "privacy-new-student" with email "privacy-new-student@sample.app" and password "password" without privacy preferences
    Then The response code is 201
    And A verification link is sent to "privacy-new-student@sample.app"
    When I open the emailed verification link
    Then The response code is 201
    Given I login as "privacy-new-student" with password "password"
    When I retrieve the profile of user "privacy-new-student"
    Then The response code is 200
    And the saved privacy settings are profile public true and leaderboard anonymous false

  Scenario: Another student cannot change my privacy settings
    Given I login as "demo" with password "password"
    When I set "profilePublic" to false for the profile of "privacy-student"
    Then The response code is 403
    When I set "anonymousInLeaderboards" to true for the profile of "privacy-student"
    Then The response code is 403
    Given I login as "privacy-student" with password "password"
    When I retrieve the profile of user "privacy-student"
    Then The response code is 200
    And the saved privacy settings are profile public true and leaderboard anonymous false

  Scenario: Changing profile visibility takes effect on the next read
    Given the profile of "privacy-student" has an avatar at "https://example.org/privacy-avatar.png"
    And I login as "demo" with password "password"
    When I retrieve the profile of user "privacy-student"
    Then The response code is 200
    And The profile field "profilePicture" is "https://example.org/privacy-avatar.png"
    Given I login as "privacy-student" with password "password"
    When I set "profilePublic" to false for the profile of "privacy-student"
    Then The response code is 200
    Given I login as "demo" with password "password"
    When I retrieve the profile of user "privacy-student"
    Then The response code is 200
    And the private profile displays only the public name "privacy-student"
    Given I login as "privacy-student" with password "password"
    When I retrieve the profile of user "privacy-student"
    Then The response code is 200
    And the saved privacy settings are profile public false and leaderboard anonymous false
    And The profile field "profilePicture" is "https://example.org/privacy-avatar.png"
    When I set "profilePublic" to true for the profile of "privacy-student"
    Then The response code is 200
    When I retrieve the profile of user "privacy-student"
    Then The response code is 200
    And the saved privacy settings are profile public true and leaderboard anonymous false
    Given I login as "demo" with password "password"
    When I retrieve the profile of user "privacy-student"
    Then The response code is 200
    And The profile field "profilePicture" is "https://example.org/privacy-avatar.png"

  Scenario Outline: Leaderboard display changes keep the same position and score
    Given privacy leaderboard entries exist for "privacy-student"
    And the privacy leaderboard viewer is "<viewer>"
    When I request the privacy leaderboard "<view>"
    Then The response code is 200
    And the leaderboard row at rank 2 with score <score> displays "privacy-student"
    Given I login as "privacy-student" with password "password"
    When I set "anonymousInLeaderboards" to true for the profile of "privacy-student"
    Then The response code is 200
    Given the privacy leaderboard viewer is "<viewer>"
    When I request the privacy leaderboard "<view>"
    Then The response code is 200
    And the leaderboard row at rank 2 with score <score> displays "Anonymous student"
    And that row does not identify the account of "privacy-student"
    Given I login as "privacy-student" with password "password"
    When I retrieve the profile of user "privacy-student"
    Then The response code is 200
    And the saved privacy settings are profile public true and leaderboard anonymous true
    When I set "anonymousInLeaderboards" to false for the profile of "privacy-student"
    Then The response code is 200
    When I retrieve the profile of user "privacy-student"
    Then The response code is 200
    And the saved privacy settings are profile public true and leaderboard anonymous false
    Given the privacy leaderboard viewer is "<viewer>"
    When I request the privacy leaderboard "<view>"
    Then The response code is 200
    And the leaderboard row at rank 2 with score <score> displays "privacy-student"

    Examples:
      | view    | viewer  | score |
      | subject | demo    | 80    |
      | global  | visitor | 180   |
      | home    | admin   | 180   |

  Scenario Outline: An anonymous row has no account reference in the standard leaderboard response
    Given privacy leaderboard entries exist for "privacy-student"
    And I login as "privacy-student" with password "password"
    When I set "anonymousInLeaderboards" to true for the profile of "privacy-student"
    Then The response code is 200
    Given I'm not logged in
    When I request the standard privacy leaderboard "<view>"
    Then The response code is 200
    And the leaderboard row at rank 2 with score <score> does not identify the account of "privacy-student"

    Examples:
      | view       | score |
      | subject    | 80    |
      | global     | 180   |
      | collection | 180   |
      | entry      | 180   |

  Scenario: An anonymous student sees their own name and can find their position outside the page
    Given privacy leaderboard entries exist for "privacy-student"
    And I login as "privacy-student" with password "password"
    When I set "anonymousInLeaderboards" to true for the profile of "privacy-student"
    Then The response code is 200
    When I set "profilePublic" to false for the profile of "privacy-student"
    Then The response code is 200
    When I request the privacy leaderboard "subject"
    Then The response code is 200
    And the leaderboard row at rank 2 with score 80 displays "privacy-student"
    When I request only the first row of the privacy subject leaderboard
    Then The response code is 200
    And the privacy subject leaderboard page contains only rank 1
    When I request my privacy leaderboard positions using my identity
    Then The response code is 200
    And the leaderboard row at rank 2 with score 80 displays "privacy-student"
    When I retrieve the profile of user "privacy-student"
    Then The response code is 200
    And the saved privacy settings are profile public false and leaderboard anonymous true
