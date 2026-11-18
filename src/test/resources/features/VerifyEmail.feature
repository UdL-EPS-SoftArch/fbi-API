Feature: Verify my email address
  In order to prove that the account belongs to me and be able to recover it
  As a newly registered student
  I want to confirm my email address through a link

  Scenario: A new account receives a link and cannot log in until it is verified
    Given I'm not logged in
    When I register a new user with username "newuser", email "newuser@sample.app" and password "password"
    Then The response code is 201
    And A verification link is sent to "newuser@sample.app"
    When I log in with "newuser@sample.app" and password "password"
    Then The response code is 403
    And The error message is "Please verify your email address"

  Scenario: Opening the link verifies the account, and the link cannot be used again
    Given I'm not logged in
    And I register a new user with username "newuser", email "newuser@sample.app" and password "password"
    And A verification link is sent to "newuser@sample.app"
    When I open the emailed verification link
    Then The response code is 201
    When I log in with "newuser@sample.app" and password "password"
    Then The response code is 200
    When I open the emailed verification link
    Then The response code is 410

  Scenario: An expired link can be replaced by a new one
    Given I'm not logged in
    And I register a new user with username "newuser", email "newuser@sample.app" and password "password"
    And A verification link is sent to "newuser@sample.app"
    And 25 hours have passed
    When I open the emailed verification link
    Then The response code is 410
    And The error message is "VERIFICATION_LINK_EXPIRED"
    When I request a new verification link for "newuser@sample.app"
    Then The response code is 201
    And A verification link is sent to "newuser@sample.app"
    When I open the emailed verification link
    Then The response code is 201

  Scenario: Only the latest link works
    Given I'm not logged in
    And I register a new user with username "newuser", email "newuser@sample.app" and password "password"
    And A verification link is sent to "newuser@sample.app"
    And I request a new verification link for "newuser@sample.app"
    And A verification link is sent to "newuser@sample.app"
    When I open the first verification link
    Then The response code is 410
    When I open the emailed verification link
    Then The response code is 201

  Scenario: Requesting a link does not reveal which emails are registered or verified
    Given I'm not logged in
    When I request a new verification link for "nobody@sample.app"
    Then The response code is 201
    And No verification email is sent to "nobody@sample.app"
    When I request a new verification link for "demo@sample.app"
    Then The response code is 201
    And No verification email is sent to "demo@sample.app"

  Scenario: Unverified accounts older than 7 days are deleted automatically
    Given There is an unverified user "olduser" with email "olduser@sample.app" registered 8 days ago
    And There is an unverified user "recentuser" with email "recentuser@sample.app" registered 6 days ago
    And There is a verified user "veteran" with email "veteran@sample.app" registered 30 days ago
    When The daily cleanup of unverified accounts runs
    Then The user "olduser" no longer exists
    And The user "recentuser" still exists
    And The user "veteran" still exists
