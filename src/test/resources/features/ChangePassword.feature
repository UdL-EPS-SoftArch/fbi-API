@change-password
Feature: Change my password

  Background:
    Given There is a registered user with username "changeuser" and password "oldpassword" and email "change@example.com"
    And I login as "changeuser" with password "oldpassword"

  Scenario Outline: A user changes their password
    Given The account "changeuser" is a "<role>" for password change
    When I change my password from "oldpassword" to "newpassword"
    Then The response code is 201
    And The password change response contains no passwords
    And I cannot login with username "changeuser" and password "oldpassword"
    And I can login with username "changeuser" and password "newpassword"
    And A password change notification is sent to "change@example.com"

    Examples:
      | role    |
      | STUDENT |
      | ADMIN   |

  Scenario Outline: Invalid password changes are rejected
    When I change my password from "<current>" to "<new>"
    Then The response code is 400
    And I can login with username "changeuser" and password "oldpassword"
    And No password change notification is sent

    Examples:
      | current     | new         |
      | wrong       | newpassword |
      | oldpassword | oldpassword |
      |             | newpassword |
      | oldpassword |             |
      | oldpassword | short       |

  Scenario: Anonymous users cannot change a password
    Given I'm not logged in
    When I change my password from "oldpassword" to "newpassword"
    Then The response code is 401

  Scenario Outline: Profile updates cannot change credentials
    When I attempt to change my password through a "<method>" profile update
    Then The profile password update is rejected
    And I can login with username "changeuser" and password "oldpassword"
    And I cannot login with username "changeuser" and password "newpassword"

    Examples:
      | method |
      | PATCH  |
      | PUT    |
