Feature: Edit a subject
  In order to keep the subject data up to date
  As an administrator
  I want to edit the name and the description of an existing subject

  Scenario: Administrator edits the name and the description of a subject
    Given There is a subject with name "Networks"
    And I login as "admin" with password "password"
    When I edit the subject "Networks" to name "  Computer Networks  " and description "Protocols and layers"
    Then The response code is 200
    And The subject "Computer Networks" has description "Protocols and layers"
    And The subject "Computer Networks" appears in the subjects list
    And There is no subject named "Networks"

  Scenario: Cannot rename a subject to a name already in use, ignoring case and spaces
    Given There is a subject with name "Algorithms"
    And There is a subject with name "Graph Theory"
    And I login as "admin" with password "password"
    When I edit the subject "Algorithms" to name " graph THEORY " and description "Duplicate"
    Then The response code is 409
    And There is only one subject named "Graph Theory"
    And The subject "Algorithms" exists and is active

  Scenario: A subject can be edited keeping its own name
    Given There is a subject with name "Statistics"
    And I login as "admin" with password "password"
    When I edit the subject "Statistics" to name "Statistics" and description "Probability and inference"
    Then The response code is 200
    And The subject "Statistics" has description "Probability and inference"

  Scenario: Cannot edit a subject leaving its name empty
    Given There is a subject with name "Physics"
    And I login as "admin" with password "password"
    When I edit the subject "Physics" to name "" and description "No name"
    Then The response code is 400
    And The error message is "must not be blank"
    And The subject "Physics" exists and is active

  Scenario: Editing a subject does not affect its tests and leaderboard
    Given There is a subject with name "Operating Systems"
    And The subject "Operating Systems" has a test "OS Exam" and a leaderboard entry of 80 points for "demo"
    And I login as "admin" with password "password"
    When I edit the subject "Operating Systems" to name "Kernel Design" and description "Processes and memory"
    Then The response code is 200
    And The subject "Kernel Design" keeps its test "OS Exam" and its leaderboard entry of 80 points

  Scenario: The identifier of a subject cannot be changed
    Given There is a subject with name "Logic"
    And I login as "admin" with password "password"
    When I try to change the id of the subject "Logic"
    Then The subject "Logic" keeps its id

  Scenario: A student cannot edit a subject
    Given There is a subject with name "Chemistry"
    And I login as "demo" with password "password"
    When I edit the subject "Chemistry" to name "Organic Chemistry" and description "Not allowed"
    Then The response code is 403
    And The subject "Chemistry" exists and is active

  Scenario: A visitor cannot edit a subject
    Given There is a subject with name "Biology"
    And I'm not logged in
    When I edit the subject "Biology" to name "Marine Biology" and description "Not allowed"
    Then The response code is 401
    And The subject "Biology" exists and is active
