@us-aut-05
Feature: Return to the previous point after authentication
  In order to continue what I was doing without searching for the content again
  As a visitor who has just logged in or registered
  I want to be sent back to the page or action from which I started authentication

  Background:
    Given There is a registered user with username "user" and password "password" and email "user@sample.app"
    And There is a published official test "Databases exam" with one question

  Scenario: Logging in after clicking "Take test" returns to that test's detail page
    Given I'm not logged in
    And I try to take the test "Databases exam"
    When I log in with "user@sample.app" and password "password" returning to the detail page of the test "Databases exam"
    Then The response code is 201
    And I am sent back to the detail page of the test "Databases exam"

  Scenario: Registering from a restricted action returns there after verifying and logging in
    Given I'm not logged in
    When I register a new user with username "newuser", email "newuser@sample.app" and password "password" returning to the detail page of the test "Databases exam"
    And A verification link is sent to "newuser@sample.app"
    And I open the emailed verification link
    And I start a session with "newuser@sample.app" and password "password"
    Then The response code is 201
    And I am sent back to the detail page of the test "Databases exam"

  Scenario Outline: A return address outside the platform is ignored
    Given I'm not logged in
    When I log in with "user@sample.app" and password "password" returning to "<returnTo>"
    Then The response code is 201
    And I am sent to the home page

    Examples:
      | returnTo                  |
      | https://evil.example.com  |
      | //evil.example.com        |
      | /\evil.example.com        |
      | javascript:alert(1)       |

  Scenario: Content that is no longer available sends me home with a notice
    Given I'm not logged in
    And The test "Databases exam" is no longer available
    When I log in with "user@sample.app" and password "password" returning to the detail page of the test "Databases exam"
    Then The response code is 201
    And I am sent to the home page
    And I am told "The content you were looking for is no longer available"

  Scenario: Logging in without a return address sends me home
    Given I'm not logged in
    When I start a session with "user@sample.app" and password "password"
    Then The response code is 201
    And I am sent to the home page
