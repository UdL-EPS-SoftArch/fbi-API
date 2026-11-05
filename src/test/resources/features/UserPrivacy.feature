Feature: User privacy
  In order to protect personal data
  As a registered user
  I want my private profile fields and account to be protected from other users

  Background:
    Given There is a registered user with username "alice" and password "password" and email "alice@sample.app"

  Scenario: Another student cannot see private profile fields
    Given I login as "demo" with password "password"
    When I retrieve the profile of user "alice"
    Then The response code is 200
    And The profile field "username" is "alice"
    And The profile field "email" is hidden
    And The profile field "role" is hidden

  Scenario: The owner and administrators can see private profile fields
    Given I login as "admin" with password "password"
    When I retrieve the profile of user "alice"
    Then The response code is 200
    And The profile field "email" is "alice@sample.app"

  Scenario: Visitors cannot obtain emails through the user search
    Given I'm not logged in
    When I search users whose username contains "alice"
    Then The response code is 200
    And The user search returns 1 users without visible emails

  Scenario: Administrators are not discoverable by students
    Given I login as "demo" with password "password"
    When I search users whose username contains "admin"
    Then The user search returns 0 users without visible emails

  Scenario: A student cannot delete another user
    Given I login as "demo" with password "password"
    When I delete the user "alice"
    Then The response code is 403
    And The user "alice" still exists
