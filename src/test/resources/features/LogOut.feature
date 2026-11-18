Feature: Log out
  In order to protect my information when I stop using the platform
  As an authenticated user
  I want to log out and have my session invalidated on the server

  Background:
    Given There is a registered user with username "user" and password "password" and email "user@sample.app"

  Scenario: Logging in starts a session on the server
    When I start a session with "user@sample.app" and password "password"
    Then The response code is 201
    And I receive a session token
    When I request my identity with my session
    Then The response code is 200
    And I am logged in as "user" with role "STUDENT"

  Scenario: Logging out invalidates the session on the server
    Given I have a session as "user@sample.app" with password "password"
    When I log out
    Then The response code is 204
    And The browser is asked to clear its cache
    When I request my identity with my session
    Then The response code is 401
    And The error message is "Your session has ended"

  Scenario: Private responses are not stored in the browser cache
    Given I have a session as "user@sample.app" with password "password"
    When I request my identity with my session
    Then The response code is 200
    And The response must not be cached

  Scenario: A session token cannot start another session
    Given I have a session as "user@sample.app" with password "password"
    When I start a session with my session token
    Then The response code is 403

  Scenario: Logging out while taking a test keeps the attempt in progress
    Given There is a published official test "Exam" with one question
    And "user" has an attempt in progress on test "Exam"
    And I have a session as "user@sample.app" with password "password"
    When I log out
    And I have a session as "user@sample.app" with password "password"
    And I retrieve my attempt in progress with my session
    Then The response code is 200
    And The attempt is still in progress
