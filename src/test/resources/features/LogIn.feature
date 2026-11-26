Feature: Log in
  In order to access my features and personal data
  As a visitor with an existing account
  I want to log in with my email and password

  Background:
    Given There is a registered user with username "user" and password "password" and email "user@sample.app"

  Scenario: Log in with email and password
    When I log in with "user@sample.app" and password "password"
    Then The response code is 200
    And I am logged in as "user" with role "STUDENT"

  Scenario: An administrator logs in and gets the administrator role
    When I log in with "admin@sample.app" and password "password"
    Then The response code is 200
    And I am logged in as "admin" with role "ADMIN"

  Scenario: A wrong password gets a generic message
    When I log in with "user@sample.app" and password "wrongpassword"
    Then The response code is 401
    And The error message is "Incorrect email or password"

  Scenario: An unknown email gets the same generic message
    When I log in with "nobody@sample.app" and password "password"
    Then The response code is 401
    And The error message is "Incorrect email or password"

  Scenario: Five consecutive failed attempts throttle new attempts, even with the right password
    Given I failed to log in 5 times with "user@sample.app"
    When I log in with "user@sample.app" and password "password"
    Then The response code is 429
    And The error message is "Too many failed login attempts"

  Scenario: Failed attempts with the username and with the email count together
    Given I failed to log in 3 times with "user"
    And I failed to log in 2 times with "user@sample.app"
    When I log in with "user" and password "password"
    Then The response code is 429

  Scenario: A successful login resets the failed attempts
    Given I failed to log in 4 times with "user@sample.app"
    And I log in with "user@sample.app" and password "password"
    And I failed to log in 4 times with "user@sample.app"
    When I log in with "user@sample.app" and password "password"
    Then The response code is 200

  Scenario: The throttling expires after 15 minutes
    Given I failed to log in 5 times with "user@sample.app"
    And 15 minutes have passed
    When I log in with "user@sample.app" and password "password"
    Then The response code is 200

  Scenario: An unverified account is asked to verify its email
    Given The account "user" has not verified its email
    When I log in with "user@sample.app" and password "password"
    Then The response code is 403
    And The error message is "Please verify your email address"

  Scenario: A suspended account is told that it is suspended
    Given The account "user" is suspended
    When I log in with "user@sample.app" and password "password"
    Then The response code is 403
    And The error message is "Your account is suspended"

  Scenario: The account status is not revealed with a wrong password
    Given The account "user" is suspended
    When I log in with "user@sample.app" and password "wrongpassword"
    Then The response code is 401
    And The error message is "Incorrect email or password"
