@recovery
Feature: Recover a password
  As a registered user
  I want to recover access to my account

  Background:
    Given There is a registered user with username "recoveruser" and password "oldpassword" and email "recover@example.com"
    And I'm not logged in

  Scenario: Recovery requests do not reveal whether an account exists
    When I request password recovery for "recover@example.com"
    Then The response code is 201
    When I save the recovery response and request recovery for "unknown@example.com"
    Then The response code is 201
    And The recovery responses are identical

  Scenario Outline: Recovery requests are rate limited for any email
    When I request password recovery 6 times for "<email>"
    Then The response code is 429

    Examples:
      | email               |
      | recover@example.com |
      | unknown@example.com |

  Scenario: An unknown token cannot be validated
    When I validate the recovery token "unknown-token"
    Then The response code is 410

  Scenario: An unknown token cannot change a password
    When I confirm the recovery token "unknown-token" with password "newpassword"
    Then The response code is 410
    And I can login with username "recoveruser" and password "oldpassword"

  Scenario Outline: Invalid recovery request data is rejected
    When I request password recovery for "<email>"
    Then The response code is 400

    Examples:
      | email         |
      |               |
      | invalid-email |

  Scenario: An unknown token cannot be reused
    When I confirm the recovery token "unknown-token" with password "newpassword"
    Then The response code is 410
    When I confirm the recovery token "unknown-token" with password "anotherpassword"
    Then The response code is 410

  Scenario: A student resets a password without losing account data
    Given There is a registered user with username "studentreset" and password "oldpassword" and email "student-reset@example.com"
    When I request password recovery for "student-reset@example.com"
    Then The response code is 201
    And A recovery link is sent to "student-reset@example.com"
    When I validate the emailed recovery token
    Then The response code is 201
    When I confirm the emailed token with password "newpassword"
    Then The response code is 201
    And I cannot login with username "studentreset" and password "oldpassword"
    And I can login with username "studentreset" and password "newpassword"
    And The account "studentreset" has role "STUDENT" and email "student-reset@example.com"
    When I validate the emailed recovery token
    Then The response code is 410

  Scenario: An administrator resets a password
    Given There is a registered user with username "adminreset" and password "oldpassword" and email "admin-reset@example.com"
    And The account "adminreset" is an administrator
    When I request password recovery for "admin-reset@example.com"
    Then The response code is 201
    And A recovery link is sent to "admin-reset@example.com"
    When I confirm the emailed token with password "newpassword"
    Then The response code is 201
    And I cannot login with username "adminreset" and password "oldpassword"
    And I can login with username "adminreset" and password "newpassword"
    And The account "adminreset" has role "ADMIN" and email "admin-reset@example.com"

  Scenario: A link expires exactly one hour after it is issued
    Given There is a registered user with username "expiredreset" and password "oldpassword" and email "expired-reset@example.com"
    When I request password recovery for "expired-reset@example.com"
    Then A recovery link is sent to "expired-reset@example.com"
    When The recovery clock advances by 3600 seconds
    And I validate the emailed recovery token
    Then The response code is 410
    When I request password recovery for "expired-reset@example.com"
    Then The response code is 201
    And A recovery link is sent to "expired-reset@example.com"
    When I validate the emailed recovery token
    Then The response code is 201

  Scenario: An invalid new password does not consume the token
    Given There is a registered user with username "invalidreset" and password "oldpassword" and email "invalid-reset@example.com"
    When I request password recovery for "invalid-reset@example.com"
    Then A recovery link is sent to "invalid-reset@example.com"
    When I confirm the emailed token with password "short"
    Then The response code is 400
    When I confirm the emailed token with password "newpassword"
    Then The response code is 201

  Scenario: Only one simultaneous confirmation can use the link
    Given There is a registered user with username "concurrentreset" and password "oldpassword" and email "concurrent-reset@example.com"
    When I request password recovery for "concurrent-reset@example.com"
    Then A recovery link is sent to "concurrent-reset@example.com"
    When Two clients confirm the emailed token concurrently
    Then One confirmation succeeds and the other reports an expired token

  Scenario: No email is sent for an unknown account
    When I request password recovery for "nobody-reset@example.com"
    Then The response code is 201
    And No recovery email is sent to "nobody-reset@example.com"
