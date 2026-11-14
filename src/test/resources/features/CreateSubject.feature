Feature: Create a subject
  In order to add subjects when they become part of the curriculum
  As an administrator
  I want to create a new subject with a name and a description

  Background:
    Given There is a subject with name "Human-Computer Interaction"

  Scenario: Administrator creates a subject with a trimmed name
    Given I login as "admin" with password "password"
    When I create a new subject with name "  Web Project  " and description "Full-stack web application project"
    Then The response code is 201
    And The subject "Web Project" exists and is active
    And The subject "Web Project" appears in the subjects list
    And The subject "Web Project" has an empty leaderboard

  Scenario: Cannot create a subject with a name already in use, ignoring case and spaces
    Given I login as "admin" with password "password"
    When I create a new subject with name " human-computer INTERACTION " and description "Duplicate"
    Then The response code is 409
    And There is only one subject named "Human-Computer Interaction"

  Scenario: Cannot create a subject with an empty name
    Given I login as "admin" with password "password"
    When I create a new subject with name "" and description "No name"
    Then The response code is 400
    And The error message is "must not be blank"

  Scenario: A student cannot create a subject
    Given I login as "demo" with password "password"
    When I create a new subject with name "Human-Computer Interaction" and description "Usability and interface design"
    Then The response code is 403
    And There is only one subject named "Human-Computer Interaction"

  Scenario: A visitor cannot create a subject
    Given I'm not logged in
    When I create a new subject with name "Human-Computer Interaction" and description "Usability and interface design"
    Then The response code is 401
    And There is only one subject named "Human-Computer Interaction"
