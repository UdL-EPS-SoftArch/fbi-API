@US-EST-42
Feature: Optional public profile details
  Background:
    Given There is a registered user with username "optional-profile" and password "password" and email "optional-profile@sample.app"
    And the optional profile of "optional-profile" has full name "Example Student"

  Scenario Outline: Personal details are private by default
    Given the optional profile viewer is "<viewer>"
    When I retrieve the profile of user "optional-profile"
    Then The response code is 200
    And The profile field "username" is "optional-profile"
    And The profile field "fullName" is hidden
    And The profile field "email" is hidden
    When I search users whose username contains "optional-profile"
    Then the optional profile search hides personal details

    Examples:
      | viewer  |
      | visitor |
      | demo    |

  Scenario Outline: The owner can publish and revoke each detail independently
    Given I login as "optional-profile" with password "password"
    When I retrieve the profile of user "optional-profile"
    Then the publication settings are show full name false and show email false
    When I set "<setting>" to true for the profile of "optional-profile"
    Then The response code is 200
    And the publication settings are show full name <showFullName> and show email <showEmail>
    Given I'm not logged in
    When I retrieve the profile of user "optional-profile"
    Then The response code is 200
    And The profile field "<visible>" is "<value>"
    And The profile field "<hidden>" is hidden
    Given I login as "optional-profile" with password "password"
    When I set "<setting>" to false for the profile of "optional-profile"
    Then The response code is 200
    And the publication settings are show full name false and show email false
    Given I'm not logged in
    When I retrieve the profile of user "optional-profile"
    Then The profile field "<visible>" is hidden
    And The profile field "<hidden>" is hidden

    Examples:
      | setting      | visible  | value                       | hidden   | showFullName | showEmail |
      | showFullName | fullName | Example Student             | email    | true         | false     |
      | showEmail    | email    | optional-profile@sample.app | fullName | false        | true      |

  Scenario Outline: Only the owner can change publication settings
    Given I login as "<viewer>" with password "password"
    When I set "<setting>" to true for the profile of "optional-profile"
    Then The response code is 403
    Given I'm not logged in
    When I retrieve the profile of user "optional-profile"
    Then The profile field "fullName" is hidden
    And The profile field "email" is hidden

    Examples:
      | setting      | viewer |
      | showFullName | demo   |
      | showEmail    | demo   |
      | showFullName | admin  |
      | showEmail    | admin  |

  Scenario: A private profile suppresses opted-in details
    Given I login as "optional-profile" with password "password"
    When I set "showFullName" to true for the profile of "optional-profile"
    Then The response code is 200
    When I set "showEmail" to true for the profile of "optional-profile"
    Then The response code is 200
    When I set "profilePublic" to false for the profile of "optional-profile"
    Then The response code is 200
    Given I'm not logged in
    When I retrieve the profile of user "optional-profile"
    Then The response code is 200
    And The profile field "fullName" is hidden
    And The profile field "email" is hidden
